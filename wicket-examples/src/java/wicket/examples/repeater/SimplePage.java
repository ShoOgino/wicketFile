/*
 * $Id$ $Revision$
 * $Date$
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

import wicket.AttributeModifier;
import wicket.Component;
import wicket.extensions.markup.html.repeater.data.DataView;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.markup.html.basic.Label;
import wicket.model.AbstractReadOnlyModel;

/**
 * Page that demonstrates a simple dataview.
 * 
 * @see wicket.extensions.markup.html.repeater.data.DataView
 * 
 * @author igor
 */
public class SimplePage extends BasePage
{
	/**
	 * constructor
	 */
	public SimplePage()
	{
		new DataView(this, "simple", new ContactDataProvider())
		{
			@Override
			protected void populateItem(final Item item)
			{
				Contact contact = (Contact)item.getModelObject();
				new ActionPanel(item, "actions", item.getModel());
				new Label(item, "contactid", String.valueOf(contact.getId()));
				new Label(item, "firstname", contact.getFirstName());
				new Label(item, "lastname", contact.getLastName());
				new Label(item, "homephone", contact.getHomePhone());
				new Label(item, "cellphone", contact.getCellPhone());

				item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>()
				{
					@Override
					public String getObject(Component component)
					{
						return (item.getIndex() % 2 == 1) ? "even" : "odd";
					}
				}));
			}
		};
	}
}
