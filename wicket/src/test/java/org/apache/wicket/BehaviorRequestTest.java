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

import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.behavior.IBehaviorListener;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.handler.IPageAndComponentProvider;
import org.apache.wicket.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.request.handler.ListenerInvocationNotAllowedException;
import org.apache.wicket.request.handler.PageAndComponentProvider;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;


/**
 * See issue <a href="https://issues.apache.org/jira/browse/WICKET-3098">WICKET-3098</a>
 */
public class BehaviorRequestTest extends WicketTestCase
{
	private TestPage page;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		page = new TestPage();
		tester.startPage(page);
	}

	public void testEnabledBehaviorRequest()
	{
		tester.executeUrl(urlForBehavior(page.enabledBehavior));
		assertTrue(page.enabledBehavior.wasCalled());
	}

	public void testDisabledBehaviorRequest()
	{
		try
		{
			tester.executeUrl(urlForBehavior(page.disabledBehavior));
			fail("Executing the listener on disabled component is not allowed.");
		}
		catch (ListenerInvocationNotAllowedException expected)
		{
			assertFalse(page.disabledBehavior.wasCalled());
		}
	}

	private String urlForBehavior(IBehavior behaviorUnderTest)
	{
		final int index = page.container.getBehaviorId(behaviorUnderTest);
		final IPageAndComponentProvider provider = new PageAndComponentProvider(page, page.container);
		final IRequestHandler handler =
			new ListenerInterfaceRequestHandler(provider, IBehaviorListener.INTERFACE, index);

		return tester.urlFor(handler).toString();
	}

	public static class TestPage extends WebPage implements IMarkupResourceStreamProvider
	{
		private WebMarkupContainer container;
		private TestCallbackBehavior enabledBehavior;
		private TestCallbackBehavior disabledBehavior;

		public TestPage()
		{
			enabledBehavior = new TestCallbackBehavior();
			enabledBehavior.setEnabled(true);
			disabledBehavior = new TestCallbackBehavior();
			disabledBehavior.setEnabled(false);
			container = new WebMarkupContainer("container");
			container.add(enabledBehavior);
			container.add(disabledBehavior);
			add(container);
		}

		public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass)
		{
			return new StringResourceStream("<html><a wicket:id=\"container\">container</a></html>");
		}
	}

	private static class TestCallbackBehavior extends AbstractBehavior implements IBehaviorListener
	{
		private boolean enabled;
		private boolean called;

		@Override
		public void onComponentTag(Component component, ComponentTag tag)
		{
			super.onComponentTag(component, tag);
			tag.put("href", component.urlFor(this, IBehaviorListener.INTERFACE));
		}

		public void onRequest()
		{
			called = true;
		}

		public void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
		}

		@Override
		public boolean isEnabled(Component component)
		{
			return component.isEnabledInHierarchy() && enabled;
		}

		public boolean wasCalled()
		{
			return called;
		}

	}
}