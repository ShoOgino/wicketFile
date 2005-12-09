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
package wicket.extensions.markup.html.repeater.data.table;

import wicket.AttributeModifier;
import wicket.Component;
import wicket.extensions.markup.html.repeater.OrderedRepeatingView;
import wicket.extensions.markup.html.repeater.data.IDataProvider;
import wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.navigation.paging.IPageable;
import wicket.markup.html.panel.Panel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.IModel;

/**
 * A data table builds on data grid view to introduce toolbars. Toolbars can be
 * used to display sortable column headers, paging information, filter controls,
 * and other information.
 * <p>
 * Data table also provides its own markup for an html table so the user does
 * not need to provide it himself. This makes it very simple to add a datatable
 * to the markup, however, some flexibility.
 * <p>
 * Example
 * 
 * <pre>
 *      &lt;table wicket:id=&quot;datatable&quot;&gt;&lt;/table&gt;
 * </pre>
 * 
 * And the related Java code: ( the first column will be sortable because its
 * sort property is specified, the second column will not )
 * 
 * <pre>
 * 
 * IColumn[] columns = new IColumn[2];
 * 
 * columns[0] = new PropertyColumn(new Model(&quot;First Name&quot;), &quot;name.first&quot;, &quot;name.first&quot;);
 * columns[1] = new PropertyColumn(new Model(&quot;Last Name&quot;), &quot;name.last&quot;);
 * 
 * DataTable table = new DataTable(&quot;datatable&quot;, columns, new UserProvider(), 10);
 * table.add(new NavigationToolbar(table));
 * table.add(new HeadersToolbar(table));
 * add(table);
 * 
 * </pre>
 * 
 * @see DefaultDataTable
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class DataTable extends Panel implements IPageable
{
	/**
	 * The component id that toolbars must be created with in order to be added
	 * to the data table
	 */
	public static final String TOOLBAR_COMPONENT_ID = "toolbar";

	private static final long serialVersionUID = 1L;

	private final DataGridView datagrid;

	private IColumn[] columns;

	private final OrderedRepeatingView topToolbars;
	private final OrderedRepeatingView bottomToolbars;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            component id
	 * @param columns
	 *            list of IColumn objects
	 * @param dataProvider
	 *            imodel for data provider
	 * @param rowsPerPage
	 *            number of rows per page
	 */
	public DataTable(String id, IColumn[] columns, IDataProvider dataProvider, int rowsPerPage)
	{
		super(id);

		this.columns = columns;

		datagrid = new DataGridView("rows", columns, dataProvider)
		{
			private static final long serialVersionUID = 1L;

			protected void postProcessRowItem(Item item)
			{
				final int idx = item.getIndex();

				final IModel model = new AbstractReadOnlyModel()
				{

					private static final long serialVersionUID = 1L;

					public Object getObject(Component component)
					{
						return (idx % 2 == 0) ? "odd" : "even";
					}


				};

				item.add(new AttributeModifier("class", true, model));
			}
		};
		datagrid.setRowsPerPage(rowsPerPage);
		add(datagrid);

		topToolbars = new OrderedRepeatingView("topToolbars")
		{
			private static final long serialVersionUID = 1L;

			public boolean isVisible()
			{
				return size() > 0;
			}

		};

		bottomToolbars = new OrderedRepeatingView("bottomToolbars")
		{

			private static final long serialVersionUID = 1L;

			public boolean isVisible()
			{
				return size() > 0;
			}
		};

		add(topToolbars);
		add(bottomToolbars);
	}

	/**
	 * @return array of column objects this table displays
	 */
	public final IColumn[] getColumns()
	{
		return columns;
	}

	/**
	 * Adds a toolbar to the datatable that will be displayed before the data
	 * 
	 * @param toolbar
	 *            toolbar to be added
	 * 
	 * @see AbstractToolbar
	 */
	public void addTopToolbar(AbstractToolbar toolbar)
	{
		addToolbar(toolbar, topToolbars);
	}

	/**
	 * Adds a toolbar to the datatable that will be displayed after the data
	 * 
	 * @param toolbar
	 *            toolbar to be added
	 * 
	 * @see AbstractToolbar
	 */
	public void addBottomToolbar(AbstractToolbar toolbar)
	{
		addToolbar(toolbar, bottomToolbars);
	}

	private void addToolbar(AbstractToolbar toolbar, OrderedRepeatingView container)
	{
		if (toolbar == null)
		{
			throw new IllegalArgumentException("argument [toolbar] cannot be null");
		}

		if (!toolbar.getId().equals(TOOLBAR_COMPONENT_ID))
		{
			throw new IllegalArgumentException(
					"Toolbar must have component id equal to AbstractDataTable.TOOLBAR_COMPONENT_ID");
		}

		// create a container item for the toolbar (required by repeating view)
		WebMarkupContainer item = new WebMarkupContainer(container.newChildId());
		item.setRenderBodyOnly(true);
		item.add(toolbar);

		container.add(item);
	}

	/**
	 * @see wicket.markup.html.navigation.paging.IPageable#getCurrentPage()
	 */
	public final int getCurrentPage()
	{
		return datagrid.getCurrentPage();
	}

	/**
	 * @see wicket.markup.html.navigation.paging.IPageable#setCurrentPage(int)
	 */
	public final void setCurrentPage(int page)
	{
		datagrid.setCurrentPage(page);
	}

	/**
	 * @see wicket.markup.html.navigation.paging.IPageable#getPageCount()
	 */
	public final int getPageCount()
	{
		return datagrid.getPageCount();
	}

	/**
	 * @return total number of rows in this table
	 */
	public final int getRowCount()
	{
		return datagrid.getRowCount();
	}

	/**
	 * @return number of rows per page
	 */
	public final int getRowsPerPage()
	{
		return datagrid.getRowsPerPage();
	}

}
