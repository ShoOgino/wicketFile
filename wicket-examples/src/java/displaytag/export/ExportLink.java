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
package displaytag.export;

import java.util.List;

import com.voicetribe.wicket.Page;
import com.voicetribe.wicket.RequestCycle;
import com.voicetribe.wicket.markup.html.link.Link;

/**
 * Define action if Link is selected
 * 
 * @author Juergen Donnerstag
 */
public class ExportLink extends Link
{
    final private List data;
    final private BaseExportView exportView; 
    
    public ExportLink(final String componentName, final List data, final BaseExportView exportView)
    {
        super(componentName);
        this.data = data;
        this.exportView = exportView;
    }
    
    public void linkClicked(final RequestCycle cycle)
    {
        new Export().doExport(cycle, exportView, data);
        
        // rendering completed
        cycle.setPage((Page)null);
    }
}
