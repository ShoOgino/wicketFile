/*
 * $Id: AbstractDefaultAjaxBehavior.java 4858 2006-03-12 00:26:31 -0800 (Sun, 12
 * Mar 2006) ivaynberg $ $Revision$ $Date: 2006-03-12 00:26:31 -0800
 * (Sun, 12 Mar 2006) $
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
package wicket.ajax;

import wicket.Application;
import wicket.RequestCycle;
import wicket.Response;
import wicket.behavior.AbstractAjaxBehavior;
import wicket.markup.html.PackageResourceReference;
import wicket.settings.IAjaxSettings;
import wicket.util.string.AppendingStringBuffer;
import wicket.util.string.Strings;

/**
 * The base class for Wicket's default AJAX implementation.
 * 
 * @author Igor Vaynberg
 */
public abstract class AbstractDefaultAjaxBehavior extends AbstractAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	/** reference to the default indicator gif file. */
	public static final PackageResourceReference INDICATOR = new PackageResourceReference(
			AbstractDefaultAjaxBehavior.class, "indicator.gif");


	/** reference to the default ajax support javascript file. */
	private static final PackageResourceReference JAVASCRIPT = new PackageResourceReference(
			AbstractDefaultAjaxBehavior.class, "wicket-ajax.js");

	/** reference to the default ajax debug support javascript file. */
	private static final PackageResourceReference JAVASCRIPT_DEBUG_DRAG = new PackageResourceReference(
			AbstractDefaultAjaxBehavior.class, "wicket-ajax-debug-drag.js");

	/** reference to the default ajax debug support javascript file. */
	private static final PackageResourceReference JAVASCRIPT_DEBUG = new PackageResourceReference(
			AbstractDefaultAjaxBehavior.class, "wicket-ajax-debug.js");


	/**
	 * 
	 * @see wicket.behavior.AbstractAjaxBehavior#getImplementationId()
	 */
	protected final String getImplementationId()
	{
		return "wicket-default";
	}
	
	/**
	 * Subclasses should call super.onBind()
	 * @see wicket.behavior.AbstractAjaxBehavior#onBind()
	 */
	protected void onBind()
	{
		getComponent().setOutputMarkupId(true);
	}

	/**
	 * 
	 * @see wicket.behavior.AbstractAjaxBehavior#onRenderHeadInitContribution(wicket.Response)
	 */
	protected void onRenderHeadInitContribution(final Response response)
	{
		final IAjaxSettings settings = Application.get().getAjaxSettings();

		writeJsReference(response, JAVASCRIPT);

		if (settings.isAjaxDebugModeEnabled())
		{
			response.write("<script>wicketAjaxDebugEnable=true;</script>");
			writeJsReference(response, JAVASCRIPT_DEBUG_DRAG);
			writeJsReference(response, JAVASCRIPT_DEBUG);
		}


	}

	/**
	 * @return ajax call decorator used to decorate the call generated by this
	 *         behavior
	 */
	protected IAjaxCallDecorator getAjaxCallDecorator()
	{
		return null;
	}

	/**
	 * @return javascript that will generate an ajax GET request to this
	 *         behavior
	 */
	protected String getCallbackScript()
	{
		return getCallbackScript("wicketAjaxGet('" + getCallbackUrl() + "'");
	}


	/**
	 * Returns javascript that performs an ajax callback to this behavior. The
	 * script is decorated by the ajax callback decorator from
	 * {@link AbstractDefaultAjaxBehavior#getAjaxCallDecorator()}.
	 * 
	 * @param partialCall
	 *            Javascript of a partial call to the function performing the
	 *            actual ajax callback. Must be in format
	 *            <code>function(params,</code> with signature
	 *            <code>function(params, onSuccessHandler, onFailureHandler</code>.
	 *            Example: <code>wicketAjaxGet('callbackurl'</code>
	 * 
	 * @return script that peforms ajax callback to this behavior
	 */
	protected String getCallbackScript(String partialCall)
	{
		final IAjaxCallDecorator callDecorator = getAjaxCallDecorator();
		final String before = (callDecorator == null) ? null : callDecorator.getBeforeScript();
		final String after = (callDecorator == null) ? null : callDecorator.getAfterScript();
		final String success = (callDecorator == null) ? null : callDecorator.getOnSuccessScript();
		final String failure = (callDecorator == null) ? null : callDecorator.getOnFailureScript();

		String indicatorId = findIndicatorId();

		AppendingStringBuffer buff = new AppendingStringBuffer(128);

		if (!Strings.isEmpty(indicatorId))
		{
			buff.append("wicketShow('").append(indicatorId).append("');");
		}

		if (!Strings.isEmpty(before))
		{
			buff.append(before);
			if (!before.endsWith(";"))
			{
				buff.append("; ");
			}
		}

		buff.append("var ").append(IAjaxCallDecorator.WICKET_CALL_MADE_VAR).append("=");

		buff.append(partialCall);

		if (!Strings.isEmpty(success)||!Strings.isEmpty(indicatorId))
		{
			buff.append(", function() { ");
			if (!Strings.isEmpty(indicatorId))
			{
				buff.append("wicketHide('").append(indicatorId).append("');");
			}
			if (!Strings.isEmpty(success)) {
				buff.append(success);
				if (!success.endsWith(";")) {
					buff.append(";");
				}
			}
			buff.append("}");
		}

		buff.append(");");

		if (!Strings.isEmpty(after))
		{
			buff.append(after);
			if (!after.endsWith(";"))
			{
				buff.append(";");
			}
		}

		return buff.toString();
	}


	private String findIndicatorId()
	{
		if (getComponent() instanceof IAjaxIndicatorAware)
		{
			return ((IAjaxIndicatorAware)getComponent()).getAjaxIndicatorMarkupId();
		}
		
		if (this instanceof IAjaxIndicatorAware) {
			return ((IAjaxIndicatorAware)this).getAjaxIndicatorMarkupId();
		}
		
		return null;
	}

	/**
	 * @see wicket.behavior.IBehaviorListener#onRequest()
	 */
	public final void onRequest()
	{
		boolean isPageVersioned = true;
		try
		{
			isPageVersioned = getComponent().getPage().isVersioned();
			getComponent().getPage().setVersioned(false);

			AjaxRequestTarget target = new AjaxRequestTarget();
			RequestCycle.get().setRequestTarget(target);
			respond(target);
		}
		finally
		{
			getComponent().getPage().setVersioned(isPageVersioned);
		}
	}

	/**
	 * @param target
	 *            The AJAX target
	 */
	protected abstract void respond(AjaxRequestTarget target);
}
