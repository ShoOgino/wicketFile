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
package org.apache.wicket.request.target.coding;

import org.apache.wicket.IResourceListener;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPageWithIFrame extends WebPage
{
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(TestPageWithIFrame.class);

	public static final String resourceText = "foobar - do nada nothing njet";

	public String resourceContent;

	class TestFrame extends WebMarkupContainer implements IResourceListener
	{
		private static final long serialVersionUID = 1L;

		public TestFrame(String id)
		{
			super(id);
		}

		@Override
		protected void onComponentTag(ComponentTag tag)
		{
			checkComponentTag(tag, "iframe");
			super.onComponentTag(tag);
			tag.put("src", urlFor(IResourceListener.INTERFACE));
		}

		public void onResourceRequested()
		{
			resourceContent = resourceText;
			log.info(resourceContent);
		}
	}

	public TestPageWithIFrame(PageParameters params)
	{
		TestFrame frame = new TestFrame("frame");
		add(frame);
	}
}
