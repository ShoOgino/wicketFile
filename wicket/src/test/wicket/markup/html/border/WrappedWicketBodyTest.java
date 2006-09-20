/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar
 * 2006) eelco12 $ $Revision: 5004 $ $Date: 2006-03-17 20:47:08 -0800 (Fri, 17
 * Mar 2006) $
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
package wicket.markup.html.border;

import wicket.MarkupContainer;
import wicket.WicketTestCase;
import wicket.markup.IAlternateParentProvider;
import wicket.markup.IMarkupResourceStreamProvider;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.util.resource.IResourceStream;
import wicket.util.resource.StringResourceStream;

/**
 * Test borders where wicket:body is a child of border's child instead of a
 * direct child of the border
 * 
 * @author ivaynberg
 */
public class WrappedWicketBodyTest extends WicketTestCase
{
	/**
	 * Test borders where wicket:body is a child of border's child instead of a
	 * direct child of the border
	 * 
	 * @throws Exception
	 */
	public void testWicketBodyContainer() throws Exception
	{
		assertTrue(accessPage(TestPage.class).getDocument().contains("[[SUCCESS]]"));
	}

	/**
	 * Same as {@link #testWicketBodyContainer()}, but tests if borders operate
	 * properly when embedded in each other
	 * 
	 * @throws Exception
	 */
	public void testMultiLevelWicketBodyContainer() throws Exception
	{
		assertTrue(accessPage(TestPage2.class).getDocument().contains("[[SUCCESS]]"));
	}

	/**
	 * Test page
	 * 
	 * @author ivaynberg
	 */
	public static class TestPage extends WebPage implements IMarkupResourceStreamProvider
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 */
		public TestPage()
		{
			Border border = new TestBorder(this, "border");
			new Label(border, "label", "[[SUCCESS]]");
		}

		public IResourceStream getMarkupResourceStream(MarkupContainer container,
				Class<? extends MarkupContainer> containerClass)
		{
			return new StringResourceStream(
					"<html><body><span wicket:id='border'><span wicket:id='label'></span></span></body></html>");
		}
	}

	/**
	 * Test page
	 * 
	 * @author ivaynberg
	 */
	public static class TestPage2 extends WebPage implements IMarkupResourceStreamProvider
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 */
		public TestPage2()
		{
			Border border = new TestBorder(this, "border");
			Border border2 = new TestBorder(border, "border2");
			new Label(border2, "label", "[[SUCCESS]]");
		}

		public IResourceStream getMarkupResourceStream(MarkupContainer container,
				Class<? extends MarkupContainer> containerClass)
		{
			return new StringResourceStream(
					"<html><body><span wicket:id='border'><span wicket:id='border2'><span wicket:id='label'></span></span></span></body></html>");
		}
	}

	/**
	 * Test border that implemetns {@link IAlternateParentProvider}
	 * 
	 * @author ivaynberg
	 */
	public static class TestBorder extends Border
			implements
				IAlternateParentProvider,
				IMarkupResourceStreamProvider
	{
		private static final long serialVersionUID = 1L;

		/** container for wicket:body components */
		private final WebMarkupContainer bodyParent;

		/**
		 * Construct.
		 * 
		 * @param parent
		 * @param id
		 */
		public TestBorder(MarkupContainer parent, String id)
		{
			super(parent, id);
			bodyParent = new WebMarkupContainer(this, "body-parent");
		}

		public MarkupContainer getAlternateParent(Class childClass, String childId)
		{
			return (bodyParent == null) ? this : bodyParent;
		}

		public IResourceStream getMarkupResourceStream(MarkupContainer container,
				Class<? extends MarkupContainer> containerClass)
		{
			return new StringResourceStream(
					"<wicket:border><span wicket:id='body-parent'><wicket:body/></span></wicket:border>");
		}

	}

}
