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
package wicket.markup.html.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import wicket.RequestCycle;
import wicket.markup.ComponentTagAttributeModifier;
import wicket.markup.html.HtmlContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.table.ListItem;
import wicket.markup.html.table.ListView;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * A Tree that renders as a flat list, using spacers and nodes.
 * <p>
 * The visible tree rows are put in one flat list. For each row, a list is constructed
 * with fillers, that can be used to create indentation. After the fillers, the actual
 * node content is put.
 * </p>
 * <p>
 * For example:
 * <pre>
 * &lt;span id="spacers"&gt;&lt;/span&gt;&lt;span id=" spacers"&gt;&lt;/span&gt;
 * &lt;span id ="node"&gt;&lt;span id="label"&gt;foo&lt;/span&gt;&lt;/span&gt;
 * </pre>
 * Could be one row, where the node is on level two (hence the two spacer elements).
 * </p>
 * <p>
 * If you combine this with CSS like:
 * <pre>
 *	#spacers {
 *		padding-left: 16px;
 *	}
 * </pre>
 * and you have an indented tree.
 * </p>
 *
 * @author Eelco Hillenius
 */
public abstract class FlatTree extends AbstractTree
{ // TODO finalize javadoc
    /** table for the current visible tree paths. */
    private ListView visibleTreePathTable;

    /**
     * Constructor.
     * @param componentName The name of this container
     * @param model the underlying tree model
     */
    public FlatTree(final String componentName, final TreeModel model)
    {
        super(componentName, model);
    }

    /**
     * Constructor.
     * @param componentName The name of this container
     * @param model the underlying tree model
     * @param makeTreeModelUnique whether to make the user objects of the tree model
     * unique. If true, the default implementation will wrapp all user objects in
     * instances of {@link IdWrappedUserObject}. If false, users must ensure that the
     * user objects are unique within the tree in order to have the tree working properly
     */
    public FlatTree(final String componentName, final TreeModel model,
    		final boolean makeTreeModelUnique)
    {
        super(componentName, model, makeTreeModelUnique);
    }

    /**
     * Constructor using the given tree state. This tree state holds the tree model and
     * the currently visible paths.
     * @param componentName The name of this container
     * @param treeState the tree state that holds the tree model and the currently visible
     * paths
     */
    public FlatTree(final String componentName, TreeStateCache treeState)
    {
        super(componentName, treeState);
    }

    /**
     * Builds the structures needed to display the currently visible tree paths.
     * @param treeState the current tree state
     */
    protected final void applySelectedPaths(TreeStateCache treeState)
    {
        Enumeration e = treeState.getVisiblePathsFromRoot();
        List visiblePathsList = new ArrayList();
        boolean skip = (!treeState.isRootVisible());
        while (e.hasMoreElements())
        {
        	TreePath path = (TreePath)e.nextElement();
        	if(skip) // skip root if not visible
        	{
        		skip = false;
        		continue;
        	}
            DefaultMutableTreeNode treeNode =
                (DefaultMutableTreeNode)path.getLastPathComponent();
            TreeNodeModel treeNodeModel = new TreeNodeModel(treeNode, treeState, path);
            visiblePathsList.add(treeNodeModel);
        }
        Model model = new Model((Serializable) visiblePathsList);

        if (visibleTreePathTable == null)
        {
        	visibleTreePathTable = newVisibleTreePathListView(model);
            add(visibleTreePathTable);
        }
        else
        {
            visibleTreePathTable.removeAll();
            visibleTreePathTable.setModel(model);
        }
    }

	/**
	 * Creates a {@link ListView} to use for rendering the visible tree rows.
	 * The model parameter should be used as the model for the <code>ListView</code>.
	 * @param model a model that contains a list of {@link TreeNodeModel}s
	 * @return a new <code>ListView</code> that uses the provided model
	 */
	protected final ListView newVisibleTreePathListView(Model model)
	{
		return new VisibleTreePathListView("tree", model);
	}

	/**
	 * Creates a junction link.
	 * @param node the model of the node
	 * @return link for expanding/ collapsing the tree
	 */
	private final TreeNodeLink createJunctionLink(TreeNodeModel node)
	{
        TreeNodeLink junctionLink = new TreeNodeLink(
        		"junctionLink", this, node){

            public void linkClicked(RequestCycle cycle, TreeNodeModel node)
            {
            	junctionLinkClicked(cycle, node);
            }   
        };
		HtmlContainer junctionImg = new HtmlContainer("junctionImg");
		junctionImg.add(new ComponentTagAttributeModifier(
        		"src", true, new Model(getJunctionImageName(node))));
		junctionLink.add(junctionImg);
		return junctionLink;
	}

    /**
     * Get image name for junction; used by method createExpandCollapseLink.
     * @param node the model with the current node
     * @return image name
     */
    protected abstract String getJunctionImageName(TreeNodeModel node);

	/**
	 * Handler that is called when a junction link is clicked; this implementation
	 * sets the expanded state to one that corresponds with the node selection.
	 * @param cycle the current request cycle
	 * @param node the tree node model
	 */
	protected void junctionLinkClicked(RequestCycle cycle, TreeNodeModel node)
	{
        setExpandedState(node);
	}

	/**
	 * Creates a node link.
	 * @param node the model of the node
	 * @return link for selection
	 */
	private final TreeNodeLink createNodeLink(TreeNodeModel node)
	{
		Object userObject = node.getTreeNode().getUserObject();
        TreeNodeLink nodeLink = new TreeNodeLink("nodeLink", this, node){

            public void linkClicked(RequestCycle cycle, TreeNodeModel node)
            {
            	nodeLinkClicked(cycle, node);
            }   
        };
		HtmlContainer nodeImg = new HtmlContainer("nodeImg");
		nodeImg.add(new ComponentTagAttributeModifier(
        		"src", true, new Model(getNodeImageName(node))));
		nodeLink.add(nodeImg);
		String userObjectAsString = getNodeLabel(node);
		nodeLink.add(new Label("label", userObjectAsString));
		return nodeLink;
	}

	/**
	 * Gets the label of the node that is used for the node link.
	 * Defaults to treeNodeModel.getUserObject().toString(); override to provide
	 * a custom label
	 * @param node the tree node model
	 * @return the label of the node that is used for the node link
	 */
	protected abstract String getNodeLabel(TreeNodeModel node);

    /**
     * Get image name for node; used by method createExpandCollapseLink.
     * @param node the model with the current node
     * @return image name
     */
    protected abstract String getNodeImageName(TreeNodeModel node);

	/**
	 * Handler that is called when a node link is clicked; this implementation
	 * sets the expanded state just as a click on a junction would do.
	 * Override this for custom behaviour.
	 * @param cycle the current request cycle
	 * @param node the tree node model
	 */
	protected void nodeLinkClicked(RequestCycle cycle, TreeNodeModel node)
	{
        setExpandedState(node);
	}

    /**
     * Table for visible tree paths.
     */
    private final class VisibleTreePathListView extends ListView
    {
        /**
         * Construct.
         * @param name name of the component
         * @param model the model
         */
        public VisibleTreePathListView(String name, IModel model)
        {
            super(name, model);
        }

        /**
         * @see wicket.markup.html.table.ListView#populateItem(wicket.markup.html.table.ListItem)
         */
        protected void populateItem(ListItem listItem)
        {
            TreeNodeModel treeNodeModel = (TreeNodeModel)listItem.getModelObject();

            // add spacers
            int level = treeNodeModel.getLevel();
            List spacerList = new ArrayList(level);
            for(int i = 0; i < level; i++)
            {
                spacerList.add(treeNodeModel);
            }
            listItem.add(new SpacerList("spacers", spacerList));

            // add node
            HtmlContainer nodeContainer = new HtmlContainer("node");
            Serializable userObject = treeNodeModel.getUserObject();

            if (userObject == null)
            {
                throw new RuntimeException("userObject == null");
            }
            TreeNodeLink expandCollapsLink = createJunctionLink(treeNodeModel);
            nodeContainer.add(expandCollapsLink);

            TreeNodeLink selectLink = createNodeLink(treeNodeModel);
            nodeContainer.add(selectLink);
            listItem.add(nodeContainer);
        }
    }

    /**
     * Renders spacer items.
     */
    private final class SpacerList extends ListView
    {
        /**
         * Construct.
         * @param componentName name of the component
         * @param list list
         */
        public SpacerList(String componentName, List list)
        {
            super(componentName, list);
        }

        /**
         * @see wicket.markup.html.table.ListView#populateItem(wicket.markup.html.table.ListItem)
         */
        protected void populateItem(ListItem listItem)
        {
        	// nothing needed; we just render the tags and use CSS to indent
        }
    }
}
