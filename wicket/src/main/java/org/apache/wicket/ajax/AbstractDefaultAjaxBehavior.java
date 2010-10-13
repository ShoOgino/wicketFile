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
package org.apache.wicket.ajax;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WicketEventReference;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.resource.JavascriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.IDebugSettings;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;

/**
 * The base class for Wicket's default AJAX implementation.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class AbstractDefaultAjaxBehavior extends AbstractAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	/** reference to the default indicator gif file. */
	public static final ResourceReference INDICATOR = new PackageResourceReference(
		AbstractDefaultAjaxBehavior.class, "indicator.gif");

	/** reference to the default ajax debug support javascript file. */
	private static final ResourceReference JAVASCRIPT_DEBUG = new JavascriptResourceReference(
		AbstractDefaultAjaxBehavior.class, "wicket-ajax-debug.js");

	/**
	 * Subclasses should call super.onBind()
	 * 
	 * @see org.apache.wicket.behavior.AbstractAjaxBehavior#onBind()
	 */
	@Override
	protected void onBind()
	{
		getComponent().setOutputMarkupId(true);
	}

	/**
	 * @see org.apache.wicket.behavior.AbstractAjaxBehavior#renderHead(org.apache.wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response)
	{
		final IDebugSettings debugSettings = Application.get().getDebugSettings();

		response.renderJavascriptReference(WicketEventReference.INSTANCE);
		response.renderJavascriptReference(WicketAjaxReference.INSTANCE);

		if (debugSettings.isAjaxDebugModeEnabled())
		{
			response.renderJavascriptReference(JAVASCRIPT_DEBUG);
			response.renderJavascript("wicketAjaxDebugEnable=true;", "wicket-ajax-debug-enable");
		}

		// TODO NG Escape
		response.renderJavascript("Wicket.Ajax.baseUrl=\"" +
			RequestCycle.get().getUrlRenderer().getBaseUrl() + "\";", "wicket-ajax-base-url");
	}

	/**
	 * @return ajax call decorator used to decorate the call generated by this behavior or null for
	 *         none
	 */
	protected IAjaxCallDecorator getAjaxCallDecorator()
	{
		return null;
	}

	/**
	 * @return javascript that will generate an ajax GET request to this behavior
	 */
	protected CharSequence getCallbackScript()
	{
		return getCallbackScript(false);
	}

	/**
	 * @return javascript that will generate an ajax GET request to this behavior *
	 * @param onlyTargetActivePage
	 *            if true the callback to this behavior will be ignore if the page is not the last
	 *            one the user accessed
	 * 
	 */
	protected CharSequence getCallbackScript(boolean onlyTargetActivePage)
	{
		return generateCallbackScript("wicketAjaxGet('" + getCallbackUrl() + "'");
	}

	/**
	 * @return an optional javascript expression that determines whether the request will actually
	 *         execute (in form of return XXX;);
	 */
	protected CharSequence getPreconditionScript()
	{
		if (getComponent() instanceof Page)
		{
			return "return true;";
		}
		else
		{
			return "return Wicket.$('" + getComponent().getMarkupId() + "') != null;";
		}
	}

	/**
	 * @return javascript that will run when the ajax call finishes with an error status
	 */
	protected CharSequence getFailureScript()
	{
		return null;
	}

	/**
	 * @return javascript that will run when the ajax call finishes successfully
	 */
	protected CharSequence getSuccessScript()
	{
		return null;
	}

	/**
	 * Returns javascript that performs an ajax callback to this behavior. The script is decorated
	 * by the ajax callback decorator from
	 * {@link AbstractDefaultAjaxBehavior#getAjaxCallDecorator()}.
	 * 
	 * @param partialCall
	 *            Javascript of a partial call to the function performing the actual ajax callback.
	 *            Must be in format <code>function(params,</code> with signature
	 *            <code>function(params, onSuccessHandler, onFailureHandler</code>. Example:
	 *            <code>wicketAjaxGet('callbackurl'</code>
	 * 
	 * @return script that performs ajax callback to this behavior
	 */
	protected CharSequence generateCallbackScript(final CharSequence partialCall)
	{
		final CharSequence onSuccessScript = getSuccessScript();
		final CharSequence onFailureScript = getFailureScript();
		final CharSequence precondition = getPreconditionScript();

		final IAjaxCallDecorator decorator = getAjaxCallDecorator();

		String indicatorId = findIndicatorId();

		CharSequence success = (onSuccessScript == null) ? "" : onSuccessScript;
		CharSequence failure = (onFailureScript == null) ? "" : onFailureScript;

		if (decorator != null)
		{
			success = decorator.decorateOnSuccessScript(getComponent(), success);
		}

		if (!Strings.isEmpty(indicatorId))
		{
			String hide = ";Wicket.hideIncrementally('" + indicatorId + "');";
			success = success + hide;
			failure = failure + hide;
		}

		if (decorator != null)
		{
			failure = decorator.decorateOnFailureScript(getComponent(), failure);
		}

		AppendingStringBuffer buff = new AppendingStringBuffer(256);
		buff.append("var ").append(IAjaxCallDecorator.WICKET_CALL_RESULT_VAR).append("=");
		buff.append(partialCall);

		buff.append(",function() { ").append(success).append("}.bind(this)");
		buff.append(",function() { ").append(failure).append("}.bind(this)");

		if (precondition != null)
		{
			buff.append(", function() {");
			buff.append(precondition);
			buff.append("}.bind(this)");
		}

		String channel = getChannelName();
		if (channel != null)
		{
			if (precondition == null)
			{
				buff.append(", null");
			}
			buff.append(", '");
			buff.append(channel);
			buff.append("'");
		}

		buff.append(");");

		CharSequence call = buff;

		if (!Strings.isEmpty(indicatorId))
		{
			final AppendingStringBuffer indicatorWithPrecondition = new AppendingStringBuffer(
				"if (");
			if (precondition != null)
			{
				indicatorWithPrecondition.append("function(){").append(precondition).append("}()");
			}
			else
			{
				indicatorWithPrecondition.append("true");
			}
			indicatorWithPrecondition.append(") { Wicket.showIncrementally('")
				.append(indicatorId)
				.append("');}")
				.append(call);

			call = indicatorWithPrecondition;
		}

		if (decorator != null)
		{
			call = decorator.decorateScript(getComponent(), call);
		}

		return call;
	}

	protected String getChannelName()
	{
		return null;
	}

	/**
	 * Finds the markup id of the indicator. The default search order is: component, behavior,
	 * component's parent hieararchy.
	 * 
	 * @return markup id or <code>null</code> if no indicator found
	 */
	protected String findIndicatorId()
	{
		if (getComponent() instanceof IAjaxIndicatorAware)
		{
			return ((IAjaxIndicatorAware)getComponent()).getAjaxIndicatorMarkupId();
		}

		if (this instanceof IAjaxIndicatorAware)
		{
			return ((IAjaxIndicatorAware)this).getAjaxIndicatorMarkupId();
		}

		Component parent = getComponent().getParent();
		while (parent != null)
		{
			if (parent instanceof IAjaxIndicatorAware)
			{
				return ((IAjaxIndicatorAware)parent).getAjaxIndicatorMarkupId();
			}
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * @see org.apache.wicket.behavior.IBehaviorListener#onRequest()
	 */
	public final void onRequest()
	{
		WebApplication app = (WebApplication)getComponent().getApplication();
		AjaxRequestTarget target = app.newAjaxRequestTarget(getComponent().getPage());

		RequestCycle requestCycle = RequestCycle.get();
		requestCycle.scheduleRequestHandlerAfterCurrent(target);

		Url oldBaseURL = requestCycle.getUrlRenderer().getBaseUrl();
		WebRequest request = (WebRequest)requestCycle.getRequest();
		Url baseURL = Url.parse(request.getAjaxBaseUrl(), request.getCharset());
		requestCycle.getUrlRenderer().setBaseUrl(baseURL);

		respond(target);

		requestCycle.getUrlRenderer().setBaseUrl(oldBaseURL);
	}

	/**
	 * @param target
	 *            The AJAX target
	 */
	// TODO rename this to onEvent or something? respond is mostly the same as
	// onRender
	// this is not the case this is still the event handling period. respond is
	// called
	// in the RequestCycle on the AjaxRequestTarget..
	protected abstract void respond(AjaxRequestTarget target);

	/**
	 * Wraps the provided javascript with a throttled block. Throttled behaviors only execute once
	 * within the given delay even though they are triggered multiple times.
	 * <p>
	 * For example, this is useful when attaching an event behavior to the onkeypress event. It is
	 * not desirable to have an ajax call made every time the user types so we throttle that call to
	 * a desirable delay, such as once per second. This gives us a near real time ability to provide
	 * feedback without overloading the server with ajax calls.
	 * 
	 * @param script
	 *            javascript to be throttled
	 * @param throttleId
	 *            the id of the throttle to be used. Usually this should remain constant for the
	 *            same javascript block.
	 * @param throttleDelay
	 *            time span within which the javascript block will only execute once
	 * @return wrapped javascript
	 */
	public static CharSequence throttleScript(CharSequence script, String throttleId,
		Duration throttleDelay)
	{
		if (Strings.isEmpty(script))
		{
			throw new IllegalArgumentException("script cannot be empty");
		}

		if (Strings.isEmpty(throttleId))
		{
			throw new IllegalArgumentException("throttleId cannot be empty");
		}

		if (throttleDelay == null)
		{
			throw new IllegalArgumentException("throttleDelay cannot be null");
		}

		return new AppendingStringBuffer("wicketThrottler.throttle( '").append(throttleId)
			.append("', ")
			.append(throttleDelay.getMilliseconds())
			.append(", function() { ")
			.append(script)
			.append("}.bind(this));");
	}
}
