/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.request.compound;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;

import wicket.Application;
import wicket.ApplicationPages;
import wicket.ApplicationSettings;
import wicket.Component;
import wicket.IRequestTarget;
import wicket.Page;
import wicket.PageParameters;
import wicket.RequestCycle;
import wicket.Session;
import wicket.WicketRuntimeException;
import wicket.protocol.http.request.WebErrorCodeResponseTarget;
import wicket.protocol.http.request.WebExternalResourceRequestTarget;
import wicket.request.ExpiredPageClassRequestTarget;
import wicket.request.ListenerInterfaceRequestTarget;
import wicket.request.PageClassRequestTarget;
import wicket.request.PageRequestTarget;
import wicket.request.RedirectPageRequestTarget;
import wicket.request.RequestParameters;
import wicket.request.SharedResourceRequestTarget;
import wicket.util.string.Strings;

/**
 * Default target resolver strategy. It tries to lookup any registered mount
 * with {@link wicket.request.IRequestEncoder} and in case no mount was found,
 * it uses the {@link wicket.request.RequestParameters} object for default
 * resolving.
 * 
 * @author Eelco Hillenius
 */
public final class DefaultRequestTargetResolver implements IRequestTargetResolverStrategy
{
	/**
	 * Construct.
	 */
	public DefaultRequestTargetResolver()
	{
	}

	/**
	 * @see wicket.request.compound.IRequestTargetResolverStrategy#resolve(wicket.RequestCycle,
	 *      RequestParameters)
	 */
	public final IRequestTarget resolve(RequestCycle requestCycle,
			RequestParameters requestParameters)
	{
		String path = requestCycle.getRequest().getPath();

		// first, see whether we can find any mount
		IRequestTarget mounted = requestCycle.getRequestCycleProcessor().getRequestEncoder()
				.getPathMount(path);
		if (mounted != null)
		{
			return mounted;
		}

		// See whether this request points to a rendered page
		if (requestParameters.getComponentPath() != null)
		{
			return resolveRenderedPage(requestCycle, requestParameters);
		}
		// see whether this request points to a bookmarkable page
		else if (requestParameters.getBookmarkablePageAlias() != null)
		{
			return resolveBookmarkablePage(requestCycle, requestParameters);
		}
		// see whether this request points to a shared resource
		else if (requestParameters.getResourceKey() != null)
		{
			return resolveSharedResource(requestCycle, requestParameters);
		}
		// see whether this request points to the home page
		else if (Strings.isEmpty(path) || ("/".equals(path)))
		{
			return resolveHomePageTarget(requestCycle, requestParameters);
		}

		// if we get here, we have no regconized Wicket target, and thus
		// regard this as a external (non-wicket) resource request on
		// this server

		// Get the relative URL we need for loading the resource from
		// the servlet context
		// NOTE: we NEED to put the '/' in front as otherwise some versions
		// of application servers (e.g. Jetty 5.1.x) will fail for requests
		// like '/mysubdir/myfile.css'
		final String url = '/' + requestCycle.getRequest().getRelativeURL();
		return new WebExternalResourceRequestTarget(url);
	}

	/**
	 * Resolves to a shared resource target.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 * @param requestParameters
	 *            the request parameters object
	 * @return the shared resource as a request target
	 */
	private IRequestTarget resolveSharedResource(final RequestCycle requestCycle,
			RequestParameters requestParameters)
	{
		final String resourceKey = requestParameters.getResourceKey();
		return new SharedResourceRequestTarget(resourceKey);
	}

	/**
	 * Resolves to a page target that was previously rendered. Optionally
	 * resolves to a component call target, which is a specialization of a page
	 * target. If no corresponding page could be found, a expired page target
	 * will be returned.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 * @param requestParameters
	 *            the request parameters object
	 * @return the previously rendered page as a request target
	 */
	private IRequestTarget resolveRenderedPage(RequestCycle requestCycle,
			RequestParameters requestParameters)
	{
		final String componentPath = requestParameters.getComponentPath();
		final Session session = requestCycle.getSession();
		final Page page = session.getPage(requestParameters.getPageMapName(), componentPath,
				requestParameters.getVersionNumber());

		// Does page exist?
		if (page != null)
		{
			// Assume cluster needs to be updated now, unless listener
			// invocation change this (for example, with a simple page
			// redirect)
			requestCycle.setUpdateCluster(true);

			// see whether this resolves to a component call or just the page
			final String interfaceName = requestParameters.getInterfaceName();
			if (interfaceName != null)
			{
				if (interfaceName.equals("IRedirectListener"))
				{
					return new RedirectPageRequestTarget(page);
				}
				else
				{
					final Method listenerMethod = requestCycle
							.getRequestInterfaceMethod(interfaceName);
					if (listenerMethod == null)
					{
						throw new WicketRuntimeException("Attempt to access unknown interface "
								+ interfaceName);
					}
					String componentPart = Strings.afterFirstPathComponent(componentPath, ':');
					if (Strings.isEmpty(componentPart))
					{
						// we have an interface that is not redirect, but no
						// component... that must be wrong
						throw new WicketRuntimeException("when trying to call " + listenerMethod
								+ ", a component must be provided");
					}
					final Component component = page.get(componentPart);
					if (!component.isVisible())
					{
						throw new WicketRuntimeException(
								"Calling listener methods on components that are not visible is not allowed");
					}
					return new ListenerInterfaceRequestTarget(page, component, listenerMethod);
				}
			}
			else
			{
				return new PageRequestTarget(page);
			}
		}
		else
		{
			// Page was expired from session, probably because backtracking
			// limit was reached
			return new ExpiredPageClassRequestTarget();
		}
	}

	/**
	 * Resolves to a bookmarkable page target.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 * @param requestParameters
	 *            the request parameters object
	 * @return the bookmarkable page as a request target
	 */
	private IRequestTarget resolveBookmarkablePage(RequestCycle requestCycle,
			RequestParameters requestParameters)
	{
		final String bookmarkablePageAlias = requestParameters.getBookmarkablePageAlias();
		final IRequestTarget requestTarget;
		final Session session = requestCycle.getSession();
		final Application application = session.getApplication();

		// first see whether we have a logical mapping
		Class pageClass = application.getPages().classForAlias(bookmarkablePageAlias);

		// nope, we don't have a logical mapping, so this should be a
		// full class name
		if (pageClass == null)
		{
			try
			{
				pageClass = session.getClassResolver().resolveClass(bookmarkablePageAlias);
			}
			catch (RuntimeException e)
			{
				return new WebErrorCodeResponseTarget(HttpServletResponse.SC_NOT_FOUND,
						"Unable to load Bookmarkable Page");
			}
		}

		try
		{
			Page newPage = session.getPageFactory().newPage(pageClass,
					new PageParameters(requestParameters.getParameters()));

			// the response might have been set in the constructor of
			// the bookmarkable page
			if (requestCycle.getResponsePage() == null)
			{
				requestTarget = new PageRequestTarget(newPage);
			}
			else
			{
				requestTarget = new PageRequestTarget(requestCycle.getResponsePage());
			}

			// as we have a new page, we should update the cluster
			// TODO abstract this so that we can decide by looking
			// at the kind of target and we don't have to bother
			// users with it?
			requestCycle.setUpdateCluster(true);

			return requestTarget;
		}
		catch (RuntimeException e)
		{
			throw new WicketRuntimeException("Unable to instantiate Page class: "
					+ bookmarkablePageAlias + ". See below for details.", e);
		}
	}

	/**
	 * Resolves to a home page target.
	 * 
	 * @param requestCycle
	 *            the current request cycle.
	 * @param requestParameters
	 *            the request parameters object
	 * @return the home page as a request target
	 */
	private IRequestTarget resolveHomePageTarget(RequestCycle requestCycle,
			RequestParameters requestParameters)
	{
		final IRequestTarget requestTarget;
		final Session session = requestCycle.getSession();
		final Application application = session.getApplication();
		try
		{
			Class homePage = application.getPages().getHomePage();
			ApplicationPages.HomePageRenderStrategy homePageStrategy = application.getPages()
					.getHomePageRenderStrategy();
			if (homePageStrategy == ApplicationPages.BOOKMARK_REDIRECT)
			{
				requestTarget = new PageClassRequestTarget(homePage);
			}
			else
			{
				final PageParameters parameters = new PageParameters(requestParameters
						.getParameters());
				Page newPage = session.getPageFactory().newPage(homePage, parameters);

				// check if the home page didn't set a page by itself
				if (requestCycle.getResponsePage() == null)
				{
					if (homePageStrategy == ApplicationPages.PAGE_REDIRECT)
					{
						// see if we have to redirect the render part by default
						// so that a homepage has the same url as a post or
						// get to that page.
						ApplicationSettings.RenderStrategy strategy = session.getApplication()
								.getSettings().getRenderStrategy();
						boolean issueRedirect = (strategy == ApplicationSettings.REDIRECT_TO_RENDER || strategy == ApplicationSettings.REDIRECT_TO_BUFFER);
						requestCycle.setRedirect(issueRedirect);
					}
					requestTarget = new PageRequestTarget(newPage);
				}
				else
				{
					requestTarget = new PageRequestTarget(requestCycle.getResponsePage());
				}
			}

			// as we have a new page, we should update the cluster
			// TODO abstract this so that we can decide by looking
			// at the kind of target and we don't have to bother
			// users with it?
			requestCycle.setUpdateCluster(true);

			return requestTarget;
		}
		catch (WicketRuntimeException e)
		{
			throw new WicketRuntimeException("Could not create home page", e);
		}
	}
}
