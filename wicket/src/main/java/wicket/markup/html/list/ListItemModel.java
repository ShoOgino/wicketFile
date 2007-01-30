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
package wicket.markup.html.list;

import wicket.model.LoadableDetachableModel;

/**
 * Model for list items.
 * 
 * @param <T>
 *            Type of model object this model holds
 * 
 */
public class ListItemModel<T> extends LoadableDetachableModel<T>
{
	private static final long serialVersionUID = 1L;

	/** The ListView's list model */
	private final ListView<T> listView;

	/** The list item's index */
	private final int index;

	/**
	 * Construct
	 * 
	 * @param listView
	 *            The ListView
	 * @param index
	 *            The index of this model
	 */
	public ListItemModel(final ListView<T> listView, final int index)
	{
		this.listView = listView;
		this.index = index;
	}


	@Override
	protected T load()
	{
		return listView.getModelObject().get(index);
	}
}
