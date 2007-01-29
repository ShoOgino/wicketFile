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
package wicket.markup.html.list;

import junit.framework.TestCase;
import wicket.markup.html.link.Link;
import wicket.protocol.http.MockWebApplication;
import wicket.util.diff.DiffUtil;


/**
 * Test for simple table behavior.
 */
public class PagedTableNavigatorWithLabelProviderTest extends TestCase
{
	/**
	 * Construct.
	 */
	public PagedTableNavigatorWithLabelProviderTest()
	{
		super();
	}

	/**
	 * Construct.
	 * @param name name of test
	 */
	public PagedTableNavigatorWithLabelProviderTest(String name)
	{
		super(name);
	}

	/**
	 * Test simple table behavior.
	 * @throws Exception
	 */
	public void testPagedTable() throws Exception
	{
		MockWebApplication application = new MockWebApplication(null);
		application.setHomePage(PagedTableNavigatorWithLabelProviderPage.class);
		application.setupRequestAndResponse();
		application.processRequestCycle();
		PagedTableNavigatorWithLabelProviderPage page = (PagedTableNavigatorWithLabelProviderPage)application.getLastRenderedPage();
		String document = application.getServletResponse().getDocument();
		DiffUtil.validatePage(document, this.getClass(), "PagedTableNavigatorWithLabelProviderExpectedResult_1.html", true);

		Link link = (Link)page.get("navigator:first");
		assertFalse(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		assertFalse(link.isEnabled());

		link = (Link)page.get("navigator:next");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:last");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:next");
		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(link);
		application.processRequestCycle();
		document = application.getServletResponse().getDocument();
		DiffUtil.validatePage(document, this.getClass(), "PagedTableNavigatorWithLabelProviderExpectedResult_2.html", true);

		link = (Link)page.get("navigator:first");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:next");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:last");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(link);
		application.processRequestCycle();
		document = application.getServletResponse().getDocument();
		DiffUtil.validatePage(document, this.getClass(), "PagedTableNavigatorWithLabelProviderExpectedResult_3.html", true);

		link = (Link)page.get("navigator:first");
		assertFalse(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		assertFalse(link.isEnabled());

		link = (Link)page.get("navigator:next");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:last");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:last");
		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(link);
		application.processRequestCycle();
		document = application.getServletResponse().getDocument();
		DiffUtil.validatePage(document, this.getClass(), "PagedTableNavigatorWithLabelProviderExpectedResult_4.html", true);

		link = (Link)page.get("navigator:first");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:next");
		assertFalse(link.isEnabled());

		link = (Link)page.get("navigator:last");
		assertFalse(link.isEnabled());

		link = (Link)page.get("navigator:first");
		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(link);
		application.processRequestCycle();
		document = application.getServletResponse().getDocument();
		DiffUtil.validatePage(document, this.getClass(), "PagedTableNavigatorWithLabelProviderExpectedResult_5.html", true);

		link = (Link)page.get("navigator:first");
		assertFalse(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		assertFalse(link.isEnabled());

		link = (Link)page.get("navigator:next");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:last");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:navigation:3:pageLink");
		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(link);
		application.processRequestCycle();
		document = application.getServletResponse().getDocument();
		DiffUtil.validatePage(document, this.getClass(), "PagedTableNavigatorWithLabelProviderExpectedResult_6.html", true);

		link = (Link)page.get("navigator:first");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:next");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:last");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(link);
		application.processRequestCycle();
		document = application.getServletResponse().getDocument();
		DiffUtil.validatePage(document, this.getClass(), "PagedTableNavigatorWithLabelProviderExpectedResult_7.html", true);

		link = (Link)page.get("navigator:first");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:prev");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:next");
		assertTrue(link.isEnabled());

		link = (Link)page.get("navigator:last");
		assertTrue(link.isEnabled());
	}
}
