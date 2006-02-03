/*
 * $Id$ $Revision$
 * $Date$
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
package wicket.ajax.markup.html;

import wicket.ajax.AjaxEventBehavior;
import wicket.ajax.AjaxRequestTarget;
import wicket.markup.html.WebMarkupContainer;
import wicket.model.IModel;

/**
 * A component that allows a trigger request to be triggered via html anchor tag
 */
public abstract class AjaxLink extends WebMarkupContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public AjaxLink(final String id)
	{
		this(id, null);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 */
	public AjaxLink(final String id, final IModel model)
	{
		super(id, model);

		add(new AjaxEventBehavior("href")
		{
			private static final long serialVersionUID = 1L;

			protected void onEvent(AjaxRequestTarget target)
			{
				onClick(target);
			}
		});
	}

	/**
	 * Listener method invoked on the ajax request generated when the user
	 * clicks the link
	 * 
	 * @param target
	 */
	protected abstract void onClick(final AjaxRequestTarget target);
}
