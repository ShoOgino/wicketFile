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
package wicket.examples.filebrowser;

import javax.swing.tree.TreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.PageParameters;
import wicket.examples.util.NavigationPanel;
import wicket.markup.ComponentTagAttributeModifier;
import wicket.markup.html.HtmlPage;
import wicket.markup.html.panel.Panel;
import wicket.markup.html.tree.Tree;
import wicket.markup.html.tree.TreeNodeModel;
import wicket.markup.html.tree.TreeRowReplacementModel;

/**
 * Tree example that uses the user-home dirs to populate the tree.
 *
 * @author Eelco Hillenius
 */
public class FileBrowser extends HtmlPage
{
    /** Log. */
    private static Log log = LogFactory.getLog(FileBrowser.class);

    /**
     * Constructor.
     * @param parameters Page parameters
     */
    public FileBrowser(final PageParameters parameters)
    {
        add(new NavigationPanel("mainNavigation", "Filebrowser example"));

        TreeModel model = new FileModelProvider().getFileModel();
        Tree fileTree = new Tree("fileTree", model){

            /**
             * Override to provide a custom row panel.
             * @see wicket.markup.html.tree.Tree#getTreeRowPanel(java.lang.String, wicket.markup.html.tree.TreeNodeModel)
             */
            protected Panel getTreeRowPanel(String componentName, TreeNodeModel nodeModel)
            {
                TreeRowReplacementModel replacementModel =
                    new TreeRowReplacementModel(nodeModel);
                Panel rowPanel = new FileTreeRow(componentName, this, nodeModel);
                rowPanel.add(new ComponentTagAttributeModifier(
                        "class", true, replacementModel));
                return rowPanel;
            }   
        };
        add(fileTree);
    }
}
