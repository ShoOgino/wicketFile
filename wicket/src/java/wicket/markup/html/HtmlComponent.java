/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.markup.html;

import java.io.Serializable;

import wicket.Component;
import wicket.model.IModel;

/**
 * Base class for simple HTML components which do not hold nested components. If
 * you need to support nested components, see HtmlContainer or use Panel if the
 * component will have its own associated markup.
 * 
 * @see wicket.markup.html.HtmlContainer
 * @author Jonathan Locke
 */
public abstract class HtmlComponent extends Component
{
	/**
	 * @see Component#Component(String)
	 */
	public HtmlComponent(final String name)
	{
		super(name);
	}

	/**
	 * @see Component#Component(String, IModel)
	 */
	public HtmlComponent(final String name, final IModel model)
	{
		super(name, model);
	}

	/**
	 * @see Component#Component(String, IModel, String)
	 */
	public HtmlComponent(final String name, final IModel model, final String expression)
	{
		super(name, model, expression);
	}

	/**
	 * @see Component#Component(String, Serializable)
	 */
	public HtmlComponent(final String name, final Serializable object)
	{
		super(name, object);
	}

	/**
	 * @see Component#Component(String, Serializable, String)
	 */
	public HtmlComponent(final String name, final Serializable object, final String expression)
	{
		super(name, object, expression);
	}

	/**
	 * Renders this component.
	 */
	protected void handleRender()
	{
		renderComponent(findMarkupStream());
	}
}