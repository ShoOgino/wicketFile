/*
 * $Id: AjaxSubmitButton.java 5125 2006-03-25 11:42:10 -0800 (Sat, 25 Mar 2006)
 * ivaynberg $ $Revision$ $Date: 2006-03-25 11:42:10 -0800 (Sat, 25 Mar
 * 2006) $
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
package wicket.ajax.markup.html.form;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.IAjaxCallDecorator;
import wicket.ajax.form.AjaxFormSubmitBehavior;
import wicket.markup.ComponentTag;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.util.string.AppendingStringBuffer;

/**
 * A button that submits the form via ajax. Since this button takes the form as
 * a constructor argument it does not need to be added to it unlike the
 * {@link Button} component.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public abstract class AjaxSubmitButton extends Button
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param form
	 */
	public AjaxSubmitButton(String id, final Form form)
	{
		super(id);

		add(new AjaxFormSubmitBehavior(form, "onclick")
		{

			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target)
			{
				AjaxSubmitButton.this.onSubmit(target, form);
			}
			
			protected void onError(AjaxRequestTarget target)
			{
				AjaxSubmitButton.this.onError(target, form);
			}

			protected CharSequence getEventHandler()
			{
				return new AppendingStringBuffer(super.getEventHandler()).append("; return false;");
			}

			protected IAjaxCallDecorator getAjaxCallDecorator()
			{
				return AjaxSubmitButton.this.getAjaxCallDecorator();
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
		checkComponentTag(tag, "input");

		final String type = tag.getAttributes().getString("type");
		if (!"button".equals(type) && !"image".equals(type) && !"submit".equals(type))
		{
			findMarkupStream().throwMarkupException(
					"Component " + getId() + " must be applied to a tag with 'type'"
							+ " attribute matching 'submit', 'button' or 'image', not '" + type
							+ "'");
		}

		super.onComponentTag(tag);
	}

	/**
	 * Listener method invoked on form submit with no errors
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
	protected void onError(AjaxRequestTarget target, Form form) {
		
	}

}
