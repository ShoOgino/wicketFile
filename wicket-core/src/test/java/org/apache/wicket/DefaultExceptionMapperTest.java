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
package org.apache.wicket;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link DefaultExceptionMapper}
 */
public class DefaultExceptionMapperTest
{
	/**
	 * <a href="https://issues.apache.org/jira/browse/WICKET-3520">WICKET-3520</a>
	 */
	@Test
	public void showNoExceptionPage()
	{
		MockApplication application = new MockApplication()
		{
			@Override
			protected void init()
			{
				getExceptionSettings().setUnexpectedExceptionDisplay(
					IExceptionSettings.SHOW_NO_EXCEPTION_PAGE);
			}
		};

		WicketTester tester = new WicketTester(application);
		tester.setExposeExceptions(false);

		ShowNoExceptionPage page = new ShowNoExceptionPage(null);
		tester.startPage(page);

		tester.submitForm("form");

		Assert.assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, tester.getLastResponse()
			.getStatus());

		tester.destroy();
	}

	/**
	 * A test page for {@link DefaultExceptionMapperTest#showNoExceptionPage()}
	 */
	public static class ShowNoExceptionPage extends WebPage
		implements
			IMarkupResourceStreamProvider
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param parameters
		 */
		public ShowNoExceptionPage(final PageParameters parameters)
		{
			super(parameters);

			Form<?> form = new Form<Void>("form")
			{
				private static final long serialVersionUID = 1L;

				/**
				 * Always fails.
				 */
				@Override
				public void onSubmit()
				{
					throw new RuntimeException("test");
				}

			};
			add(form);
		}

		public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass)
		{
			return new StringResourceStream(
				"<html><body><form wicket:id=\"form\"></form></body></html>");
		}

	}


}
