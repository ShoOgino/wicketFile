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
package org.apache.wicket.core.util.string;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.BufferedWebResponse;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.handler.render.PageRenderer;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

/**
 * A helper class for rendering components and pages
 */
public class ComponentRenderer
{
	/**
	 * The id to use when rendering a component
	 */
	public static final String COMP_ID = "compId";

	/**
	 * Collects the html generated by the rendering of a page.
	 *
	 * @param pageProvider
	 *            the provider of the page class/instance and its parameters
	 * @return the html rendered by a page
	 */
	public static CharSequence renderPage(final PageProvider pageProvider)
	{
		final RenderPageRequestHandler handler = new RenderPageRequestHandler(pageProvider,
				RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT);

		Application application = Application.get();
		final PageRenderer pageRenderer = application.getPageRendererProvider().get(handler);

		RequestCycle originalRequestCycle = RequestCycle.get();

		BufferedWebResponse tempResponse = new BufferedWebResponse(null);

		RequestCycleContext requestCycleContext = new RequestCycleContext(originalRequestCycle.getRequest(),
				tempResponse, application.getRootRequestMapper(), application.getExceptionMapperProvider().get());
		RequestCycle tempRequestCycle = new RequestCycle(requestCycleContext);

		final Response oldResponse = originalRequestCycle.getResponse();

		try
		{
			originalRequestCycle.setResponse(tempResponse);
			pageRenderer.respond(tempRequestCycle);
		}
		finally
		{
			originalRequestCycle.setResponse(oldResponse);
		}

		return tempResponse.getText();
	}


	/**
	 * Collects the html generated by the rendering of a component.
	 *
	 * @param component
	 *            the component to render.
	 * @return the html rendered by the component
	 */
	public static CharSequence renderComponent(final Component component)
	{
		RequestCycle requestCycle = RequestCycle.get();

		final Response originalResponse = requestCycle.getResponse();
		BufferedWebResponse tempResponse = new BufferedWebResponse(null);

		try
		{
			requestCycle.setResponse(tempResponse);

			RenderPage page = new RenderPage();
			page.add(component);

			component.render();
		}
		finally
		{
			requestCycle.setResponse(originalResponse);
		}

		return tempResponse.getText();
	}

	/**
	 * A page used as a parent for the component based rendering.
	 */
	private static class RenderPage extends WebPage implements IMarkupResourceStreamProvider
	{
		private static final String MARKUP = "<wicket:container wicket:id='" + COMP_ID +
				"'></wicket:container>";

		@Override
		public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass)
		{
			return new StringResourceStream(MARKUP);
		}

	}

}
