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
package wicket.extensions.markup.html.repeater.data.sort;

import wicket.extensions.markup.html.repeater.data.IDataProvider;

/**
 * 
 * @author Igor Vaynberg
 */
public abstract class SortableDataProvider implements IDataProvider, ISortStateLocator
{
	private SingleSortState state = new SingleSortState();

	/**
	 * @see wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator#getSortState()
	 */
	public final ISortState getSortState()
	{
		return state;
	}

	/**
	 * @see wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator#setSortState(wicket.extensions.markup.html.repeater.data.sort.ISortState)
	 */
	public final void setSortState(ISortState state)
	{
		if (!(state instanceof SingleSortState))
		{
			throw new IllegalArgumentException(
					"argument [state] must be an instance of SingleSortState, but it is ["
							+ state.getClass().getName() + "]:[" + state.toString() + "]");
		}
		this.state = (SingleSortState)state;
	}

	/**
	 * Returns current sort state
	 * 
	 * @return current sort state
	 */
	public SortParam getSort()
	{
		return state.getSort();
	}

	/**
	 * Sets the current sort state
	 * 
	 * @param param
	 *            parameter containing new sorting information
	 */
	public void setSort(SortParam param)
	{
		state.setSort(param);
	}

	/**
	 * Sets the current sort state
	 * 
	 * @param property
	 *            sort property
	 * @param ascending
	 *            sort direction
	 */
	public void setSort(String property, boolean ascending)
	{
		setSort(new SortParam(property, ascending));
	}


}
