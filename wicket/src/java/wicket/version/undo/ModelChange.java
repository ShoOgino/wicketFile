/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.version.undo;

import wicket.Component;
import wicket.markup.html.form.FormComponent;
import wicket.model.IModel;
import wicket.util.lang.Objects;

/**
 * A model change operation.
 * 
 * @author Jonathan Locke
 */
class ModelChange extends Change
{
	private final Component component;
	private IModel originalModel;

	ModelChange(final Component component)
	{
		// Save component
		this.component = component;

		// Get component model
		final IModel model = component.getModel();

		// If the component has a model, it's about to change!
		if (model != null)
		{
			// Should we clone the model?
			boolean cloneModel = true;

			// If the component is a form component
			if (component instanceof FormComponent)
			{
				// and it's using the same model as the form
				if (((FormComponent)component).getForm().getModel() == model)
				{
					// we don't need to clone the model, because it will
					// be re-initialized using initModel()
					cloneModel = false;
				}
			}
			else
			{
				// If the component is using the same model as the page
				if (component.getPage().getModel() == model)
				{
					// we don't need to clone the model, because it will
					// be re-initialized using initModel()
					cloneModel = false;
				}
			}

			// Clone model?
			if (cloneModel)
			{
				model.detach();
				originalModel = (IModel)Objects.clone(model);
			}
		}
	}

	void undo()
	{
		component.setModel(originalModel);
	}
}
