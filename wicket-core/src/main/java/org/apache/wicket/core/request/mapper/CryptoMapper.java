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
package org.apache.wicket.core.request.mapper;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.apache.wicket.Application;
import org.apache.wicket.core.request.handler.RequestSettingRequestHandler;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.IRequestMapperDelegate;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.crypt.ICryptFactory;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A request mapper that encrypts URLs generated by another mapper. This mapper encrypts the segments
 * and query parameters of URLs starting with {@link IMapperContext#getNamespace()}, and just the
 * {@link PageComponentInfo} parameter for mounted URLs.
 * </p>
 *
 * <p>
 * <strong>Important</strong>: for better security it is recommended to use
 * {@link org.apache.wicket.core.request.mapper.CryptoMapper#CryptoMapper(org.apache.wicket.request.IRequestMapper, org.apache.wicket.util.Supplier)}
 * constructor with {@link org.apache.wicket.util.crypt.ICrypt} implementation that generates a
 * separate key for each user. {@link org.apache.wicket.core.util.crypt.KeyInSessionSunJceCryptFactory} provides such an
 * implementation that stores the key in the HTTP session.
 * </p>
 * 
 * <p>
 * This mapper can be mounted before or after mounting other pages, but will only encrypt URLs for
 * pages mounted before the {@link CryptoMapper}. If required, multiple {@link CryptoMapper}s may be
 * installed in an {@link Application}.
 * </p>
 * 
 * <p>
 * When encrypting URLs in the Wicket namespace (starting with {@link IMapperContext#getNamespace()}), the entire URL,
 * including segments and parameters, is encrypted, with the encrypted form stored in the first segment of the encrypted URL.
 * </p>
 * 
 * <p>
 * To be able to handle relative URLs, like for image URLs in a CSS file, checksum segments are appended to the
 * encrypted URL until the encrypted URL has the same number of segments as the original URL had.
 * Each checksum segment has a precise 5 character value, calculated using a checksum. This helps in calculating
 * the relative distance from the original URL. When a URL is returned by the browser, we iterate through these
 * checksummed placeholder URL segments. If the segment matches the expected checksum, then the segment is deemed
 * to be the corresponding segment in the original URL. If the segment does not match the expected checksum, then
 * the segment is deemed a plain text sibling of the corresponding segment in the original URL, and all subsequent
 * segments are considered plain text children of the current segment.
 * </p>
 * 
 * <p>
 * When encrypting mounted URLs, we look for the {@link PageComponentInfo} parameter, and encrypt only that parameter.
 * </p>
 * 
 * <p>
 * {@link CryptoMapper} can be configured to mark encrypted URLs as encrypted, and throw a {@link PageExpiredException}
 * exception if a encrypted URL cannot be decrypted. This can occur when using {@code KeyInSessionSunJceCryptFactory}, and
 * the session has expired.
 * </p>
 * 
 * @author igor.vaynberg
 * @author Jesse Long
 * @author svenmeier
 * @see org.apache.wicket.settings.SecuritySettings#setCryptFactory(org.apache.wicket.util.crypt.ICryptFactory)
 * @see org.apache.wicket.core.util.crypt.KeyInSessionSunJceCryptFactory
 * @see org.apache.wicket.util.crypt.SunJceCrypt
 */
public class CryptoMapper implements IRequestMapperDelegate
{
	private static final Logger log = LoggerFactory.getLogger(CryptoMapper.class);

	/**
	 * Name of the parameter which contains encrypted page component info.
	 */
	private static final String ENCRYPTED_PAGE_COMPONENT_INFO_PARAMETER = "wicket-crypt";

	private static final String ENCRYPTED_URL_MARKER_PREFIX = "crypt.";

	private final IRequestMapper wrappedMapper;
	private final Supplier<ICrypt> cryptProvider;

	/**
	 * Whether or not to mark encrypted URLs as encrypted.
	 */
	private boolean markEncryptedUrls = false;

	/**
	 * Encrypt with {@link org.apache.wicket.settings.SecuritySettings#getCryptFactory()}.
	 * <p>
	 * <strong>Important</strong>: Encryption is done with {@link org.apache.wicket.settings.SecuritySettings#DEFAULT_ENCRYPTION_KEY} if you haven't
	 * configured an alternative {@link ICryptFactory}. For better security it is recommended to use
	 * {@link CryptoMapper#CryptoMapper(IRequestMapper, Supplier)} with a specific {@link ICrypt} implementation
	 * that generates a separate key for each user.
	 * {@link org.apache.wicket.core.util.crypt.KeyInSessionSunJceCryptFactory} provides such an implementation that stores the
	 * key in the HTTP session.
	 * </p>
	 *
	 * @param wrappedMapper
	 *            the non-crypted request mapper
	 * @param application
	 *            the current application
	 * @see org.apache.wicket.util.crypt.SunJceCrypt
	 */
	public CryptoMapper(final IRequestMapper wrappedMapper, final Application application)
	{
		this(wrappedMapper, () -> application.getSecuritySettings().getCryptFactory().newCrypt());
	}

	/**
	 * Construct.
	 * 
	 * @param wrappedMapper
	 *            the non-crypted request mapper
	 * @param cryptProvider
	 *            the custom crypt provider
	 */
	public CryptoMapper(final IRequestMapper wrappedMapper, final Supplier<ICrypt> cryptProvider)
	{
		this.wrappedMapper = Args.notNull(wrappedMapper, "wrappedMapper");
		this.cryptProvider = Args.notNull(cryptProvider, "cryptProvider");
	}

	/**
	 * Whether or not to mark encrypted URLs as encrypted. If set, a {@link PageExpiredException} is thrown when
	 * a encrypted URL can no longer be decrypted.
	 * 
	 * @return whether or not to mark encrypted URLs as encrypted.
	 */
	public boolean getMarkEncryptedUrls()
	{
		return markEncryptedUrls;
	}

	/**
	 * Sets whether or not to mark encrypted URLs as encrypted. If set, a {@link PageExpiredException} is thrown when
	 * a encrypted URL can no longer be decrypted.
	 * 
	 * @param markEncryptedUrls
	 *		whether or not to mark encrypted URLs as encrypted.
	 * 
	 * @return {@code this}, for chaining.
	 */
	public CryptoMapper setMarkEncryptedUrls(boolean markEncryptedUrls)
	{
		this.markEncryptedUrls = markEncryptedUrls;
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation decrypts the URL and passes the decrypted URL to the wrapped mapper.
	 * </p>
	 * @param request
	 *		The request for which to get a compatibility score.
	 * 
	 * @return The compatibility score.
	 */
	@Override
	public int getCompatibilityScore(final Request request)
	{
		Url decryptedUrl = decryptUrl(request, request.getUrl());

		if (decryptedUrl == null)
		{
			return 0;
		}

		Request decryptedRequest = request.cloneWithUrl(decryptedUrl);

		return wrappedMapper.getCompatibilityScore(decryptedRequest);
	}

	@Override
	public Url mapHandler(final IRequestHandler requestHandler)
	{
		final Url url = wrappedMapper.mapHandler(requestHandler);

		if (url == null)
		{
			return null;
		}

		if (url.isFull())
		{
			// do not encrypt full urls
			return url;
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

		Request decryptedRequest = request.cloneWithUrl(url);

		IRequestHandler handler = wrappedMapper.mapRequest(decryptedRequest);

		if (handler != null)
		{
			handler = new RequestSettingRequestHandler(decryptedRequest, handler);
		}

		return handler;
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
	@Override
	public final IRequestMapper getDelegateMapper()
	{
		return wrappedMapper;
	}

	/**
	 * Returns the applications {@link IMapperContext}.
	 *
	 * @return The applications {@link IMapperContext}.
	 */
	protected IMapperContext getContext()
	{
		return Application.get().getMapperContext();
	}

	/**
	 * Encrypts a URL. This method should return a new, encrypted instance of the URL. If the URL starts with {@code /wicket/},
	 * the entire URL is encrypted.
	 * 
	 * @param url
	 *		The URL to encrypt.
	 * 
	 * @return A new, encrypted version of the URL.
	 */
	protected Url encryptUrl(final Url url)
	{
		if (url.getSegments().size() > 0
			&& url.getSegments().get(0).equals(getContext().getNamespace()))
		{
			return encryptEntireUrl(url);
		}
		else
		{
			return encryptRequestListenerParameter(url);
		}
	}

	/**
	 * Encrypts an entire URL, segments and query parameters.
	 * 
	 * @param url
	 *		The URL to encrypt.
	 * 
	 * @return An encrypted form of the URL.
	 */
	protected Url encryptEntireUrl(final Url url)
	{
		String encryptedUrlString = getCrypt().encryptUrlSafe(url.toString());

		Url encryptedUrl = new Url(url.getCharset());

		if (getMarkEncryptedUrls())
		{
			encryptedUrl.getSegments().add(ENCRYPTED_URL_MARKER_PREFIX + encryptedUrlString);
		}
		else
		{
			encryptedUrl.getSegments().add(encryptedUrlString);
		}

		int numberOfSegments = url.getSegments().size() - 1;
		HashedSegmentGenerator generator = new HashedSegmentGenerator(encryptedUrlString);
		for (int segNo = 0; segNo < numberOfSegments; segNo++)
		{
			encryptedUrl.getSegments().add(generator.next());
		}
		return encryptedUrl;
	}

	/**
	 * Encrypts the {@link PageComponentInfo} query parameter in the URL, if any is found.
	 * 
	 * @param url
	 *		The URL to encrypt.
	 * 
	 * @return An encrypted form of the URL.
	 */
	protected Url encryptRequestListenerParameter(final Url url)
	{
		Url encryptedUrl = new Url(url);
		boolean encrypted = false;

		for (Iterator<Url.QueryParameter> it = encryptedUrl.getQueryParameters().iterator(); it.hasNext();)
		{
			Url.QueryParameter qp = it.next();

			if (MapperUtils.parsePageComponentInfoParameter(qp) != null)
			{
				it.remove();
				String encryptedParameterValue = getCrypt().encryptUrlSafe(qp.getName());
				Url.QueryParameter encryptedParameter
					= new Url.QueryParameter(ENCRYPTED_PAGE_COMPONENT_INFO_PARAMETER, encryptedParameterValue);
				encryptedUrl.getQueryParameters().add(0, encryptedParameter);
				encrypted = true;
				break;
			}
		}

		if (encrypted)
		{
			return encryptedUrl;
		}
		else
		{
			return url;
		}
	}

	/**
	 * Decrypts a {@link Url}. This method should return {@code null} if the URL is not decryptable, or if the
	 * URL should have been encrypted but was not. Returning {@code null} results in a 404 error.
	 * 
	 * @param request
	 *		The {@link Request}.
	 * @param encryptedUrl
	 *		The encrypted {@link Url}.
	 * 
	 * @return Returns a decrypted {@link Url}.
	 */
	protected Url decryptUrl(final Request request, final Url encryptedUrl)
	{
		Url url = decryptEntireUrl(request, encryptedUrl);

		if (url == null)
		{
			if (encryptedUrl.getSegments().size() > 0
				&& encryptedUrl.getSegments().get(0).equals(getContext().getNamespace()))
			{
				/*
				 * This URL should have been encrypted, but was not. We should refuse to handle this, except when
				 * there is more than one CryptoMapper installed, and the request was decrypted by some other
				 * CryptoMapper.
				 */
				if (request.getOriginalUrl().getSegments().size() > 0
					&& request.getOriginalUrl().getSegments().get(0).equals(getContext().getNamespace()))
				{
					return null;
				}
				else
				{
					return encryptedUrl;
				}
			}
		}

		if (url == null)
		{
			url = decryptRequestListenerParameter(request, encryptedUrl);
		}

		return url;
	}

	/**
	 * Decrypts an entire URL, which was previously encrypted by {@link #encryptEntireUrl(org.apache.wicket.request.Url)}.
	 * This method should return {@code null} if the URL is not decryptable.
	 * 
	 * @param request
	 *		The request that was made.
	 * @param encryptedUrl
	 *		The encrypted URL.
	 * 
	 * @return A decrypted form of the URL, or {@code null} if the URL is not decryptable.
	 */
	protected Url decryptEntireUrl(final Request request, final Url encryptedUrl)
	{
		Url url = new Url(request.getCharset());

		List<String> encryptedSegments = encryptedUrl.getSegments();

		if (encryptedSegments.isEmpty())
		{
			return null;
		}

		/*
		 * The first encrypted segment contains an encrypted version of the entire plain text url.
		 */
		String encryptedUrlString = encryptedSegments.get(0);
		if (Strings.isEmpty(encryptedUrlString))
		{
			return null;
		}

		if (getMarkEncryptedUrls())
		{
			if (encryptedUrlString.startsWith(ENCRYPTED_URL_MARKER_PREFIX))
			{
				encryptedUrlString = encryptedUrlString.substring(ENCRYPTED_URL_MARKER_PREFIX.length());
			}
			else
			{
				return null;
			}
		}

		String decryptedUrl;
		try
		{
			decryptedUrl = getCrypt().decryptUrlSafe(encryptedUrlString);
		}
		catch (Exception e)
		{
			log.error("Error decrypting URL", e);
			return null;
		}

		if (decryptedUrl == null)
		{
			if (getMarkEncryptedUrls())
			{
				throw new PageExpiredException("Encrypted URL is no longer decryptable");
			}
			else
			{
				return null;
			}
		}

		Url originalUrl = Url.parse(decryptedUrl, request.getCharset());

		int originalNumberOfSegments = originalUrl.getSegments().size();
		int encryptedNumberOfSegments = encryptedUrl.getSegments().size();

		if (originalNumberOfSegments > 0)
		{
			/*
			 * This should always be true. Home page URLs are the only ones without
			 * segments, and we don't encrypt those with this method.
			 * 
			 * We always add the first segment of the URL, because we encrypt a URL like:
			 *	/path/to/something
			 * to:
			 *	/encrypted_full/hash/hash
			 * 
			 * Notice the consistent number of segments. If we applied the following relative URL:
			 *	../../something
			 * then the resultant URL would be:
			 *	/something
			 * 
			 * Hence, the mere existence of the first, encrypted version of complete URL, segment
			 * tells us that the first segment of the original URL is still to be used.
			 */
			url.getSegments().add(originalUrl.getSegments().get(0));
		}

		HashedSegmentGenerator generator = new HashedSegmentGenerator(encryptedUrlString);
		int segNo = 1;
		for (; segNo < encryptedNumberOfSegments; segNo++)
		{
			if (segNo > originalNumberOfSegments)
			{
				break;
			}

			String next = generator.next();
			String encryptedSegment = encryptedSegments.get(segNo);
			if (!next.equals(encryptedSegment))
			{
				/*
				 * This segment received from the browser is not the same as the expected segment generated
				 * by the HashSegmentGenerator. Hence it, and all subsequent segments are considered plain
				 * text siblings of the original encrypted url.
				 */
				break;
			}

			/*
			 * This segments matches the expected checksum, so we add the corresponding segment from the
			 * original URL.
			 */
			url.getSegments().add(originalUrl.getSegments().get(segNo));
		}
		/*
		 * Add all remaining segments from the encrypted url as plain text segments.
		 */
		for (; segNo < encryptedNumberOfSegments; segNo++)
		{
			// modified or additional segment
			url.getSegments().add(encryptedUrl.getSegments().get(segNo));
		}

		url.getQueryParameters().addAll(originalUrl.getQueryParameters());
		// WICKET-4923 additional parameters
		url.getQueryParameters().addAll(encryptedUrl.getQueryParameters());

		return url;
	}

	/**
	 * Decrypts a URL which may contain an encrypted {@link PageComponentInfo} query parameter.
	 * 
	 * @param request
	 *		The request that was made.
	 * @param encryptedUrl
	 *		The (potentially) encrypted URL.
	 * 
	 * @return A decrypted form of the URL.
	 */
	protected Url decryptRequestListenerParameter(final Request request, Url encryptedUrl)
	{
		Url url = new Url(encryptedUrl);

		url.getQueryParameters().clear();

		for (Url.QueryParameter qp : encryptedUrl.getQueryParameters())
		{
			if (MapperUtils.parsePageComponentInfoParameter(qp) != null)
			{
				/*
				 * Plain text request listener parameter found. This should have been encrypted, so we
				 * refuse to map the request unless the original URL did not include this parameter, which
				 * case there are likely to be multiple cryptomappers installed.
				 */
				if (request.getOriginalUrl().getQueryParameter(qp.getName()) == null)
				{
					url.getQueryParameters().add(qp);
				}
				else
				{
					return null;
				}
			}
			else if (ENCRYPTED_PAGE_COMPONENT_INFO_PARAMETER.equals(qp.getName()))
			{
				String encryptedValue = qp.getValue();

				if (Strings.isEmpty(encryptedValue))
				{
					url.getQueryParameters().add(qp);
				}
				else
				{
					String decryptedValue = null;

					try
					{
						decryptedValue = getCrypt().decryptUrlSafe(encryptedValue);
					}
					catch (Exception e)
					{
						log.error("Error decrypting encrypted request listener query parameter", e);
					}

					if (Strings.isEmpty(decryptedValue))
					{
						url.getQueryParameters().add(qp);
					}
					else
					{
						Url.QueryParameter decryptedParamter = new Url.QueryParameter(decryptedValue, "");
						url.getQueryParameters().add(0, decryptedParamter);
					}
				}
			}
			else
			{
				url.getQueryParameters().add(qp);
			}
		}

		return url;
	}

	/**
	 * A generator of hashed segments.
	 */
	public static class HashedSegmentGenerator
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

		public int hashString(final String str)
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
