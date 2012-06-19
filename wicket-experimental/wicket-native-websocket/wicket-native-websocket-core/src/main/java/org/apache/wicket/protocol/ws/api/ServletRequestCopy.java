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
package org.apache.wicket.protocol.ws.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A copy of the http servlet request used to create the WebSocket.
 * Copy its details because they are discarded at the end of the request (even if we
 * use AsyncContext from Servlet 3 spec). This copy is used later for following requests
 * through the WebSocket connection to construct WebSocketRequest.
 *
 * @since 6.0
 */
public class ServletRequestCopy implements HttpServletRequest
{
	private final String contextPath;
	private final String servletPath;
	private final String pathInfo;
	private final String requestUri;
	private final HttpSessionCopy httpSession;
	private final StringBuffer requestURL;
	private final Map<String, Object> attributes = new HashMap<String, Object>();
	private final Map<String, String> headers = new HashMap<String, String>();
	private final Map<String, String[]> parameters = new HashMap<String, String[]>();
	private final String method;
	private final String serverName;
	private final int serverPort;

	public ServletRequestCopy(HttpServletRequest request) {
		this.servletPath = request.getServletPath();
		this.contextPath = request.getContextPath();
		this.pathInfo = request.getPathInfo();
		this.requestUri = request.getRequestURI();
		this.requestURL = request.getRequestURL();
		this.method = request.getMethod();
		this.serverName = request.getServerName();
		this.serverPort = request.getServerPort();

		HttpSession session = request.getSession(true);
		httpSession = new HttpSessionCopy(session);

		Enumeration<String> e = request.getHeaderNames();
		String s;
		while (e.hasMoreElements()) {
			s = e.nextElement();
			headers.put(s, request.getHeader(s));
		}

		e = request.getAttributeNames();
		while (e.hasMoreElements()) {
			s = e.nextElement();
			attributes.put(s, request.getAttribute(s));
		}

		e = request.getParameterNames();
		while (e.hasMoreElements()) {
			s = e.nextElement();
			parameters.put(s, request.getParameterValues(s));
		}
	}

	public void destroy() {
		attributes.clear();
		headers.clear();
		parameters.clear();
		httpSession.destroy();
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public int getServerPort() {
		return serverPort;
	}

	@Override
	public BufferedReader getReader() throws IOException
	{
		return null;
	}

	@Override
	public String getRemoteAddr()
	{
		return null;
	}

	@Override
	public String getRemoteHost()
	{
		return null;
	}

	@Override
	public HttpSession getSession(boolean create) {
		return httpSession;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getAuthType()
	{
		return null;
	}

	@Override
	public Cookie[] getCookies()
	{
		return new Cookie[0];
	}

	@Override
	public long getDateHeader(String name)
	{
		return 0;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public Enumeration<String> getHeaders(final String name) {
		return new Enumeration<String>() {

			boolean hasNext = true;

			@Override
			public boolean hasMoreElements() {
				return hasNext && headers.get(name) != null;
			}

			@Override
			public String nextElement() {
				hasNext = false;
				return headers.get(name);
			}
		};
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(parameters.keySet());
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name) != null ? parameters.get(name)[0] : null;
	}

	@Override
	public String[] getParameterValues(final String name) {
		return parameters.get(name);
	}

	@Override
	public Map getParameterMap()
	{
		return null;
	}

	@Override
	public String getProtocol()
	{
		return null;
	}

	@Override
	public String getScheme()
	{
		return null;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(headers.keySet());
	}

	@Override
	public int getIntHeader(String name)
	{
		return 0;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	@Override
	public String getCharacterEncoding()
	{
		return null;
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException
	{
	}

	@Override
	public int getContentLength()
	{
		return 0;
	}

	@Override
	public String getContentType()
	{
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException
	{
		return null;
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public Locale getLocale()
	{
		return null;
	}

	@Override
	public Enumeration getLocales()
	{
		return null;
	}

	@Override
	public boolean isSecure()
	{
		return false;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path)
	{
		return null;
	}

	@Override
	public String getRealPath(String path)
	{
		return null;
	}

	@Override
	public int getRemotePort()
	{
		return 0;
	}

	@Override
	public String getLocalName()
	{
		return null;
	}

	@Override
	public String getLocalAddr()
	{
		return null;
	}

	@Override
	public int getLocalPort()
	{
		return 0;
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public String getQueryString()
	{
		return null;
	}

	@Override
	public String getRemoteUser()
	{
		return null;
	}

	@Override
	public boolean isUserInRole(String role)
	{
		return false;
	}

	@Override
	public Principal getUserPrincipal()
	{
		return null;
	}

	@Override
	public String getRequestedSessionId()
	{
		return null;
	}

	@Override
	public String getServletPath() {
		return servletPath;
	}

	@Override
	public String getPathInfo() {
		return pathInfo;
	}

	@Override
	public String getPathTranslated()
	{
		return null;
	}

	@Override
	public String getRequestURI() {
		return requestUri;
	}

	@Override
	public HttpSession getSession() {
		return httpSession;
	}

	@Override
	public boolean isRequestedSessionIdValid()
	{
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie()
	{
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL()
	{
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl()
	{
		return false;
	}

	@Override
	public StringBuffer getRequestURL() {
		return requestURL;
	}
}
