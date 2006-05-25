/*
 * $Id: RefreshingPage.java 5838 2006-05-24 20:44:49 +0000 (Wed, 24 May 2006)
 * joco01 $ $Revision$ $Date: 2006-05-24 20:44:49 +0000 (Wed, 24 May
 * 2006) $
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.examples.repeater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wicket.AttributeModifier;
import wicket.Component;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.extensions.markup.html.repeater.refreshing.RefreshingView;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.model.AbstractReadOnlyModel;

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
			contacts.add(dp.model((Contact)it.next()));
		}

		// create the refreshing view
		RefreshingView view = new RefreshingView(this, "view")
		{
			/**
			 * Return an iterator over models for items in the view
			 */
			@Override
			protected Iterator getItemModels()
			{
				return contacts.iterator();
			}

			@Override
			protected void populateItem(final Item item)
			{
				Contact contact = (Contact)item.getModelObject();
				item.add(new Label(item, "itemid", item.getId()));
				item.add(new ActionPanel(item, "actions", item.getModel()));
				item.add(new Label(item, "contactid", String.valueOf(contact.getId())));
				item.add(new Label(item, "firstname", contact.getFirstName()));
				item.add(new Label(item, "lastname", contact.getLastName()));
				item.add(new Label(item, "homephone", contact.getHomePhone()));
				item.add(new Label(item, "cellphone", contact.getCellPhone()));

				item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel()
				{
					@Override
					public Object getObject(Component component)
					{
						return (item.getIndex() % 2 == 1) ? "even" : "odd";
					}
				}));
			}
		};

		add(view);

		add(new Link(this, "refreshLink")
		{
			@Override
			public void onClick()
			{
				// noop
			}
		});
	}
}
