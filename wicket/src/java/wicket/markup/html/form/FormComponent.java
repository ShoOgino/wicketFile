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
package wicket.markup.html.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wicket.markup.ComponentTag;
import wicket.markup.html.HtmlContainer;
import wicket.markup.html.form.validation.IValidator;
import wicket.util.lang.Classes;
import wicket.util.string.StringList;

/**
 * An html form component knows how to validate itself. Validators that
 * implement IValidator can be added to the component. They will be evaluated in
 * the order they were added and the first Validator that returns an error
 * message determines the error message returned by the component.
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public abstract class FormComponent extends HtmlContainer
{
	/**
	 * Whether this form component should save and restore state between
	 * sessions. This is false by default.
	 */
	private boolean persistent = false;

	/** The validator or validator list for this component. */
	private IValidator validator = IValidator.NULL;

	/**
	 * A convenient and memory efficent representation for a list of validators.
	 */
	static private final class ValidatorList implements IValidator
	{
		/**
		 * Left part of linked list.
		 */
		private final IValidator left;

		/**
		 * Right part of linked list.
		 */
		private IValidator right;

		/**
		 * Constructs a list with validators in it.
		 * 
		 * @param left
		 *            The left validator
		 * @param right
		 *            The right validator
		 */
		ValidatorList(final IValidator left, final IValidator right)
		{
			this.left = left;
			this.right = right;
		}

		/**
		 * Gets the string representation of this object.
		 * 
		 * @return String representation of this object
		 */
		public String toString()
		{
			final StringList stringList = new StringList();
			ValidatorList current = this;

			while (true)
			{
				stringList.add(Classes.name(current.left.getClass()) + " "
						+ current.left.toString());

				if (current.right instanceof ValidatorList)
				{
					current = (ValidatorList)current.right;
				}
				else
				{
					stringList.add(Classes.name(current.right.getClass()) + " "
							+ current.right.toString());

					break;
				}
			}

			return stringList.toString();
		}

		/**
		 * Validates the given component.
		 * 
		 * @param component
		 *            The component to validate
		 */
		public void validate(final FormComponent component)
		{
			left.validate(component);
			right.validate(component);
		}

		/**
		 * Adds the given code validator to this list of code validators.
		 * 
		 * @param validator
		 *            The validator
		 */
		void add(final IValidator validator)
		{
			ValidatorList current = this;

			while (current.right instanceof ValidatorList)
			{
				current = (ValidatorList)current.right;
			}

			current.right = new ValidatorList(current.right, validator);
		}

		/**
		 * Gets the validators as a List.
		 * 
		 * @return the validators as a List
		 */
		List asList()
		{
			ValidatorList current = this;
			List validators = new ArrayList();
			while (true)
			{
				validators.add(current.left);
				if (current.right instanceof ValidatorList)
				{
					current = (ValidatorList)current.right;
				}
				else
				{
					validators.add(current.right);
					break;
				}
			}
			return validators;
		}
	}

	/**
	 * @see wicket.Component#Component(String)
	 */
	public FormComponent(final String componentName)
	{
		super(componentName);
	}

	/**
	 * @see wicket.Component#Component(String, Serializable)
	 */
	public FormComponent(String name, Serializable object)
	{
		super(name, object);
	}

	/**
	 * @see wicket.Component#Component(String, Serializable, String)
	 */
	public FormComponent(String name, Serializable object, String expression)
	{
		super(name, object, expression);
	}

	/**
	 * Adds a validator to this form component.
	 * 
	 * @param validator
	 *            The validator
	 * @return This
	 */
	public final FormComponent add(final IValidator validator)
	{
		// If we don't yet have a validator
		if (this.validator == IValidator.NULL)
		{
			// Just add the validator directly
			this.validator = validator;
		}
		else
		{
			// Create a validator list?
			if (this.validator instanceof ValidatorList)
			{
				// Already have a list. Just add new validator to list
				((ValidatorList)this.validator).add(validator);
			}
			else
			{
				// Create a set of the current validator and the new validator
				this.validator = new ValidatorList(this.validator, validator);
			}
		}
		return this;
	}

	/**
	 * Gets the registered validators as a list.
	 * 
	 * @return the validators as a list
	 */
	public final List getValidators()
	{
		final List list;
		if (this.validator == null)
		{
			list = Collections.EMPTY_LIST;
		}
		else if (this.validator instanceof ValidatorList)
		{
			list = ((ValidatorList)this.validator).asList();
		}
		else
		{
			list = new ArrayList(1);
			list.add(validator);
		}
		return list;
	}

	/**
	 * Gets current value for a form component.
	 * 
	 * @return The value
	 */
	public String getValue()
	{
		return getModelObjectAsString();
	}

	/**
	 * @return True if this component supports persistence AND it has been asked
	 *         to persist itself with setPersistent().
	 */
	public final boolean isPersistent()
	{
		return supportsPersistence() && persistent;
	}

	/**
	 * Gets whether this component is 'valid'. Valid in this context means that
	 * no validation errors were reported the last time the form component was
	 * processed. This variable not only is convenient for 'business' use, but
	 * is also nescesarry as we don't want the form component models updated
	 * with invalid input.
	 * 
	 * @return valid whether this component is 'valid'
	 */
	public final boolean isValid()
	{
		return !hasErrorMessage();
	}

	/**
	 * Gets whether this component is to be validated.
	 * 
	 * @return True if this component has one or more validators
	 */
	public final boolean isValidated()
	{
		return this.validator != IValidator.NULL;
	}

	/**
	 * Sets whether this component is to be persisted.
	 * 
	 * @param persistent
	 *            True if this component is to be persisted.
	 */
	public final void setPersistent(final boolean persistent)
	{
		if (supportsPersistence())
		{
			this.persistent = persistent;
		}
		else
		{
			throw new UnsupportedOperationException("FormComponent " + getClass()
					+ " does not support cookies");
		}
	}

	/**
	 * Sets the value for a form component.
	 * 
	 * @param value
	 *            The value
	 */
	public void setValue(final String value)
	{
		setModelObject(value);
	}

	/**
	 * @return True if this type of FormComponent can be persisted.
	 */
	public boolean supportsPersistence()
	{
		return false;
	}

	/**
	 * Implemented by form component subclass to update the form component's
	 * model.
	 */
	public abstract void updateModel();

	/**
	 * Validates this component using the component's validator.
	 */
	public final void validate()
	{
		validator.validate(this);
	}

	/**
	 * Processes the component tag.
	 * 
	 * @param tag
	 *            Tag to modify
	 * @see wicket.Component#handleComponentTag(ComponentTag)
	 */
	protected void handleComponentTag(final ComponentTag tag)
	{
		super.handleComponentTag(tag);
		tag.put("name", getPath());
	}

	/**
	 * Template method that can be implemented by form component subclass to
	 * react when validation errors are cleared. This implementation is a noop.
	 */
	protected void handleValid()
	{
	}

	/**
	 * Template method that can be implemented by form component subclass to
	 * react on validation errors. This implementation is a noop.
	 */
	protected void handleInvalid()
	{
	}
}