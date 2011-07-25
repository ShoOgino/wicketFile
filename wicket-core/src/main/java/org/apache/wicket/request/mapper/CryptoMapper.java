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
		this.wrappedMapper = wrappedMapper;
		this.cryptProvider = cryptProvider;
	}

	public int getCompatibilityScore(final Request request)
	{
		return 0;
	}

	public Url mapHandler(final IRequestHandler requestHandler)
	{
		final Url url = wrappedMapper.mapHandler(requestHandler);

		if (url == null)
		{
			return null;
		}

		return encryptUrl(url);
	}

	public IRequestHandler mapRequest(final Request request)
	{
		Url url = decryptUrl(request, request.getUrl());

		if (url == null)
		{
			return null;
		}

		return wrappedMapper.mapRequest(request.cloneWithUrl(url));
	}

	private ICrypt getCrypt()
	{
		return cryptProvider.get();
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
		if (numberOfSegments == 0 && !url.getQueryParameters().isEmpty())
		{
			numberOfSegments = 1;
		}
		char[] encryptedChars = encryptedUrlString.toCharArray();
		int hash = 0;
		for (int segNo = 0; segNo < numberOfSegments; segNo++)
		{
			char a = encryptedChars[Math.abs(hash % encryptedChars.length)];
			hash++;
			char b = encryptedChars[Math.abs(hash % encryptedChars.length)];
			hash++;
			char c = encryptedChars[Math.abs(hash % encryptedChars.length)];

			String segment = "" + a + b + c;
			hash = hashString(segment);

			segment += String.format("%02x", Math.abs(hash % 256));
			encryptedUrl.getSegments().add(segment);
			hash = hashString(segment);
		}
		return encryptedUrl;
	}

	private Url decryptUrl(final Request request, final Url encryptedUrl)
	{
		if (encryptedUrl.getSegments().isEmpty() && encryptedUrl.getQueryParameters().isEmpty())
		{
			return encryptedUrl;
		}

		List<String> segments = encryptedUrl.getSegments();
		if (segments.size() < 2)
		{
			return null;
		}

		Url url = new Url(request.getCharset());
		try
		{
			String encryptedUrlString = segments.get(0);
			if (Strings.isEmpty(encryptedUrlString))
			{
				return null;
			}

			String decryptedUrl = getCrypt().decryptUrlSafe(encryptedUrlString);
			Url originalUrl = Url.parse(decryptedUrl, request.getCharset());

			int originalNumberOfSegments = originalUrl.getSegments().size();
			if (originalNumberOfSegments == 0 &&
				originalUrl.getQueryParameters().isEmpty() == false)
			{
				originalNumberOfSegments = 1;
			}
			int numberOfSegments = encryptedUrl.getSegments().size();

			char[] encryptedChars = encryptedUrlString.toCharArray();
			int hash = 0;

			int segNo;
			for (segNo = 1; segNo < numberOfSegments && segNo < originalNumberOfSegments + 1; segNo++)
			{
				char a = encryptedChars[Math.abs(hash % encryptedChars.length)];
				hash++;
				char b = encryptedChars[Math.abs(hash % encryptedChars.length)];
				hash++;
				char c = encryptedChars[Math.abs(hash % encryptedChars.length)];

				String segment = "" + a + b + c;
				hash = hashString(segment);

				segment += String.format("%02x", Math.abs(hash % 256));
				hash = hashString(segment);

				if (segment.equals(segments.get(segNo)) &&
					originalUrl.getSegments().size() >= segNo)
				{
					url.getSegments().add(originalUrl.getSegments().get(segNo - 1));
				}
				else
				{
					break;
				}
			}

			if (segNo < numberOfSegments)
			{
				url.getQueryParameters().addAll(originalUrl.getQueryParameters());

				for (; segNo < numberOfSegments; segNo++)
				{
					url.getSegments().add(encryptedUrl.getSegments().get(segNo));
				}
			}
			else
			{
				url.getQueryParameters().addAll(originalUrl.getQueryParameters());
			}
		}
		catch (Exception e)
		{
			log.error("Error decrypting URL", e);
			url = null;
		}

		return url;
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

	private static class ApplicationCryptProvider implements IProvider<ICrypt>
	{
		private final Application application;

		public ApplicationCryptProvider(final Application application)
		{
			this.application = application;
		}

		public ICrypt get()
		{
			return application.getSecuritySettings().getCryptFactory().newCrypt();
		}
	}

}
