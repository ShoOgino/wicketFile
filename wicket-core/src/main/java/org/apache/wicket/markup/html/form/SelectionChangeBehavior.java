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
package org.apache.wicket.markup.html.form;

import org.apache.wicket.Component;
import org.apache.wicket.IRequestListener;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;

/**
 * A behavior to get change notifications when a choice component changes its selection.
 * <p>
 * Contrary to {@link AjaxFormChoiceComponentUpdatingBehavior} all notification are send via
 * standard HTTP requests and the full page is rendered as the response.
 * 
 * @see SelectionChangeBehavior#onSelectionChanged()
 */
public class SelectionChangeBehavior extends Behavior implements IRequestListener
{

	private FormComponent<?> formComponent;

	@Override
	public boolean getStatelessHint(Component component)
	{
		return false;
	}

	@Override
	public final void bind(final Component hostComponent)
	{
		Args.notNull(hostComponent, "hostComponent");

		if (formComponent != null)
		{
			throw new IllegalStateException("this kind of handler cannot be attached to " +
				"multiple components; it is already attached to component " + formComponent +
				", but component " + hostComponent + " wants to be attached too");
		}

		this.formComponent = (FormComponent<?>)hostComponent;

		formComponent.setRenderBodyOnly(false);

		// call the callback
		onBind();
	}

	protected void onBind()
	{
	}

	public FormComponent<?> getFormComponent()
	{
		return formComponent;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag)
	{
		CharSequence url = component.urlForListener(this, new PageParameters());

		String event = getJSEvent();

		String condition = String.format("if (event.target.name !== '%s') return; ",
			formComponent.getInputName());

		Form<?> form = component.findParent(Form.class);
		if (form != null)
		{
			tag.put(event, condition + form.getJsForListenerUrl(url.toString()));
		}
		else
		{
			char separator = url.toString().indexOf('?') > -1 ? '&' : '?';

			tag.put(event, condition + String.format("window.location.href='%s%s%s=' + %s;", url,
				separator, formComponent.getInputName(), getJSValue()));
		}
	}

	/**
	 * Which JavaScript event triggers notification. 
	 */
	private String getJSEvent()
	{
		if (formComponent instanceof DropDownChoice)
		{
			return "onchange";
		}
		else
		{
			return "onclick";
		}
	}

	/**
	 * How to get the current value via JavaScript. 
	 */
	private String getJSValue()
	{
		if (formComponent instanceof DropDownChoice)
		{
			return "this.options[this.selectedIndex].value";
		}
		else if (formComponent instanceof CheckBox)
		{
			return "this.checked";
		}
		else
		{
			return "event.target.value";
		}
	}

	/**
	 * Process the form component.
	 */
	private void process()
	{
		formComponent.validate();
		if (formComponent.isValid())
		{
			if (getUpdateModel())
			{
				formComponent.valid();
				formComponent.updateModel();
			}

			onSelectionChanged();
		}
		else
		{
			formComponent.invalid();
		}
		
		onSelectionChanged();
	}

	/**
	 * Gives the control to the application to decide whether the form component model should
	 * be updated automatically or not. Make sure to call {@link org.apache.wicket.markup.html.form.FormComponent#valid()}
	 * additionally in case the application want to update the model manually.
	 *
	 * @return true if the model of form component should be updated, false otherwise
	 */
	protected boolean getUpdateModel()
	{
		return true;
	}

	/**
	 * Hook method invoked when selection has changed.
	 */
	protected void onSelectionChanged()
	{
	}

	@Override
	public final void onRequest()
	{
		Form<?> form = formComponent.findParent(Form.class);
		if (form == null)
		{
			process();
		}
		else
		{
			form.getRootForm().onFormSubmitted(new IFormSubmitter()
			{
				@Override
				public void onSubmit()
				{
					process();
				}

				@Override
				public void onError()
				{
				}

				@Override
				public void onAfterSubmit()
				{
				}

				@Override
				public Form<?> getForm()
				{
					return formComponent.getForm();
				}

				@Override
				public boolean getDefaultFormProcessing()
				{
					return false;
				}
			});
		}
	}
}
