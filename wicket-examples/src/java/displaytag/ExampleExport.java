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
package displaytag;

import java.util.List;

import com.voicetribe.util.time.Time;
import com.voicetribe.wicket.PageParameters;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.table.ListItem;

import displaytag.export.CsvView;
import displaytag.export.ExcelView;
import displaytag.export.ExportLink;
import displaytag.export.XmlView;
import displaytag.utils.ListObject;
import displaytag.utils.TableWithAlternatingRowStyle;
import displaytag.utils.TestList;

/**
 * How to support exporting table data
 * 
 * @author Juergen Donnerstag
 */
public class ExampleExport extends Displaytag
{
    /**
     * Constructor.
     * 
     * @param parameters Page parameters
     */
    public ExampleExport(final PageParameters parameters)
    {
        // Test data
        final List data = new TestList(6, false);
        
        // Add the table
        TableWithAlternatingRowStyle table = new TableWithAlternatingRowStyle("rows", data)
        {
            public void populateItem(final ListItem listItem)
            {
                final ListObject value = (ListObject) listItem.getModelObject();

                listItem.add(new Label("id", new Integer(value.getId())));
                listItem.add(new Label("email", value.getEmail()));
                listItem.add(new Label("status", value.getStatus()));
                listItem.add(new Label("date", Time.valueOf(value.getDate()).toDateString()));
            }
        };
        
        add(table);
        
        // Add the export links
        add(new ExportLink("exportCsv", data, new CsvView(data, true, false, false)));
        add(new ExportLink("exportExcel", data, new ExcelView(data, true, false, false)));
        add(new ExportLink("exportXml", data, new XmlView(data, true, false, false)));
    }
}