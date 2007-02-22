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
package wicket.util.tester;

import java.util.Locale;

import junit.framework.TestCase;
import wicket.Component;
import wicket.MockPageWithLink;
import wicket.MockPageWithOneComponent;
import wicket.Page;
import wicket.Session;
import wicket.ajax.AjaxEventBehavior;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.ClientEvent;
import wicket.ajax.markup.html.AjaxLink;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.util.tester.MockPageWithFormAndAjaxFormSubmitBehavior.Pojo;
import wicket.util.tester.apps_1.Book;
import wicket.util.tester.apps_1.CreateBook;
import wicket.util.tester.apps_1.MyMockApplication;
import wicket.util.tester.apps_1.SuccessPage;
import wicket.util.tester.apps_1.ViewBook;


/**
 * 
 * @author Juergen Donnerstag
 */
public class WicketTesterTest extends TestCase
{
	private boolean eventExecuted;
	private WicketTester tester;

	@Override
	protected void setUp() throws Exception
	{
		eventExecuted = false;
		tester = new WicketTester(new MyMockApplication());
	}

	@Override
	protected void tearDown() throws Exception
	{
		tester.destroy();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testViewBook() throws Exception
	{
		// for WebPage without default constructor, I define a TestPageSource to
		// let the page be instatiated lately.
		tester.startPage(new ITestPageSource()
		{
			private static final long serialVersionUID = 1L;

			public Page getTestPage()
			{
				Book mockBook = new Book("xxId", "xxName");
				return new ViewBook(mockBook);
			}
		});

		// assertion
		tester.assertRenderedPage(ViewBook.class);
		tester.assertLabel("id", "xxId");
		tester.assertLabel("name", "xxName");
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testCreateBook_validateFail() throws Exception
	{
		Session.get().setLocale(Locale.US); // fix locale
		tester.startPage(CreateBook.class);

		FormTester formTester = tester.newFormTester("createForm");

		formTester.setValue("id", "");
		formTester.setValue("name", "");
		formTester.submit();

		tester.assertRenderedPage(CreateBook.class);

		// assert error message from validation
		tester.assertErrorMessages(new String[] { "id is required", "name is required" });
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testCreateBook_validatePass() throws Exception
	{
		tester.startPage(CreateBook.class);

		FormTester formTester = tester.newFormTester("createForm");

		formTester.setValue("id", "xxId");
		formTester.setValue("name", "xxName");
		formTester.submit();

		tester.assertRenderedPage(SuccessPage.class);

		// assert info message present.
		tester.assertInfoMessages(new String[] { "book 'xxName' created" });

		// assert previous page expired.
		// TODO Post 1.2: General: No longer a valid test
		// tester.assertExpirePreviousPage();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testBookmarkableLink() throws Exception
	{
		// for WebPage without default constructor, I define a TestPageSource to
		// let the page be instatiated lately.
		tester.startPage(new ITestPageSource()
		{
			private static final long serialVersionUID = 1L;

			public Page getTestPage()
			{
				Book mockBook = new Book("xxId", "xxName");
				return new ViewBook(mockBook);
			}
		});

		// assertion
		tester.assertRenderedPage(ViewBook.class);
		tester.clickLink("link");
		tester.assertRenderedPage(CreateBook.class);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testPageConstructor() throws Exception
	{
		Book mockBook = new Book("xxId", "xxName");
		Page page = new ViewBook(mockBook);
		tester.startPage(page);

		// assertion
		tester.assertRenderedPage(ViewBook.class);
		tester.clickLink("link");
		tester.assertRenderedPage(CreateBook.class);
	}

	/**
	 * 
	 */
	public void testAssertComponentOnAjaxResponse()
	{
		final Page page = new MockPageWithLink();
		AjaxLink ajaxLink = new AjaxLink(page, MockPageWithLink.LINK_ID)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				// Replace the link with a normal Link
				Link link = new Link(page, MockPageWithLink.LINK_ID)
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick()
					{
						// Do nothing
					}
				};
				link.setOutputMarkupId(true);

				target.addComponent(link);
			}
		};
		ajaxLink.setOutputMarkupId(true);

		tester.startPage(new ITestPageSource()
		{
			private static final long serialVersionUID = 1L;

			public Page getTestPage()
			{
				return page;
			}
		});


		// Click the link
		tester.clickLink(MockPageWithLink.LINK_ID);

		// The link must be a Link :)
		tester.assertComponent(MockPageWithLink.LINK_ID, Link.class);

		// Get the new link component
		Component component = tester.getComponentFromLastRenderedPage(MockPageWithLink.LINK_ID);

		// This must not fail
		tester.assertComponentOnAjaxResponse(component);
	}

	/**
	 * Test that the executeAjaxEvent on the WicketTester works.
	 */
	public void testExecuteAjaxEvent()
	{
		// Setup mocks
		final MockPageWithOneComponent page = new MockPageWithOneComponent();

		Label label = new Label(page, "component", "Dblclick This To See Magick");
		label.add(new AjaxEventBehavior(ClientEvent.DBLCLICK)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target)
			{
				eventExecuted = true;
			}
		});

		// Start the page
		tester.startPage(new ITestPageSource()
		{
			private static final long serialVersionUID = 1L;

			public Page getTestPage()
			{
				return page;
			}
		});

		// Execute the event
		tester.executeAjaxEvent(label, ClientEvent.DBLCLICK);

		assertTrue(eventExecuted);
	}

	/**
	 * Test that the clickLink works when submitting a form with a checkgroup
	 * inside.
	 */
	public void testClickLink_ajaxSubmitLink_checkGroup()
	{
		tester.startPage(MockPageWithFormAndCheckGroup.class);

		// Click the submit
		tester.clickLink("submitLink");
	}

	/**
	 * Test that the executeAjaxEvent "submits" the form if the event is a
	 * AjaxFormSubmitBehavior.
	 */
	public void testExecuteAjaxEvent_ajaxFormSubmitLink()
	{
		tester.startPage(MockPageWithFormAndAjaxFormSubmitBehavior.class);

		// Get the page
		MockPageWithFormAndAjaxFormSubmitBehavior page = (MockPageWithFormAndAjaxFormSubmitBehavior)tester
				.getLastRenderedPage();

		Pojo pojo = page.getPojo();

		assertEquals("Mock name", pojo.getName());
		assertEquals("Mock name", ((TextField)tester.getComponentFromLastRenderedPage("form"
				+ Component.PATH_SEPARATOR + "name")).getValue());

		assertFalse(page.isExecuted());

		// Execute the ajax event
		tester.executeAjaxEvent(MockPageWithFormAndAjaxFormSubmitBehavior.EVENT_COMPONENT,
				ClientEvent.CLICK);

		assertTrue("AjaxFormSubmitBehavior.onSubmit() has not been executed in "
				+ MockPageWithFormAndAjaxFormSubmitBehavior.class, page.isExecuted());

		assertEquals("Mock name", ((TextField)tester.getComponentFromLastRenderedPage("form" + Component.PATH_SEPARATOR + "name")).getValue());
		
		// The name of the pojo should still be the same. If the
		// executeAjaxEvent weren't submitting the form the name would have been
		// reset to null, because the form would have been updated but there
		// wouldn't be any data to update it with.
		assertNotNull("executeAjaxEvent() did not properly submit the form", pojo.getName());
		assertEquals("Mock name", pojo.getName());
	}

	/**
	 * 
	 */
	public void testRedirectWithPageParameters()
	{
		tester.startPage(MockPageParameterPage.class);

		tester.assertLabel("label", "");

		// Click the bookmarkable link
		tester.clickLink("link");

		// It should still be the MockPageParameterPage, but the
		// label should now have "1" in it because that's what comes
		// from the page parameter.
		tester.assertLabel("label", "1");
	}

	/**
	 * Test that clickLink on a ResourceLink with a ResourceReference on it
	 * works.
	 * 
	 * FIXME this test should be activated again once a proper solution to
	 * <b>WICKET-280 Allow to access html resources</b> is found
	 */
	public void bugTestClickResourceLink()
	{
		WicketTester tester = new WicketTester();
		
		tester.startPage(MockResourceLinkPage.class);
		
		tester.clickLink("link");
		
		tester.destroy();
	}
}
