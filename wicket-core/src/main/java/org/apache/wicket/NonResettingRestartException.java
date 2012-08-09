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
package org.apache.wicket;

import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.protocol.http.IMetaDataBufferingWebResponse;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.RequestHandlerStack;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Causes Wicket to interrupt current request processing and immediately respond with the specified
 * page. Does not reset the header meta data.
 *
 * @see RestartResponseException
 */
public class NonResettingRestartException extends RequestHandlerStack.ReplaceHandlerException
{

	/**
	 * Constructor.
	 *
	 * @param pageClass
	 *      the class of the new page that should be rendered
	 * @param params
	 *      the page parameters to use for the new page
	 * @param cycle
	 *      the request cycle to use to find the Response`s
	 */
	public NonResettingRestartException(final Class<? extends Page> pageClass,
			final PageParameters params, final RequestCycle cycle)
	{
		this(pageClass, params, cycle, RenderPageRequestHandler.RedirectPolicy.AUTO_REDIRECT);
	}

	/**
	 * Constructor.
	 *
	 * @param pageClass
	 *      the class of the new page that should be rendered
	 * @param params
	 *      the page parameters to use for the new page
	 * @param cycle
	 *      the request cycle to use to find the Response`s
	 * @param redirectPolicy
	 *      the policy that mandates whether to do a redirect
	 */
	public NonResettingRestartException(final Class<? extends Page> pageClass,
		final PageParameters params, final RequestCycle cycle, RenderPageRequestHandler.RedirectPolicy redirectPolicy)
	{
		super(createRequestHandler(pageClass, params, redirectPolicy), true);

		Response response = cycle.getResponse();
		if (response instanceof IMetaDataBufferingWebResponse)
		{
			WebResponse originalResponse = (WebResponse) cycle.getOriginalResponse();
			if (originalResponse != response)
			{
				IMetaDataBufferingWebResponse bufferingWebResponse = (IMetaDataBufferingWebResponse) response;
				bufferingWebResponse.writeMetaData(originalResponse);
			}
		}
	}

	private static IRequestHandler createRequestHandler(Class<? extends Page> pageClass, PageParameters params,
			RenderPageRequestHandler.RedirectPolicy redirectPolicy)
	{
		return new RenderPageRequestHandler(new PageProvider(pageClass, params), redirectPolicy);
	}
} 