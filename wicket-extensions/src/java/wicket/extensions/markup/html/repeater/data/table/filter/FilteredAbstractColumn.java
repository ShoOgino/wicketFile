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
package wicket.extensions.markup.html.repeater.data.table.filter;

import wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * A helper implementation for a filtered column.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class FilteredAbstractColumn extends AbstractColumn implements IFilteredColumn
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param displayModel
	 *            model used to display the header text of this column
	 * 
	 * @param sortProperty
	 *            sort property this column represents
	 */
	public FilteredAbstractColumn(IModel displayModel, String sortProperty)
	{
		super(displayModel, sortProperty);
	}

	/**
	 * Constructor
	 * 
	 * @param displayModel
	 *            model used to display the header text of this column
	 */
	public FilteredAbstractColumn(Model displayModel)
	{
		super(displayModel);
	}


}
