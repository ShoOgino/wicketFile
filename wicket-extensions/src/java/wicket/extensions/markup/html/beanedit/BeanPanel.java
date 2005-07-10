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
package wicket.extensions.markup.html.beanedit;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import wicket.AttributeModifier;
import wicket.WicketRuntimeException;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.TextField;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;

/**
 * Panel for generic bean displaying/ editing.
 *
 * @author Eelco Hillenius
 */
public class BeanPanel extends Panel
{
	/** edit mode. */
	private EditMode editMode = EditMode.READ_WRITE;

	/**
	 * Construct.
	 * @param id component id
	 * @param bean JavaBean to be edited or displayed
	 */
	public BeanPanel(String id, Serializable bean)
	{
		this(id, new BeanModel(bean));
	}

	/**
	 * Construct.
	 * @param id component id
	 * @param beanModel model with the JavaBean to be edited or displayed
	 */
	public BeanPanel(String id, BeanModel beanModel)
	{
		super(id, beanModel);
		add(new Label("displayName", new BeanDisplayNameModel(beanModel)));
		add(new PropertyList("propertiesList", new BeanPropertiesListModel(beanModel)));
	}

	/**
	 * Gets the editor for the given property.
	 * @param panelId id of panel; must be used for constructing any panel
	 * @param descriptor property descriptor
	 * @return the editor
	 */
	protected Panel getPropertyEditor(String panelId, PropertyDescriptor descriptor)
	{
		Class type = descriptor.getPropertyType();
		BeanPropertyEditor editor = findCustomEditor(panelId, descriptor);

		if (editor == null)
		{
			if (descriptor instanceof IndexedPropertyDescriptor)
			{
				throw new WicketRuntimeException("index properties not supported yet ");
			}
			else
			{
				editor = getDefaultEditor(panelId, descriptor);
			}
		}

		return editor;
	}

	

	/**
	 * Gets the editMode.
	 * @return editMode
	 */
	protected EditMode getEditMode()
	{
		return editMode;
	}

	/**
	 * Sets the editMode.
	 * @param editMode editMode
	 */
	protected void setEditMode(EditMode editMode)
	{
		this.editMode = editMode;
	}

	/**
	 * Gets a default property editor panel.
	 * @param panelId component id
	 * @param descriptor property descriptor
	 * @return a property editor
	 */
	protected final BeanPropertyEditor getDefaultEditor(
			String panelId, PropertyDescriptor descriptor)
	{
		BeanPropertyEditor editor;
		Class type = descriptor.getPropertyType();
		if(Boolean.class.isAssignableFrom(type) || Boolean.TYPE == type)
		{
			editor = new PropertyCheckBox(panelId, (BeanModel)getModel(), descriptor, getEditMode());
		}
		else
		{
			editor = new PropertyInput(panelId, (BeanModel)getModel(), descriptor, getEditMode());
		}
		return editor;
	}

	/**
	 * Gets the model casted to {@link BeanModel}.
	 * @return the model casted to {@link BeanModel}
	 */
	protected final BeanModel getBeanModel()
	{
		return (BeanModel)getModel();
	}

	/**
	 * Finds a possible custom editor by looking for the type name + 'Editor'
	 * (e.g. mypackage.MyBean has editor mypackage.MyBeanEditor).
	 * @param panelId id of panel; must be used for constructing any panel
	 * @param descriptor property descriptor
	 * @return PropertyEditor if found or null
	 */
	protected final BeanPropertyEditor findCustomEditor(String panelId, PropertyDescriptor descriptor)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = getClass().getClassLoader();
		}

		Class type = descriptor.getPropertyType();
		String editorTypeName = type.getName() + "Editor";
		try
		{
			Class editorClass = classLoader.loadClass(editorTypeName);
			try
			{
				// get constructor
				Constructor constructor = editorClass.getConstructor(
						new Class[]{String.class, BeanModel.class, PropertyDescriptor.class, EditMode.class});

				// construct arguments
				Object[] args = new Object[]{panelId, BeanPanel.this.getModel(), descriptor, getEditMode()};

				// create editor instance
				BeanPropertyEditor editor = (BeanPropertyEditor)constructor.newInstance(args);
				return editor;
			}
			catch (SecurityException e)
			{
				throw new WicketRuntimeException(e);
			}
			catch (NoSuchMethodException e)
			{
				throw new WicketRuntimeException(e);
			}
			catch (InstantiationException e)
			{
				throw new WicketRuntimeException(e);
			}
			catch (IllegalAccessException e)
			{
				throw new WicketRuntimeException(e);
			}
			catch (InvocationTargetException e)
			{
				throw new WicketRuntimeException(e);
			}
		}
		catch(ClassNotFoundException e)
		{
			// ignore; there just is no custom editor
		}

		return null;
	}

	/**
	 * Lists all properties of the target object.
	 */
	private final class PropertyList extends ListView
	{
		/**
		 * Construct.
		 * @param id component name
		 * @param model the model
		 */
		public PropertyList(String id, BeanPropertiesListModel model)
		{
			super(id, model);
			setOptimizeItemRemoval(true);
		}

		/**
		 * @see wicket.markup.html.list.ListView#populateItem(wicket.markup.html.list.ListItem)
		 */
		protected void populateItem(ListItem item)
		{
			PropertyDescriptor descriptor = (PropertyDescriptor)item.getModelObject();
			item.add(new Label("displayName", descriptor.getDisplayName()));
			Panel propertyEditor = getPropertyEditor("editor", descriptor);
			item.add(propertyEditor);
		}
	}

	/**
	 * Panel for an input field.
	 */
	private static final class PropertyInput extends BeanPropertyEditor
	{
		/**
		 * Construct.
		 * @param id component id
		 * @param beanModel model with the target bean
		 * @param descriptor property descriptor
		 * @param editMode the edit mode
		 */
		public PropertyInput(String id, final BeanModel beanModel,
				final PropertyDescriptor descriptor, final EditMode editMode)
		{
			super(id, beanModel, descriptor, editMode);
			Class type = descriptor.getPropertyType();
			TextField valueTextField = new TextField("value",
					new BeanPropertyModel(beanModel, descriptor), type);
			EditModeReplacementModel replacementModel =
				new EditModeReplacementModel(editMode, descriptor);
			valueTextField.add(new AttributeModifier("disabled", true, replacementModel));
			add(valueTextField);
		}
	}

	/**
	 * Panel for a check box.
	 */
	public static final class PropertyCheckBox extends BeanPropertyEditor
	{
		/**
		 * Construct.
		 * @param id component id
		 * @param beanModel model with the target bean
		 * @param descriptor property descriptor
		 * @param editMode edit mode
		 */
		public PropertyCheckBox(String id, BeanModel beanModel,
				final PropertyDescriptor descriptor, EditMode editMode)
		{
			super(id, beanModel, descriptor, editMode);
			Class type = descriptor.getPropertyType();
			CheckBox valueTextField = new CheckBox("value",
					new BeanPropertyModel(beanModel, descriptor));
			EditModeReplacementModel replacementModel =
				new EditModeReplacementModel(editMode, descriptor);
			valueTextField.add(new AttributeModifier("disabled", true, replacementModel));
			add(valueTextField);
		}
	}
}
