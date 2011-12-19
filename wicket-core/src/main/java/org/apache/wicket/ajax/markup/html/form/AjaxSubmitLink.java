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
package org.apache.wicket.ajax.markup.html.form;

import org.apache.wicket.ajax.AjaxRequestAttributes;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.AbstractSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * A link that submits a form via ajax. Since this link takes the form as a constructor argument it
 * does not need to be inside form's component hierarchy.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class AjaxSubmitLink extends AbstractSubmitLink
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public AjaxSubmitLink(String id)
	{
		this(id, null);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param form
	 */
	public AjaxSubmitLink(String id, final Form<?> form)
	{
		super(id, form);

		add(new AjaxFormSubmitBehavior(form, "click")
		{

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target)
			{
				AjaxSubmitLink.this.onSubmit(target, getForm());
			}

			@Override
			protected void onError(AjaxRequestTarget target)
			{
				AjaxSubmitLink.this.onError(target, getForm());
			}

			@Override
			protected CharSequence getEventHandler()
			{
				return new AppendingStringBuffer(super.getEventHandler()).append("; return false;");
			}

			@Override
			protected IAjaxCallDecorator getAjaxCallDecorator()
			{
				return AjaxSubmitLink.this.getAjaxCallDecorator();
			}

			@Override
			protected Form<?> findForm()
			{
				return AjaxSubmitLink.this.getForm();
			}

			@Override
			protected void onComponentTag(ComponentTag tag)
			{
				// write the onclick handler only if link is enabled
				if (isLinkEnabled())
				{
					super.onComponentTag(tag);
				}
			}

			@Override
			public boolean getDefaultProcessing()
			{
				return AjaxSubmitLink.this.getDefaultFormProcessing();
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes)
			{
				super.updateAjaxAttributes(attributes);
				AjaxSubmitLink.this.updateAjaxAttributes(attributes);
			}
		});

	}

	protected void updateAjaxAttributes(AjaxRequestAttributes attributes)
	{
	}

	/**
	 * Returns the {@link IAjaxCallDecorator} that will be used to modify the generated javascript.
	 * This is the preferred way of changing the javascript in the onclick handler
	 * 
	 * @return call decorator used to modify the generated javascript or null for none
	 */
	@Deprecated
	protected IAjaxCallDecorator getAjaxCallDecorator()
	{
		return null;
	}

	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);

		if (isLinkEnabled())
		{
			if (tag.getName().toLowerCase().equals("a"))
			{
				tag.put("href", "#");
			}
		}
		else
		{
			disableLink(tag);
		}
	}

	/**
	 * Final implementation of the Button's onSubmit. AjaxSubmitLinks have there own onSubmit which
	 * is called.
	 * 
	 * @see org.apache.wicket.markup.html.form.Button#onSubmit()
	 */
	@Override
	public final void onSubmit()
	{
	}

	/**
	 * Final implementation of the Button's onError. AjaxSubmitLinks have their own onError which is
	 * called.
	 * 
	 * @see org.apache.wicket.markup.html.form.Button#onError()
	 */
	@Override
	public final void onError()
	{

	}

	/**
	 * Listener method invoked on form submit
	 * 
	 * @param target
	 * @param form
	 */
	protected abstract void onSubmit(AjaxRequestTarget target, Form<?> form);

	/**
	 * Listener method invoked on form submit with errors
	 * 
	 * @param target
	 * @param form
	 */
	protected abstract void onError(AjaxRequestTarget target, Form<?> form);

}
