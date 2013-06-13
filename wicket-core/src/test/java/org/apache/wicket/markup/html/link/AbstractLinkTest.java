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
package org.apache.wicket.markup.html.link;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.wicket.Component;
import org.apache.wicket.MockPageWithLink;
import org.apache.wicket.WicketTestCase;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link AbstractLink}
 */
public class AbstractLinkTest extends WicketTestCase
{
	/**
	 * 
	 * @see <a href="https://issues.apache.org/jira/browse/WICKET-3338">WICKET-3338</a>
	 */
	@Test
	public void testSetBodyModel()
	{
		final String linkBody = "Link body";

		MockPageWithLink mockPageWithLink = new MockPageWithLink();
		AbstractLink link = new AbstractLink("link")
		{
			private static final long serialVersionUID = 1L;
		};
		link.setMarkupId("link");
		link.setBody(Model.of(linkBody));
		mockPageWithLink.add(link);

		tester.startPage(mockPageWithLink);
		TagTester tagTester = tester.getTagById("link");
		Assert.assertEquals(linkBody, tagTester.getValue());
	}

	/**
	 * Tests that the {@link AbstractLink} uses the {@link AbstractLink#getBody()} to get its body. This
	 * method can be overridden to provide a dynamic model.
	 */
	@Test
	public void testRenderUsingGetBody()
	{
		final AtomicInteger counter = new AtomicInteger(0);

		MockPageWithLink mockPageWithLink = new MockPageWithLink();
		AbstractLink link = new AbstractLink("link")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IModel<?> getBody()
			{
				return Model.of(counter.getAndIncrement());
			}
		};
		link.setMarkupId("link");
		mockPageWithLink.add(link);

		tester.startPage(mockPageWithLink);
		TagTester tagTester = tester.getTagById("link");
		Assert.assertEquals("0", tagTester.getValue());

		tester.startPage(mockPageWithLink);
		tagTester = tester.getTagById("link");
		Assert.assertEquals("1", tagTester.getValue());
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-4904
	 *
	 * Use a Behavior that does what Twitter Bootstrap requires
	 */
	@Test
	public void useCustomDisablingBehavior()
	{
		MockPageWithLink mockPageWithLink = new MockPageWithLink();
		AbstractLink link = new AbstractLink("link")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected Behavior getDisablingBehavior()
			{
				return new Behavior()
				{
					@Override
					public void onComponentTag(Component component, ComponentTag tag)
					{
						super.onComponentTag(component, tag);

						tag.put("disabled", "disabled");
					}
				};
			}
		};
		link.setMarkupId("link");
		link.setBody(Model.of("Link"));
		mockPageWithLink.add(link);

		tester.startPage(mockPageWithLink);
		TagTester tagTester = tester.getTagById("link");
		String markup = tagTester.getMarkup();
		Assert.assertEquals("<a href=\"#\" wicket:id=\"link\" id=\"link\" disabled=\"disabled\">Link</a>", markup);
	}

}
