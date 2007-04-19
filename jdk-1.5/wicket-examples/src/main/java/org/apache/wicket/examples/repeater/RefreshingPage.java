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
package org.apache.wicket.examples.repeater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.AbstractReadOnlyModel;


/**
 * page that demonstrates a RefreshingView
 * 
 * @see RefreshingView
 * 
 * @author igor
 */
public class RefreshingPage extends BasePage
{
	/**
	 * Constructor
	 */
	public RefreshingPage()
	{
		final List contacts = new ArrayList(10);

		// populate list of contacts to be displayed
		ContactDataProvider dp = new ContactDataProvider();
		Iterator it = dp.iterator(0, 10);
		while (it.hasNext())
		{
			contacts.add(dp.model(it.next()));
		}

		// create the refreshing view
		RefreshingView view = new RefreshingView("view")
		{
			/**
			 * Return an iterator over models for items in the view
			 */
			protected Iterator getItemModels()
			{
				return contacts.iterator();
			}

			protected void populateItem(final Item item)
			{
				Contact contact = (Contact)item.getModelObject();
				item.add(new Label("itemid", item.getId()));
				item.add(new ActionPanel("actions", item.getModel()));
				item.add(new Label("contactid", String.valueOf(contact.getId())));
				item.add(new Label("firstname", contact.getFirstName()));
				item.add(new Label("lastname", contact.getLastName()));
				item.add(new Label("homephone", contact.getHomePhone()));
				item.add(new Label("cellphone", contact.getCellPhone()));

				item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel()
				{
					public Object getObject()
					{
						return (item.getIndex() % 2 == 1) ? "even" : "odd";
					}
				}));
			}
		};

		add(view);

		add(new Link("refreshLink")
		{
			public void onClick()
			{
				// noop
			}
		});
	}
}
