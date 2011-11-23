/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.request.mapper;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.IProvider;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request mapper that encrypts urls generated by another mapper. The original URL (both segments
 * and parameters) is encrypted and is represented as URL segment. To be able to handle relative
 * URLs for images in .css file the same amount of URL segments that the original URL had are
 * appended to the encrypted URL. Each segment has a precise 5 character value, calculated using a
 * checksum. This helps in calculating the relative distance from the original URL. When a URL is
 * returned by the browser, we iterate through these checksummed placeholder URL segments. If the
 * segment matches the expected checksum, then the segment it deemed to be the corresponding segment
 * in the encrypted URL. If the segment does not match the expected checksum, then the segment is
 * deemed a plain text sibling of the corresponding segment in the encrypted URL, and all subsequent
 * segments are considered plain text children of the current segment.
 * 
 * 
 * @author igor.vaynberg
 * @author Jesse Long
 * @author svenmeier
 */
public class CryptoMapper implements IRequestMapper
{
	private static final Logger log = LoggerFactory.getLogger(CryptoMapper.class);

	private final IRequestMapper wrappedMapper;
	private final IProvider<ICrypt> cryptProvider;

	/**
	 * Construct.
	 * 
	 * @param wrappedMapper
	 *            the non-crypted request mapper
	 * @param application
	 *            the current application
	 */
	public CryptoMapper(final IRequestMapper wrappedMapper, final Application application)
	{
		this(wrappedMapper, new ApplicationCryptProvider(application));
	}

	/**
	 * Construct.
	 * 
	 * @param wrappedMapper
	 *            the non-crypted request mapper
	 * @param cryptProvider
	 *            the custom crypt provider
	 */
	public CryptoMapper(final IRequestMapper wrappedMapper, final IProvider<ICrypt> cryptProvider)
	{
		this.wrappedMapper = Args.notNull(wrappedMapper, "wrappedMapper");
		this.cryptProvider = Args.notNull(cryptProvider, "cryptProvider");
	}

	@Override
	public int getCompatibilityScore(final Request request)
	{
		return 0;
	}

	@Override
	public Url mapHandler(final IRequestHandler requestHandler)
	{
		final Url url = wrappedMapper.mapHandler(requestHandler);

		if (url == null)
		{
			return null;
		}

		return encryptUrl(url);
	}

	@Override
	public IRequestHandler mapRequest(final Request request)
	{
		Url url = decryptUrl(request, request.getUrl());

		if (url == null)
		{
			return null;
		}

		return wrappedMapper.mapRequest(request.cloneWithUrl(url));
	}

	/**
	 * @return the {@link ICrypt} implementation that may be used to encrypt/decrypt {@link Url}'s
	 *         segments and/or query string
	 */
	protected final ICrypt getCrypt()
	{
		return cryptProvider.get();
	}

	/**
	 * @return the wrapped root request mapper
	 */
	protected final IRequestMapper getWrappedMapper()
	{
		return wrappedMapper;
	}

	private Url encryptUrl(final Url url)
	{
		if (url.getSegments().isEmpty() && url.getQueryParameters().isEmpty())
		{
			return url;
		}
		String encryptedUrlString = getCrypt().encryptUrlSafe(url.toString());

		Url encryptedUrl = new Url(url.getCharset());
		encryptedUrl.getSegments().add(encryptedUrlString);

		int numberOfSegments = url.getSegments().size();
		HashedSegmentGenerator generator = new HashedSegmentGenerator(encryptedUrlString);
		for (int segNo = 0; segNo < numberOfSegments; segNo++)
		{
			encryptedUrl.getSegments().add(generator.next());
		}
		return encryptedUrl;
	}

	private Url decryptUrl(final Request request, final Url encryptedUrl)
	{
		if (encryptedUrl.getSegments().isEmpty() && encryptedUrl.getQueryParameters().isEmpty())
		{
			return encryptedUrl;
		}

		List<String> encryptedSegments = encryptedUrl.getSegments();
		if (encryptedSegments.size() < 1)
		{
			return null;
		}

		Url url = new Url(request.getCharset());
		try
		{
			String encryptedUrlString = encryptedSegments.get(0);
			if (Strings.isEmpty(encryptedUrlString))
			{
				return null;
			}

			String decryptedUrl = getCrypt().decryptUrlSafe(encryptedUrlString);
			if (decryptedUrl == null)
			{
				return null;
			}
			Url originalUrl = Url.parse(decryptedUrl, request.getCharset());

			int originalNumberOfSegments = originalUrl.getSegments().size();
			int encryptedNumberOfSegments = encryptedUrl.getSegments().size();

			HashedSegmentGenerator generator = new HashedSegmentGenerator(encryptedUrlString);
			int segNo = 1;
			for (; segNo < encryptedNumberOfSegments; segNo++)
			{
				if (segNo > originalNumberOfSegments ||
					!generator.next().equals(encryptedSegments.get(segNo)))
				{
					break;
				}

				// unmodified segment
				url.getSegments().add(originalUrl.getSegments().get(segNo - 1));
			}
			for (; segNo < encryptedNumberOfSegments; segNo++)
			{
				// modified or additional segment
				url.getSegments().add(encryptedUrl.getSegments().get(segNo));
			}

			url.getQueryParameters().addAll(originalUrl.getQueryParameters());
		}
		catch (Exception e)
		{
			log.error("Error decrypting URL", e);
			url = null;
		}

		return url;
	}

	private static class ApplicationCryptProvider implements IProvider<ICrypt>
	{
		private final Application application;

		public ApplicationCryptProvider(final Application application)
		{
			this.application = application;
		}

		@Override
		public ICrypt get()
		{
			return application.getSecuritySettings().getCryptFactory().newCrypt();
		}
	}

	/**
	 * A generator of hashed segments.
	 */
	private static class HashedSegmentGenerator
	{
		private char[] characters;

		private int hash = 0;

		public HashedSegmentGenerator(String string)
		{
			characters = string.toCharArray();
		}

		/**
		 * Generate the next segment
		 * 
		 * @return segment
		 */
		public String next()
		{
			char a = characters[Math.abs(hash % characters.length)];
			hash++;
			char b = characters[Math.abs(hash % characters.length)];
			hash++;
			char c = characters[Math.abs(hash % characters.length)];

			String segment = "" + a + b + c;
			hash = hashString(segment);

			segment += String.format("%02x", Math.abs(hash % 256));
			hash = hashString(segment);

			return segment;
		}

		private int hashString(final String str)
		{
			int hash = 97;

			for (char c : str.toCharArray())
			{
				int i = c;
				hash = 47 * hash + i;
			}

			return hash;
		}
	}
}