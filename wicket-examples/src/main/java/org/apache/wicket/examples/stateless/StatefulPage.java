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
package org.apache.wicket.examples.stateless;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Another page of the stateless example.
 * 
 * @author Eelco Hillenius
 */
public class StatefulPage extends WebPage<Void>
{
	/** click count for Link. */
	private int linkClickCount = 0;

	/**
	 * Construct.
	 */
	public StatefulPage()
	{
		add(new Label<String>("message", new SessionModel()));
		add(new BookmarkablePageLink("indexLink", Index.class));

		// Action link counts link clicks
		final Link<?> actionLink = new Link<Void>("actionLink")
		{
			@Override
			public void onClick()
			{
				linkClickCount++;
			}
		};
		add(actionLink);
		actionLink.add(new Label<Integer>("linkClickCount", new PropertyModel<Integer>(this,
			"linkClickCount")));

		final TextField<String> field = new TextField<String>("textfield", new Model<String>());

		StatelessForm<?> statelessForm = new StatelessForm<Void>("statelessform")
		{
			/**
			 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
			 */
			@Override
			protected void onSubmit()
			{
				info("Submitted text: " + field.getModelObject() + ", link click count: " +
					linkClickCount);
			}
		};
		statelessForm.add(field);
		add(statelessForm);
		add(new FeedbackPanel("feedback"));
	}

	/**
	 * Gets linkClickCount.
	 * 
	 * @return linkClickCount
	 */
	public int getLinkClickCount()
	{
		return linkClickCount;
	}

	/**
	 * Sets linkClickCount.
	 * 
	 * @param linkClickCount
	 *            linkClickCount
	 */
	public void setLinkClickCount(int linkClickCount)
	{
		this.linkClickCount = linkClickCount;
	}
}