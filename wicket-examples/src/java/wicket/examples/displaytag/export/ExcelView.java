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
package wicket.examples.displaytag.export;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Export view for excel exporting.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class ExcelView extends BaseExportView
{
    /**
     * @see wicket.examples.displaytag.export.BaseExportView#BaseExportView(List, boolean, boolean, boolean)
     */
    public ExcelView(final List tableModel, final boolean exportFullList, final boolean includeHeader, final boolean decorateValues)
    {
        super(tableModel, exportFullList, includeHeader, decorateValues);
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getMimeType()
     * @return "application/vnd.ms-excel"
     */
    public String getMimeType()
    {
        return "application/vnd.ms-excel";
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getRowStart()
     * @return ""
     */
    protected String getRowStart()
    {
        return "";
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getRowEnd()
     * @return "\n"
     */
    protected String getRowEnd()
    {
        return "\n";
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getCellStart()
     * @return ""
     */
    protected String getCellStart()
    {
        return "";
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getCellEnd()
     * @return "\t"
     */
    protected String getCellEnd()
    {
        return "\t";
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getDocumentStart()
     * @return ""
     */
    protected String getDocumentStart()
    {
        return "";
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getDocumentEnd()
     * @return ""
     */
    protected String getDocumentEnd()
    {
        return "";
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getAlwaysAppendCellEnd()
     * @return false
     */
    protected boolean getAlwaysAppendCellEnd()
    {
        return false;
    }

    /**
     * @see wicket.examples.displaytag.export.BaseExportView#getAlwaysAppendRowEnd()
     * @return false
     */
    protected boolean getAlwaysAppendRowEnd()
    {
        return false;
    }

    /**
     * Escaping for excel format.
     * <ul>
     * <li>Quotes inside quoted strings are escaped with a double quote</li>
     * <li>Fields are surrounded by "" (should be optional, but sometimes you get a "Sylk error" without those)</li>
     * </ul>
     * @see wicket.examples.displaytag.export.BaseExportView#escapeColumnValue(java.lang.Object)
     */
    protected Object escapeColumnValue(Object value)
    {
        if (value != null)
        {
            // quotes around fields are needed to avoid occasional "Sylk format invalid" messages from excel
            return "\"" + StringUtils.replace(StringUtils.trim(value.toString()), "\"", "\"\"") + "\"";
        }

        return null;
    }

}
