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
package org.apache.wicket.ajax.form;

import org.apache.wicket.Page;
import org.apache.wicket.WicketTestCase;
import org.apache.wicket.util.tester.FormTester;

/**
 * Test case for WICKET-1291
 * 
 * @see <a href="https://issues.apache.org/jira/browse/WICKET-1291">WICKET-1291</a>
 * @author marrink
 */
public class AjaxFormSubmitTest extends WicketTestCase
{

	/**
	 * @see org.apache.wicket.WicketTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * @see org.apache.wicket.WicketTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test ajax form submit without default form processing.
	 */
	public void testSubmitNoDefProcessing()
	{
		Class<? extends Page> pageClass = AjaxFormSubmitTestPage.class;
		System.out.println("=== " + pageClass.getName() + " ===");

		tester.startPage(pageClass);
		tester.assertRenderedPage(pageClass);
		FormTester form = tester.newFormTester("form");
		form.setValue("txt1", "txt1");
		form.setValue("txt2", "txt2");
		tester.executeAjaxEvent("form:submit", "onclick");
		AjaxFormSubmitTestPage page = (AjaxFormSubmitTestPage)tester.getLastRenderedPage();
		assertFalse((page.getFormSubmitted() & AjaxFormSubmitTestPage.FORM) == AjaxFormSubmitTestPage.FORM);
		assertTrue((page.getFormSubmitted() & AjaxFormSubmitTestPage.BUTTON) == AjaxFormSubmitTestPage.BUTTON);
		assertEquals("foo", tester.getComponentFromLastRenderedPage("form:txt1")
			.getDefaultModelObject());
		assertEquals("bar", tester.getComponentFromLastRenderedPage("form:txt2")
			.getDefaultModelObject());
	}

	/**
	 * Test that onclick handler is generated with the proper XHTML entities
	 * for special characters, notably ampersand. See WICKET-2033.
	 */
	public void testEventJavaScriptEscaped() throws Exception
	{
		tester.startPage(AjaxFormSubmitTestPage.class);
		tester.assertResultPage(AjaxFormSubmitTestPage.class,
			"AjaxFormSubmitTestPage_expected.html");
	}

}
