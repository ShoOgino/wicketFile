/*
 * $Id: DummyHomePage.java 5860 2006-05-25 20:29:28 +0000 (Thu, 25 May 2006)
 * eelco12 $ $Revision$ $Date: 2006-05-25 20:29:28 +0000 (Thu, 25 May
 * 2006) $
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
package wicket.util.tester;

import wicket.MarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.link.Link;

/**
 * A dummy homepage required by WicketTester only
 * 
 * @author Ingram Chen
 */
public class DummyHomePage extends WebPage
{
	private static final long serialVersionUID = 1L;

	private ITestPageSource testPageSource;

	private Link testPageLink;

	/**
	 * Construct
	 */
	public DummyHomePage()
	{
		testPageLink = new TestLink(this, "testPage");
		add(testPageLink);
	}

	/**
	 * 
	 * @param testPageSource
	 */
	public void setTestPageSource(ITestPageSource testPageSource)
	{
		this.testPageSource = testPageSource;
	}

	/**
	 * 
	 * @return Link
	 */
	public Link getTestPageLink()
	{
		return testPageLink;
	}

	/**
	 * 
	 */
	public class TestLink extends Link
	{
		private static final long serialVersionUID = 1L;

		/**
		 * 
		 * @param id
		 */
		public TestLink(MarkupContainer parent, String id)
		{
			super(parent, id);
		}

		/**
		 * 
		 */
		@Override
		public void onClick()
		{
			setResponsePage(testPageSource.getTestPage());
		}
	}
}
