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
package org.apache.wicket.extensions.markup.html.repeater.data.table.filter;

import org.apache.wicket.model.Model;

/**
 * Model that wraps filter state locator to make its use transparent to wicket components.
 * <p>
 * Example:
 * 
 * <pre>
 * IFilterStateLocator locator = getLocator();
 * TextField tf = new TextField(&quot;tf&quot;, new FilterStateModel(locator));
 * </pre>
 * 
 * Text field tf will now user the object that filter state locator locates as its underlying model.
 * </p>
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
class FilterStateModel extends Model
{
	private static final long serialVersionUID = 1L;

	private IFilterStateLocator locator;

	/**
	 * Constructor
	 * 
	 * @param locator
	 *            IFilterStateLocator implementation used to provide model object for this model
	 */
	public FilterStateModel(IFilterStateLocator locator)
	{
		this.locator = locator;
	}

	/**
	 * @see org.apache.wicket.model.IModel#getObject()
	 */
	public Object getObject()
	{
		return locator.getFilterState();
	}

	/**
	 * @see org.apache.wicket.model.IModel#setObject(java.lang.Object)
	 */
	public void setObject(Object object)
	{
		locator.setFilterState(object);
	}

}
