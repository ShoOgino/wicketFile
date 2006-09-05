/*
 * $Id$ $Revision$ $Date$
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
package wicket.extensions.ajax.markup.html.autocomplete;

import wicket.RequestCycle;
import wicket.Response;
import wicket.ajax.AbstractDefaultAjaxBehavior;
import wicket.ajax.AjaxRequestTarget;
import wicket.markup.html.PackageResourceReference;
import wicket.markup.html.resources.CompressedPackageResourceReference;
import wicket.util.string.JavascriptUtils;

/**
 * @since 1.2
 * 
 * @author Janne Hietam&auml;ki (jannehietamaki)
 */
public abstract class AbstractAutoCompleteBehavior extends AbstractDefaultAjaxBehavior
{
	private static final PackageResourceReference AUTOCOMPLETE_JS = new CompressedPackageResourceReference(
			AutoCompleteBehavior.class, "wicket-autocomplete.js");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void onRenderHeadContribution(Response response)
	{
		writeJsReference(response, AUTOCOMPLETE_JS);
	}

	protected void onComponentRendered()
	{
		Response response = getComponent().getResponse();
		final String id = getComponent().getMarkupId();
		response.write(JavascriptUtils.SCRIPT_OPEN_TAG);
		response.write("new Wicket.Ajax.AutoComplete('" + id + "','" + getCallbackUrl() + "');");
		response.write(JavascriptUtils.SCRIPT_CLOSE_TAG);
	}

	protected void respond(AjaxRequestTarget target)
	{
		final RequestCycle requestCycle = RequestCycle.get();
		final String val = requestCycle.getRequest().getParameter("q");
		onRequest(val, requestCycle);
	}

	/**
	 * Callback for the ajax event generated by the javascript. This is where we
	 * need to generate our response.
	 * 
	 * @param input
	 *            the input entered so far
	 * @param requestCycle
	 *            current request cycle
	 */
	protected abstract void onRequest(String input, RequestCycle requestCycle);
}
