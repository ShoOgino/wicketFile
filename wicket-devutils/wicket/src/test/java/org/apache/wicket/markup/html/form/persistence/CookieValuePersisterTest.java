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
package org.apache.wicket.markup.html.form.persistence;

import javax.servlet.http.Cookie;
import java.util.List;

import junit.framework.TestCase;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.persistence.CookieValuePersisterTestPage.TestForm;
import org.apache.wicket.protocol.http.*;
import org.apache.wicket.util.tester.WicketTester;

/**
 * How to test CookieValuePersister. Problem: CookieValuePersister relies on
 * RequestCycle.get().getApplication() to access application settings. RequestCycle.get() however is
 * valid only during the render process. It get's automatically attached and detached. Thus
 * RequestCycle.get() will be NULL before and after render. Thus CookieValuePersister can not be
 * tested outside the render process. You may think you can subclass CookieValuePersister and
 * provide your own getSettings(). Unfortunately you can't as it is private. One more note: Though
 * CookieValuePersister could probably be re-arranged in a way that RequestCycle.get() is no longer
 * a problem, the next (testing-) problem can not be solved. Cookies are added to the response and
 * based on the servlet api, the response has no means to validate (read) the already added cookies.
 * But the MockResponse does !?!?. The only solution I came up with so far is indirect testing.
 * Create a page with a form and a form component. Enable the component to be persistent. The first
 * run should give you a null value, as no values are currently stored. Than set a new value and
 * submit the form. The component's values will (hopefully) be saved. Refresh the same page and
 * voala the saved values should be automatically loaded.
 * 
 * @author Juergen Donnerstag
 */
public class CookieValuePersisterTest extends TestCase
{
	private WicketTester application;

	@Override
	protected void setUp() throws Exception
	{
		application = new WicketTester();
		application.startPage(CookieValuePersisterTestPage.class);
	}

	@Override
	protected void tearDown() throws Exception
	{
		application.destroy();
	}

	/**
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked"})
	public void test1() throws Exception
	{
		// How does the test work: Make sure you have a page, form and form
		// component properly set up (getRelativePath() etc.). See setUp().
		final Page page = application.getLastRenderedPage();

		// Get the form and form component created
		final TestForm form = (TestForm)page.get("form");
		final TextField<String> textField = (TextField<String>)form.get("input");

		// Make sure a valid cycle is available through RequestCycle.get().
		// The RequestCycle's constructor will attach the new cycle to
		// the threadLocal retrieved by RequestCycle.get().
		// Attached to this cycle must be a valid request and response
		final WebRequestCycle cycle = application.createRequestCycle();

		// Just after init, the requests and responses cookie lists must be
		// empty
		assertNull(getRequestCookies(cycle));
		assertEquals(0, getResponseCookies(cycle).size());

		// The persister to be used for the tests
		final CookieValuePersister persister = new CookieValuePersister();

		// See comment on CookieValuePersister on how clearing a value with
		// Cookies works. As no cookies in the request, no "delete" cookie
		// will be added to the response.
		persister.clear(textField);
		assertNull(getRequestCookies(cycle));
		assertEquals(0, getResponseCookies(cycle).size());

		// Save the input field's value (add it to the response's cookie list)
		persister.save(textField);
		assertNull(getRequestCookies(cycle));
		assertEquals(1, getResponseCookies(cycle).size());
		assertEquals("test", ((Cookie)getResponseCookies(cycle).get(0)).getValue());
		assertEquals("form.input", ((Cookie)getResponseCookies(cycle).get(0)).getName());
		assertEquals("/WicketTester$DummyWebApplication",
			((Cookie)getResponseCookies(cycle).get(0)).getPath());

		// To clear in the context of cookies means to add a special cookie
		// (maxAge=0) to the response, provided a cookie with
		// the same name has been provided in the request. Thus, no changes in
		// our test case
		persister.clear(textField);
		assertNull(getRequestCookies(cycle));
		assertEquals(1, getResponseCookies(cycle).size());
		assertEquals("test", ((Cookie)getResponseCookies(cycle).get(0)).getValue());
		assertEquals("form.input", ((Cookie)getResponseCookies(cycle).get(0)).getName());
		assertEquals("/WicketTester$DummyWebApplication",
			((Cookie)getResponseCookies(cycle).get(0)).getPath());

		// Try to load it. Because there is no Cookie matching the textfield's name
		// it remains unchanged
		persister.load(textField);
		assertEquals("test", textField.getDefaultModelObjectAsString());

		// Simulate loading a textfield. Initialize textfield with a new
		// (default) value, copy the cookie from respone to request (simulating
		// a browser), than load the textfield from cookie and voala the
		// textfields value should change.
		// save means: add it to the respone
		// load means: take it from request
		assertEquals("test", textField.getDefaultModelObjectAsString());
		textField.setDefaultModelObject("new text");
		assertEquals("new text", textField.getDefaultModelObjectAsString());
		copyCookieFromResponseToRequest(cycle);
		assertEquals(1, getRequestCookies(cycle).length);
		assertEquals(1, getResponseCookies(cycle).size());

		persister.load(textField);
		assertEquals("test", textField.getDefaultModelObjectAsString());
		assertEquals(1, getRequestCookies(cycle).length);
		assertEquals(1, getResponseCookies(cycle).size());

		// remove all cookies from mock response
		// Because I'll find the cookie to be removed in the request, the
		// persister will create
		// a "delete" cookie to remove the cookie on the client and add it to
		// the response.
		persister.clear(textField);
		assertEquals(1, getRequestCookies(cycle).length);
		assertEquals(2, getResponseCookies(cycle).size());
		assertEquals("form.input", ((Cookie)getResponseCookies(cycle).get(1)).getName());
		assertEquals(0, ((Cookie)getResponseCookies(cycle).get(1)).getMaxAge());
	}

	private void copyCookieFromResponseToRequest(final RequestCycle cycle)
	{
		((MockHttpServletRequest)((WebRequest)cycle.getRequest()).getHttpServletRequest()).addCookie((Cookie)getResponseCookies(
			cycle).get(0));
	}

	private Cookie[] getRequestCookies(final RequestCycle cycle)
	{
		return ((WebRequest)cycle.getRequest()).getHttpServletRequest().getCookies();
	}

	private List<Cookie> getResponseCookies(final RequestCycle cycle)
	{
		MockHttpServletResponse response = (MockHttpServletResponse) ((WebResponse) cycle.getResponse()).getHttpServletResponse();
		return (List<Cookie>) response.getCookies();
	}
}
