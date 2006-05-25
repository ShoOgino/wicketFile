/*
 * $Id: OrderedRepeatingView.java 5840 2006-05-24 20:49:09 +0000 (Wed, 24 May
 * 2006) joco01 $ $Revision$ $Date: 2006-05-24 20:49:09 +0000 (Wed, 24
 * May 2006) $
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.extensions.markup.html.repeater;

import java.util.ArrayList;
import java.util.List;

import wicket.Component;
import wicket.MarkupContainer;
import wicket.model.IModel;

/**
 * This view has been deprecated. It is no longer needed since wicket will now
 * guarantee that the order in which MarkupContainer.iterator() returns child
 * components is the order in which they were added. Use {@link RepeatingView}
 * instead.
 * 
 * 
 * A repeater view that renders all of its children, using its body markup, in
 * the order they were added.
 * 
 * For an example see {@link RepeatingView}
 * 
 * @see RepeatingView
 * 
 * @deprecated
 * 
 * @author Igor Vaynberg ( ivaynberg )
 * 
 * TODO Post 1.2: Remove this class
 * 
 */
@Deprecated
public class OrderedRepeatingView extends RepeatingView
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** List of ids of children components in the order they were added */
	private List order = new ArrayList();

	/** @see Component#Component(MarkupContainer,String) */
	public OrderedRepeatingView(MarkupContainer parent, final String id)
	{
		super(parent, id);
	}

	/** @see Component#Component(MarkupContainer,String, IModel) */
	public OrderedRepeatingView(MarkupContainer parent, final String id, IModel model)
	{
		super(parent, id, model);
	}

}