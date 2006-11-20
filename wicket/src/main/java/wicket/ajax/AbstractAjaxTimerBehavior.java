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
package wicket.ajax;

import wicket.RequestCycle;
import wicket.markup.html.IHeaderResponse;
import wicket.markup.html.WebPage;
import wicket.util.string.JavascriptUtils;
import wicket.util.time.Duration;

/**
 * A behavior that generates an AJAX update callback at a regular interval.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class AbstractAjaxTimerBehavior extends AbstractDefaultAjaxBehavior
{
	/** The update interval */
	private final Duration updateInterval;

	private boolean attachedBodyOnLoadModifier = false;

	/**
	 * Construct.
	 * 
	 * @param updateInterval
	 *            Duration between AJAX callbacks
	 */
	public AbstractAjaxTimerBehavior(final Duration updateInterval)
	{
		this.updateInterval = updateInterval;
	}

	/**
	 * Subclasses should call super.onBind()
	 * 
	 * @see wicket.ajax.AbstractDefaultAjaxBehavior#onBind()
	 */
	@Override
	protected void onBind()
	{
		super.onBind();
	}
	
	/**
	 * @see wicket.behavior.AbstractAjaxBehavior#renderHead(wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response)
	{
		super.renderHead(response);
	}
	
	/**
	 * @param updateInterval
	 *            Duration between AJAX callbacks
	 * @return JS script
	 */
	protected final String getJsTimeoutCall(final Duration updateInterval)
	{
		return "setTimeout(function() { " + getCallbackScript(false) + " }, "
				+ updateInterval.getMilliseconds() + ");";
	}

	/**
	 * 
	 * @see wicket.ajax.AbstractDefaultAjaxBehavior#respond(wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	protected final void respond(final AjaxRequestTarget target)
	{
		onTimer(target);
	}

	/**
	 * Listener method for the AJAX timer event.
	 * 
	 * @param target
	 *            The request target
	 */
	protected abstract void onTimer(final AjaxRequestTarget target);
	
	/**
	 * Inject the timer script into the markup after the component is rendered 
	 * @see wicket.behavior.AbstractAjaxBehavior#onComponentRendered()
	 */
	@Override
	protected void onComponentRendered()
	{
		JavascriptUtils.writeJavascript(RequestCycle.get().getResponse(), getJsTimeoutCall(updateInterval));
	}
}
