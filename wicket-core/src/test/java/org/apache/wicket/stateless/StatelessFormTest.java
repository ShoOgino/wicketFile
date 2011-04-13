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
package org.apache.wicket.stateless;

import junit.framework.TestCase;

import org.apache.wicket.Page;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.stateless.pages.HomePage;
import org.apache.wicket.stateless.pages.LoginPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;

/**
 * 
 * @author marrink
 */
public class StatelessFormTest extends TestCase
{
	private final Class<? extends Page> HOME = HomePage.class;
	private final Class<? extends Page> LOGIN = LoginPage.class;

	private WicketTester createTester()
	{
		return new WicketTester(new MockApplication()
			{
				@Override
				public Class<? extends Page> getHomePage()
				{
					return HOME;
				}
	
			});
	}

	/**
	 * Login through the login page.
	 */
	public void testLogin()
	{
		WicketTester tester = createTester();

		try
		{
			tester.startPage(LOGIN);
			tester.assertRenderedPage(LOGIN);
			FormTester form = tester.newFormTester("signInPanel:signInForm");
			form.setValue("username", "test");
			form.setValue("password", "test");
			form.submit();
			tester.assertRenderedPage(HOME);
		}
		finally
		{
			tester.destroy();
		}
	}

	/**
	 * test initialization of component on stateless components
	 */
	public void testOnInitializationForStatelessComponents()
	{
		WicketTester tester = createTester();

		try
		{
			LoginPage page = new LoginPage();
			assertFalse(page.isPageInitialized());
			assertFalse(page.isPanelInitialized());

			tester.startPage(LOGIN);
			tester.assertRenderedPage(LOGIN);
			page = (LoginPage)tester.getLastRenderedPage();
			assertTrue(page.isPageInitialized());
			assertTrue(page.isPanelInitialized());
			
			FormTester form = tester.newFormTester("signInPanel:signInForm");
			form.setValue("username", "test");
			form.setValue("password", "invalid");
			form.submit();
			
			tester.assertRenderedPage(LOGIN);
			page = (LoginPage)tester.getLastRenderedPage();
			assertTrue(page.isPageInitialized());
			assertTrue(page.isPanelInitialized());
		}
		finally
		{
			tester.destroy();
		}
	}

}
