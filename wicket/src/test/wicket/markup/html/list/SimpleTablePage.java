/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup.html.list;

import java.util.ArrayList;
import java.util.List;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;


/**
 * Dummy page used for resource testing.
 */
public class SimpleTablePage extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 *  page parameters.
	 */
	public SimpleTablePage()
	{
		super();
		List list = new ArrayList();
		list.add("one");
		list.add("two");
		list.add("three");
		add(new ListView("table", list)
		{
			private static final long serialVersionUID = 1L;
			
			protected void populateItem(ListItem listItem)
			{
				String txt = (String)listItem.getModelObject();
				listItem.add(new Label("txt", txt));
			}
		});
	}
}
