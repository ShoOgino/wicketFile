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
package org.apache.wicket.protocol.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.ThreadContext;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.file.WebXmlFile;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter for initiating handling of Wicket requests.
 * 
 * <p>
 * The advantage of a filter is that, unlike a servlet, it can choose not to process the request and
 * let whatever is next in chain try. So when using a Wicket filter and a request comes in for
 * foo.gif the filter can choose not to process it because it knows it is not a wicket-related
 * request. Since the filter didn't process it, it falls on to the application server to try, and
 * then it works."
 * 
 * @see WicketServlet for documentation
 * 
 * @author Jonathan Locke
 * @author Timur Mehrvarz
 * @author Juergen Donnerstag
 * @author Igor Vaynberg (ivaynberg)
 * @author Al Maw
 * @author jcompagner
 * @author Matej Knopp
 */
public class WicketFilter implements Filter
{
	private static final Logger log = LoggerFactory.getLogger(WicketFilter.class);

	/** The name of the root path parameter that specifies the root dir of the app. */
	public static final String FILTER_MAPPING_PARAM = "filterMappingUrlPattern";

	/** The name of the context parameter that specifies application factory class */
	public static final String APP_FACT_PARAM = "applicationFactoryClassName";

	static final String SERVLET_PATH_HOLDER = "<servlet>";

	// Wicket's Application object
	private WebApplication application;

	private FilterConfig filterConfig;

	private String filterPath;

	// filterPath length without trailing "/"
	private int filterPathLength = -1;

	/**
	 * @return The class loader
	 */
	protected ClassLoader getClassLoader()
	{
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * This is Wicket's main method to execute a request
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @return false, if the request could not be processed
	 * @throws IOException
	 * @throws ServletException
	 */
	boolean processRequest(final ServletRequest request, final ServletResponse response,
		final FilterChain chain) throws IOException, ServletException
	{
		// Assume we are able to handle the request
		boolean res = true;

		final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		final ClassLoader newClassLoader = getClassLoader();

		try
		{
			if (previousClassLoader != newClassLoader)
			{
				Thread.currentThread().setContextClassLoader(newClassLoader);
			}

			HttpServletRequest httpServletRequest = (HttpServletRequest)request;
			HttpServletResponse httpServletResponse = (HttpServletResponse)response;

			String filterPath = getFilterPath(httpServletRequest);

			String redirectURL = checkIfRedirectRequired(httpServletRequest);
			if (redirectURL == null)
			{
				// No redirect; process the request
				application.set();

				WebRequest webRequest = new ServletWebRequest(httpServletRequest, filterPath);
				WebResponse webResponse = new HeaderBufferingWebResponse(new ServletWebResponse(
					httpServletRequest, httpServletResponse));

				RequestCycle requestCycle = application.createRequestCycle(webRequest, webResponse);
				if (!requestCycle.processRequestAndDetach())
				{
					if (chain != null)
					{
						chain.doFilter(request, response);
					}
					res = false;
				}
				else
				{
					webResponse.flush();
				}
			}
			else
			{
				try
				{
					// send redirect - this will discard POST parameters if the request is POST
					// - still better than getting an error because of lacking trailing slash
					httpServletResponse.sendRedirect(httpServletResponse.encodeRedirectURL(redirectURL));
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		finally
		{
			ThreadContext.detach();

			if (newClassLoader != previousClassLoader)
			{
				Thread.currentThread().setContextClassLoader(previousClassLoader);
			}

			response.flushBuffer();
		}
		return res;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response,
		final FilterChain chain) throws IOException, ServletException
	{
		processRequest(request, response, chain);
	}

	/**
	 * Creates the web application factory instance.
	 * 
	 * If no APP_FACT_PARAM is specified in web.xml ContextParamWebApplicationFactory will be used
	 * by default.
	 * 
	 * @see ContextParamWebApplicationFactory
	 * 
	 * @return application factory instance
	 */
	protected IWebApplicationFactory getApplicationFactory()
	{
		final String appFactoryClassName = filterConfig.getInitParameter(APP_FACT_PARAM);

		if (appFactoryClassName == null)
		{
			// If no context param was specified we return the default factory
			return new ContextParamWebApplicationFactory();
		}
		else
		{
			try
			{
				// Try to find the specified factory class
				// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6500212
				// final Class<?> factoryClass = Thread.currentThread()
				// .getContextClassLoader()
				// .loadClass(appFactoryClassName);
				final Class<?> factoryClass = Class.forName(appFactoryClassName, false,
					Thread.currentThread().getContextClassLoader());

				// Instantiate the factory
				return (IWebApplicationFactory)factoryClass.newInstance();
			}
			catch (ClassCastException e)
			{
				throw new WicketRuntimeException("Application factory class " +
					appFactoryClassName + " must implement IWebApplicationFactory");
			}
			catch (ClassNotFoundException e)
			{
				throw new WebApplicationFactoryCreationException(appFactoryClassName, e);
			}
			catch (InstantiationException e)
			{
				throw new WebApplicationFactoryCreationException(appFactoryClassName, e);
			}
			catch (IllegalAccessException e)
			{
				throw new WebApplicationFactoryCreationException(appFactoryClassName, e);
			}
			catch (SecurityException e)
			{
				throw new WebApplicationFactoryCreationException(appFactoryClassName, e);
			}
		}
	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(final FilterConfig filterConfig) throws ServletException
	{
		this.filterConfig = filterConfig;

		IWebApplicationFactory factory = getApplicationFactory();
		application = factory.createApplication(this);
		application.setName(filterConfig.getFilterName());
		application.setWicketFilter(this);

		filterPath = new WebXmlFile().getFilterPath(filterConfig);
		if (filterPath == null)
		{
			log.info("Unable to parse filter mapping web.xml for " + filterConfig.getFilterName() +
				". " + "Configure with init-param " + FILTER_MAPPING_PARAM +
				" if it is not \"/*\".");
		}

		final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		final ClassLoader newClassLoader = getClassLoader();

		application.set();
		try
		{
			if (previousClassLoader != newClassLoader)
			{
				Thread.currentThread().setContextClassLoader(newClassLoader);
			}

			application.initApplication();

			// Give the application the option to log that it is started
			application.logStarted();
		}
		finally
		{
			ThreadContext.detach();

			if (newClassLoader != previousClassLoader)
			{
				Thread.currentThread().setContextClassLoader(previousClassLoader);
			}
		}
	}

	/**
	 * @return filter config
	 */
	public FilterConfig getFilterConfig()
	{
		return filterConfig;
	}

	/**
	 * Either get the filterPath retrieved from web.xml, or if not found the old (1.3) way via a
	 * filter mapping param.
	 * 
	 * @param request
	 * @return filterPath
	 */
	protected String getFilterPath(final HttpServletRequest request)
	{
		if (filterPath != null)
		{
			return filterPath;
		}

		// Legacy migration check.
		// TODO: Remove this after 1.3 is released and everyone's upgraded.

		String result = filterConfig.getInitParameter(FILTER_MAPPING_PARAM);
		if (result == null || result.equals("/*"))
		{
			filterPath = "";
		}
		else if (!result.startsWith("/") || !result.endsWith("/*"))
		{
			throw new WicketRuntimeException("Your " + FILTER_MAPPING_PARAM +
				" must start with \"/\" and end with \"/*\". It is: " + result);
		}
		else
		{
			// remove leading "/" and trailing "*"
			filterPath = result.substring(1, result.length() - 1);
		}
		return filterPath;
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{
		if (application != null)
		{
			application.internalDestroy();
			application = null;
		}
	}

	/**
	 * Try to determine as fast as possible if a redirect is necessary
	 * 
	 * @param request
	 * @return null, if no redirect is necessary. Else the redirect URL
	 */
	private String checkIfRedirectRequired(final HttpServletRequest request)
	{
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();

		// length without jesessionid (http://.../abc;jsessionid=...?param)
		int uriLength = requestURI.indexOf(';');
		if (uriLength == -1)
		{
			uriLength = requestURI.length();
		}

		if (filterPathLength == -1)
		{
			filterPathLength = filterPath.length();
			if (filterPath.endsWith("/"))
			{
				filterPathLength -= 1;
			}
		}

		// uri != request.getContextPath() + "/" + filterPath (without "/" at the end)
		if (uriLength != (contextPath.length() + 1 + filterPathLength))
		{
			return null;
		}

		// current URI without jsessionid
		String uri = requestURI.substring(0, uriLength);

		// home page without trailing slash URI
		String homePageUri = contextPath + "/" + filterPath.substring(0, filterPathLength);
		if (uri.equals(homePageUri) == false)
		{
			return null;
		}

		// create the redirect URI
		uri += "/";
		if (!Strings.isEmpty(request.getQueryString()))
		{
			uri += "?" + request.getQueryString();
		}

		return uri;
	}
}
