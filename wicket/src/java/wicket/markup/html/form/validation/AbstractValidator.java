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
package wicket.markup.html.form.validation;

import java.util.HashMap;
import java.util.Map;

import wicket.Component;
import wicket.Localizer;
import wicket.WicketRuntimeException;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.model.IModel;
import wicket.model.MapModel;
import wicket.util.lang.Classes;

/**
 * Base class for form component validators.
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public abstract class AbstractValidator implements IValidator
{
	/**
	 * Returns a formatted validation error message for a given component. The
	 * error message is retrieved from a message bundle associated with the page
	 * in which this validator is contained. The resource key must be of the
	 * form: [form-name].[component-name].[validator-class]. For example, in the
	 * SignIn page's SignIn.properties file, you might find an entry:
	 * signInForm.password.RequiredValidator=A password is required Entries can
	 * contain optional ognl variable interpolations from the component, such
	 * as: editBook.name.LengthValidator='${input}' is too short a name.
	 * <p>
	 * Available variables for interpolation are:
	 * <ul>
	 * <li>${input}: the user's input</li>
	 * <li>${name}: the name of the component</li>
	 * </ul>
	 * </p>
	 * 
	 * @param input
	 *            the input (that caused the error)
	 * @param component
	 *            The component where the error occurred
	 */
	public void error(final FormComponent component, final String input)
	{
		error(component, resourceKey(component), input);
	}

	/**
	 * Returns a formatted validation error message for a given component. The
	 * error message is retrieved from a message bundle associated with the page
	 * in which this validator is contained using the given resource key. The
	 * resourceModel is used for variable interpolation.
	 * 
	 * @param resourceKey
	 *            the resource key to be used for the message
	 * @param resourceModel
	 *            the model for variable interpolation
	 * @param component
	 *            The component where the error occurred
	 */
	public void error(final FormComponent component, final String resourceKey,
			final IModel resourceModel)
	{
		// Return formatted error message
		Localizer localizer = component.getLocalizer();
		String message = localizer.getString(resourceKey, component, resourceModel);
		component.error(message);
	}

	/**
	 * Returns a formatted validation error message for a given component. The
	 * error message is retrieved from a message bundle associated with the page
	 * in which this validator is contained using the given resource key. The
	 * resourceModel is used for variable interpolation.
	 * 
	 * @param resourceKey
	 *            the resource key to be used for the message
	 * @param map
	 *            the model for variable interpolation
	 * @param component
	 *            The component where the error occurred
	 */
	public void error(final FormComponent component, final String resourceKey, final Map map)
	{
		error(component, resourceKey, MapModel.valueOf(map));
	}

	/**
	 * Returns a formatted validation error message for a given component. The
	 * error message is retrieved from a message bundle associated with the page
	 * in which this validator is contained using the given resource key.
	 * <p>
	 * The available variables for interpolation are by default:
	 * <ul>
	 * <li>${input}: the user's input</li>
	 * <li>${name}: the name of the component</li>
	 * </ul>
	 * Optionally, you can either override messageModel, or provide a model or a
	 * map with those variables yourself by using one of the other errorMessage
	 * methods.
	 * </p>
	 * 
	 * @param resourceKey
	 *            the resource key to be used for the message
	 * @param input
	 *            the input (that caused the error)
	 * @param component
	 *            The component where the error occurred
	 */
	public void error(final FormComponent component, final String resourceKey, final String input)
	{
		error(component, resourceKey, messageModel(component, input));
	}

	/**
	 * Gets the default variables for interpolation. These are:
	 * <ul>
	 * <li>${input}: the user's input</li>
	 * <li>${name}: the name of the component</li>
	 * </ul>
	 * 
	 * @param input
	 *            the user's input
	 * @param component
	 *            the component
	 * @return a map with the variables for interpolation
	 */
	protected Map messageModel(final FormComponent component, final String input)
	{
		final Map resourceModel = new HashMap(4);
		resourceModel.put("input", input);
		resourceModel.put("name", component.getName());
		return resourceModel;
	}

	/**
	 * Gets the resource key based on the form component. It will have the form:
	 * <code>[form-name].[component-name].[validator-class]</code>
	 * 
	 * @param component
	 *            the form component
	 * @return the resource key based on the form component
	 */
	protected String resourceKey(final FormComponent component)
	{
		// Resource key must be <form-name>.<component-name>.<validator-class>
		final Component parentForm = component.findParent(Form.class);
		if (parentForm != null)
		{
			return parentForm.getName() + "." + component.getName() + "."
					+ Classes.name(getClass());
		}
		else
		{
			throw new WicketRuntimeException("Unable to find Form parent for FormComponent "
					+ component);
		}
	}
}
