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
package org.apache.wicket.markup.html.form;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MockPageParametersAware;
import org.apache.wicket.WicketTestCase;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisitor;


/**
 * @author Pekka Enberg
 * @author Martijn Dashorst
 */
public class FormTest extends WicketTestCase
{
	private IVisitor<?, ?> visitor;

	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public FormTest(String name)
	{
		super(name);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		visitor = new Form.ValidationVisitor()
		{
			@Override
			public void validate(FormComponent<?> formComponent)
			{
			}
		};
	}


	/**
	 * @throws Exception
	 */
	public void testFormMethodGet() throws Exception
	{
		executeTest(FormMethodTestPage.class, "FormMethodTestPage_expected.html");
	}

	/**
	 * WICKET-3488
	 */
	public void testFormReplacement()
	{
		tester.startPage(TestPage.class);
		tester.newFormTester("form").submit();
		tester.assertRenderedPage(TestPage.class);
	}

	/**
	 * 
	 */
	public void testActionUrlNotDoubleEscaped()
	{
		tester.startPage(TestPage.class);
		String response = tester.getLastResponseAsString();
		assertTrue(response.contains(Strings.escapeMarkup(TestPage.TEST_QUERY_STRING)));
	}

	public void testOnValidateModelObjects()
	{

		class TestPage extends WebPage implements IMarkupResourceStreamProvider
		{
			boolean shouldFail, submit, error;

			public TestPage()
			{
				add(new Form<Void>("form")
				{
					@Override
					protected void onValidateModelObjects()
					{
						if (shouldFail)
							error("failed");
					}

					@Override
					protected void onSubmit()
					{
						submit = true;
					}

					@Override
					protected void onError()
					{
						error = true;
					}
				});
			}

			public IResourceStream getMarkupResourceStream(final MarkupContainer container,
				Class<?> containerClass)
			{
				return new StringResourceStream("<form wicket:id='form'></form>");
			}
		}

		TestPage page = new TestPage();
		tester.startPage(page);
		tester.submitForm("form");
		assertTrue(page.submit);
		assertFalse(page.error);

		page = new TestPage();
		page.shouldFail = true;
		tester.startPage(page);
		tester.submitForm("form");
		assertFalse(page.submit);
		assertTrue(page.error);
	}

	/** */
	public static class TestPage extends MockPageParametersAware
	{
		private static final long serialVersionUID = 1L;
		/** */
		public static final String TEST_QUERY_STRING = "&query_p_1=value_1";

		@Override
		protected Form<Void> newForm(String id)
		{
			return new Form<Void>(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected CharSequence getActionUrl()
				{
					return super.getActionUrl() + TEST_QUERY_STRING;
				}
			};
		}

	}
}
