/*
 * $Id$
 * $Revision$ $Date$
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
import wicket.markup.html.link.Link;
import wicket.model.IModel;

/**
 * An ajax link that will degrade to a normal request if ajax is not available
 * or javascript is disabled
 */
public abstract class AjaxFallbackLink extends Link
{
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public AjaxFallbackLink(final String id)
	{
		this(id, null);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 */
	public AjaxFallbackLink(final String id, final IModel model)
	{
		super(id, model);

		add(new AjaxEventBehavior("onclick")
		{
			private static final long serialVersionUID = 1L;

			protected void onEvent(AjaxRequestTarget target)
			{
				onClick(target);
			}

			protected String getEventHandler()
			{
				return AjaxFallbackLink.this.getEventHandler("return !" + super.getEventHandler());
			}
		});
	}

	/**
	 * Returns the javascript event handler for this component. This function is
	 * used to decorate the generated javascript handler.
	 * <p>
	 * NOTE: It is recommended that you only prepend additional javascript to
	 * the default handler because the default handler uses the return func()
	 * format so any appended javascript will not be evaluated by default.
	 * 
	 * @param defaultHandler
	 *            default javascript event handler generated by this link
	 * @return javascript event handler for this link
	 */
	protected String getEventHandler(String defaultHandler)
	{
		return defaultHandler;
	}

	/**
	 * 
	 * @see wicket.markup.html.link.Link#onClick()
	 */
	public final void onClick()
	{
		onClick(null);
	}

	/**
	 * Callback for the onClick event. If ajax failed and this event was
	 * generated via a normal link the target argument will be null
	 * 
	 * @param target
	 *            ajax target if this linked was invoked using ajax, null
	 *            otherwise
	 */
	protected abstract void onClick(final AjaxRequestTarget target);
}
