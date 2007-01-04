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
package wicket.ajax.markup.html.form;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.IAjaxCallDecorator;
import wicket.ajax.form.AjaxFormSubmitBehavior;
import wicket.markup.ComponentTag;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.form.Form;
import wicket.util.string.AppendingStringBuffer;

/**
 * A link that submits a form via ajax. Since this link takes the form as a
 * constructor argument it does not need to be inside form's component
 * hierarchy.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public abstract class AjaxSubmitLink extends WebMarkupContainer
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
	public AjaxSubmitLink(String id, final Form form)
	{
		super(id);

		form.setOutputMarkupId(true);

		add(new AjaxFormSubmitBehavior(form, "onclick")
		{

			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target)
			{
				AjaxSubmitLink.this.onSubmit(target, form);
			}

			protected void onError(AjaxRequestTarget target)
			{
				AjaxSubmitLink.this.onError(target, form);
			}

			protected CharSequence getEventHandler()
			{
				return new AppendingStringBuffer(super.getEventHandler()).append("; return false;");
			}

			protected IAjaxCallDecorator getAjaxCallDecorator()
			{
				return AjaxSubmitLink.this.getAjaxCallDecorator();
			}
		});

	}

	/**
	 * Returns the {@link IAjaxCallDecorator} that will be used to modify the
	 * generated javascript. This is the preferred way of changing the
	 * javascript in the onclick handler
	 * 
	 * @return call decorator used to modify the generated javascript or null
	 *         for none
	 */
	protected IAjaxCallDecorator getAjaxCallDecorator()
	{
		return null;
	}

	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);
		checkComponentTag(tag, "a");
		tag.put("href", "#");
	}

	/**
	 * Listener method invoked on form submit
	 * 
	 * @param target
	 * @param form
	 */
	protected abstract void onSubmit(AjaxRequestTarget target, Form form);

	/**
	 * Listener method invoked on form submit with errors
	 * 
	 * @param target
	 * @param form
	 * 
	 * TODO 1.3: Make abstract to be consistent with onsubmit()
	 */
	protected void onError(AjaxRequestTarget target, Form form)
	{

	}

}
