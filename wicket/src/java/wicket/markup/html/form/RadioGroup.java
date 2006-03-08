/*
 * $Id$ $Revision:
 * 1.9 $ $Date$
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
package wicket.markup.html.form;

import wicket.WicketRuntimeException;
import wicket.markup.html.WebMarkupContainer;
import wicket.model.IModel;

/**
 * Component used to connect instances of Radio components into a group.
 * Instances of Radio have to be in the component hierarchy somewhere below the
 * group component. The model object of the gorup is set to the model object of
 * the selected radio component or null if none selected.
 * 
 * ie
 * 
 * <code>
 * <span wicket:id="radiochoicegroup">
 *   ...
 *   <input type="radio" wicket:id="singleradiochoice1">choice 1</input>
 *   ...
 *   <input type="radio" wicket:id="singleradiochoice2">choice 2</input>
 *   ...
 * </span>
 * </code>
 * 
 * <p>
 * Note: This component does not support cookie persistence
 * 
 * 
 * @author Igor Vaynberg (ivaynberg@users.sf.net)
 * @author Sven Meier (svenmeier)
 * 
 */
public class RadioGroup extends FormComponent implements IOnChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see WebMarkupContainer#WebMarkupContainer(String)
	 */
	public RadioGroup(String id)
	{
		super(id);
		setRenderBodyOnly(true);
	}

	/**
	 * @see WebMarkupContainer#WebMarkupContainer(String, IModel)
	 */
	public RadioGroup(String id, IModel model)
	{
		super(id, model);
		setRenderBodyOnly(true);
	}

	protected boolean wantOnSelectionChangedNotifications()
	{
		return false;
	}

	/**
	 * @see FormComponent#updateModel()
	 */
	public void updateModel()
	{
		/*
		 * the input value contains the full path of the radio unless no choice
		 * was selected in which case the input contains null
		 */
		String path = getInput();

		if (path != null)
		{
			/*
			 * single radio choice component path sans group path = relative
			 * path from group to choice since we know the choice is child of
			 * group
			 */
			path = path.substring(getPath().length() + 1);

			// retrieve the selected single radio choice component
			Radio choice = (Radio)get(path);

			if (choice == null)
			{
				throw new WicketRuntimeException(
						"submitted http post value ["
								+ path
								+ "] for RadioGroup component ["
								+ getPath()
								+ "] is illegal because it does not contain relative path to a Radio componnet. "
								+ "Due to this the RadioGroup component cannot resolve the selected Radio component pointed to by the illegal value. A possible reason is that componment hierarchy changed between rendering and form submission.");
			}


			// assign the value of the group's model
			setModelObject(choice.getModelObject());
		}
		else
		{
			// no choice selected - set model object to null
			setModelObject(null);
		}
	}

	/**
	 * Called when a selection changes.
	 */
	public final void onSelectionChanged()
	{
		updateModel();
		onSelectionChanged(getModelObject());
	}

	/**
	 * Template method that can be overriden by clients that implement
	 * IOnChangeListener to be notified by onChange events of a select element.
	 * This method does nothing by default.
	 * <p>
	 * Called when a option is selected of a dropdown list that wants to be
	 * notified of this event. This method is to be implemented by clients that
	 * want to be notified of selection events.
	 * 
	 * @param newSelection
	 *            The newly selected object of the backing model NOTE this is
	 *            the same as you would get by calling getModelObject() if the
	 *            new selection were current
	 */
	protected void onSelectionChanged(final Object newSelection)
	{
	}
	
	protected final boolean supportsPersistence()
	{
		return false;
	}
}
