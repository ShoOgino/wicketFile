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
package wicket.markup.html.form.login;

import junit.framework.TestCase;
import wicket.Component;
import wicket.ISessionFactory;
import wicket.Request;
import wicket.RestartResponseAtInterceptPageException;
import wicket.Session;
import wicket.authorization.Action;
import wicket.authorization.IAuthorizationStrategy;
import wicket.protocol.http.MockWebApplication;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequestCycle;
import wicket.protocol.http.WebSession;
import wicket.util.string.Strings;


/**
 * @author marrink
 * 
 */
public class InterceptTest extends TestCase
{
	private MyMockWebApplication application;

	/**
	 * Constructor for InterceptTest.
	 * 
	 * @param arg0
	 */
	public InterceptTest(String arg0)
	{
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		application = new MyMockWebApplication("src/test/"
				+ getClass().getPackage().getName().replace('.', '/'));
		application.setHomePage(MockHomePage.class);
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * 
	 */
	public void testClickLink()
	{
		application.setupRequestAndResponse();
		application.processRequestCycle();
		MockLoginPage loginPage = (MockLoginPage)application.getLastRenderedPage();
		assertEquals(application.getLoginPage(), loginPage.getClass());

		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(loginPage.getForm());
		application.getServletRequest().setParameter(loginPage.getTextField().getInputName(), "admin");
		application.processRequestCycle();

		// continueToInterceptPage seems to return the same call, causing it to
		// login twice as a result the lastrendered page is null
		assertEquals(application.getHomePage(), application.getLastRenderedPage().getClass());

		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(application.getLastRenderedPage().get("link"));
		application.processRequestCycle();
		assertEquals(PageA.class, application.getLastRenderedPage().getClass());
	}

	/**
	 * 
	 */
	public void testClickLink2()
	{
		// same as above but uses different technique to login
		application.setupRequestAndResponse();
		application.processRequestCycle();
		MockLoginPage loginPage = (MockLoginPage)application.getLastRenderedPage();
		assertEquals(application.getLoginPage(), loginPage.getClass());
		
		// bypass form completely to login but continue to intercept page
		application.setupRequestAndResponse();
		WebRequestCycle requestCycle = application.createRequestCycle();
		assertTrue(((MockLoginPage)application.getLastRenderedPage()).login("admin"));
		application.processRequestCycle(requestCycle);
		assertEquals(application.getHomePage(), application.getLastRenderedPage().getClass());
		
		application.setupRequestAndResponse();
		application.getServletRequest().setRequestToComponent(application.getLastRenderedPage().get("link"));
		application.processRequestCycle();
		assertEquals(PageA.class, application.getLastRenderedPage().getClass());
	}

	/**
	 * 
	 * @author
	 */
	private static class MyMockWebApplication extends MockWebApplication implements ISessionFactory
	{
		private static final long serialVersionUID = 1L;

		/**
		 * @param path
		 */
		public MyMockWebApplication(String path)
		{
			super(path);
			getSecuritySettings().setAuthorizationStrategy(new MyAuthorizationStrategy());
		}

		/**
		 * 
		 * @return Class
		 */
		public Class getLoginPage()
		{
			return MockLoginPage.class;
		}

		/**
		 * 
		 * @see wicket.ISessionFactory#newSession(Request)
		 */
		public Session newSession(Request request)
		{
			return new MySession(this);
		}

		/**
		 * 
		 * @see wicket.Application#getSessionFactory()
		 */
		protected ISessionFactory getSessionFactory()
		{
			return this;
		}
	}

	/**
	 * 
	 */
	public static class MySession extends WebSession
	{
		private static final long serialVersionUID = 1L;

		private String username;

		/**
		 * @param application
		 */
		protected MySession(WebApplication application)
		{
			super(application);
		}

		protected final String getUsername()
		{
			return username;
		}

		protected final void setUsername(String username)
		{
			this.username = username;
		}

		/**
		 * 
		 * @return boolean
		 */
		public boolean isLoggedIn()
		{
			return !Strings.isEmpty(username);
		}
	}

	/**
	 * 
	 */
	private static class MyAuthorizationStrategy implements IAuthorizationStrategy
	{
		/**
		 * @see wicket.authorization.IAuthorizationStrategy#isInstantiationAuthorized(java.lang.Class)
		 */
		public boolean isInstantiationAuthorized(Class componentClass)
		{
			if (MockHomePage.class.equals(componentClass)
					&& !((MySession)Session.get()).isLoggedIn())
			{
				throw new RestartResponseAtInterceptPageException(MockLoginPage.class);
			}
			return true;
		}

		/**
		 * @see wicket.authorization.IAuthorizationStrategy#isActionAuthorized(wicket.Component,
		 *      wicket.authorization.Action)
		 */
		public boolean isActionAuthorized(Component component, Action action)
		{
			return true;
		}
	}
}
