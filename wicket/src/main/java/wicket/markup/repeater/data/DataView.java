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
package wicket.markup.repeater.data;

import wicket.MarkupContainer;
import wicket.markup.html.navigation.paging.IPageable;


/**
 * DataView is a basic implementation of AbstractDataView.
 * 
 * Data views aim to make it very simple to populate your repeating view from a
 * database by utilizing {@link IDataProvider} to act as an interface between
 * the database and the dataview.
 * 
 * 
 * 
 * <p>
 * Example:
 * 
 * <pre>
 *       &lt;tbody&gt;
 *         &lt;tr wicket:id=&quot;rows&quot;&gt;
 *           &lt;td&gt;&lt;span wicket:id=&quot;id&quot;&gt;Test ID&lt;/span&gt;&lt;/td&gt;
 *           ...    
 * </pre>
 * 
 * <p>
 * Though this example is about a HTML table, DataView is not at all limited to
 * HTML tables. Any kind of list can be rendered using DataView.
 * <p>
 * And the related Java code:
 * 
 * <pre>
 * add(new DataView(&quot;rows&quot;, dataProvider)
 * {
 * 	public void populateItem(final Item item)
 * 	{
 * 		final UserDetails user = (UserDetails)item.getModelObject();
 * 		item.add(new Label(&quot;id&quot;, user.getId()));
 * 	}
 * });
 * </pre>
 * 
 * @see IDataProvider
 * @see IPageable
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 * @param <T> 
 * 			Type of model object this component holds 
 */
public abstract class DataView<T> extends DataViewBase<T>
{

	/**
	 * @param parent
	 *            The parent of this component The parent of this component.
	 * @param id
	 *            component id
	 * @param dataProvider
	 *            data provider
	 */
	public DataView(MarkupContainer<?> parent, final String id, IDataProvider<T> dataProvider)
	{
		super(parent, id, dataProvider);
	}

	/**
	 * @param parent
	 *            The parent of this component The parent of this component.
	 * @param id
	 *            component id
	 * @param dataProvider
	 *            data provider
	 * @param itemsPerPage
	 *            items per page
	 */
	public DataView(MarkupContainer<?> parent, final String id, IDataProvider<T> dataProvider,
			int itemsPerPage)
	{
		super(parent, id, dataProvider);
		setItemsPerPage(itemsPerPage);
	}

	/**
	 * Sets the number of items to be displayed per page
	 * 
	 * @param items
	 *            number of items to display per page
	 */
	public void setItemsPerPage(int items)
	{
		internalSetRowsPerPage(items);
	}

	/**
	 * @return number of items displayed per page
	 */
	public int getItemsPerPage()
	{
		return internalGetRowsPerPage();
	}

	/**
	 * @return data provider
	 */
	public IDataProvider<T> getDataProvider()
	{
		return internalGetDataProvider();
	}

}
