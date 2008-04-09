/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.extensions.ajax.markup.html;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * An inplace editor much like {@link AjaxEditableLabel}, but instead of a {@link TextField} a
 * {@link DropDownChoice} is displayed.
 * 
 * @author Eelco Hillenius
 */
public class AjaxEditableChoiceLabel extends AjaxEditableLabel
{
	private static final long serialVersionUID = 1L;

	/** The list of objects. */
	private IModel choices;

	/** The renderer used to generate display/id values for the objects. */
	private IChoiceRenderer renderer;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 */
	public AjaxEditableChoiceLabel(String id)
	{
		super(id);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 */
	public AjaxEditableChoiceLabel(String id, IModel model)
	{
		super(id, model);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param choices
	 *            The collection of choices in the dropdown
	 */
	public AjaxEditableChoiceLabel(String id, List choices)
	{
		this(id, null, choices);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param choices
	 *            The collection of choices in the dropdown
	 */
	public AjaxEditableChoiceLabel(String id, IModel model, IModel choices)
	{
		super(id, model);
		this.choices = choices;
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param choices
	 *            The collection of choices in the dropdown
	 * @param renderer
	 *            The rendering engine
	 */
	public AjaxEditableChoiceLabel(String id, IModel model, IModel choices, IChoiceRenderer renderer)
	{
		super(id, model);
		this.choices = choices;
		this.renderer = renderer;
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param choices
	 *            The collection of choices in the dropdown
	 */
	public AjaxEditableChoiceLabel(String id, IModel model, List choices)
	{
		this(id, model, new Model((Serializable)choices));
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param choices
	 *            The collection of choices in the dropdown
	 * @param renderer
	 *            The rendering engine
	 */
	public AjaxEditableChoiceLabel(String id, IModel model, List choices, IChoiceRenderer renderer)
	{
		this(id, model, new Model((Serializable)choices), renderer);
	}


	/**
	 * @see org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel#newEditor(org.apache.wicket.MarkupContainer,
	 *      java.lang.String, org.apache.wicket.model.IModel)
	 */
	@Override
	protected FormComponent newEditor(MarkupContainer parent, String componentId, IModel model)
	{
		IModel choiceModel = new AbstractReadOnlyModel()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public Object getObject()
			{
				return choices.getObject();
			}

		};
		DropDownChoice editor = new DropDownChoice(componentId, model, choiceModel, renderer)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onModelChanged()
			{
				AjaxEditableChoiceLabel.this.onModelChanged();
			}

			@Override
			protected void onModelChanging()
			{
				AjaxEditableChoiceLabel.this.onModelChanging();
			}

		};
		editor.setOutputMarkupId(true);
		editor.setVisible(false);
		editor.add(new EditorAjaxBehavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag)
			{
				super.onComponentTag(tag);
				final String saveCall = "{wicketAjaxGet('" + getCallbackUrl() +
					"&save=true&'+this.name+'='+wicketEncode(this.value)); return true;}";

				final String cancelCall = "{wicketAjaxGet('" + getCallbackUrl() +
					"&save=false'); return false;}";

				tag.put("onchange", saveCall);
			}
		});
		return editor;
	}

	@Override
	protected void onModelChanged()
	{
		super.onModelChanged();
	}

	@Override
	protected void onModelChanging()
	{
		super.onModelChanging();
	}
}
