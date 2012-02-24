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

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.attributes.JavaScriptPrecondition;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IComponentAwareHeaderContributor;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.CoreLibrariesContributor;
import org.apache.wicket.util.string.Strings;

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
	 * @see org.apache.wicket.behavior.AbstractAjaxBehavior#renderHead(Component,
	 *      org.apache.wicket.markup.head.IHeaderResponse)
	 */
	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		CoreLibrariesContributor.contributeAjax(component.getApplication(), response);

		RequestCycle requestCycle = component.getRequestCycle();
		Url baseUrl = requestCycle.getUrlRenderer().getBaseUrl();
		CharSequence ajaxBaseUrl = Strings.escapeMarkup(baseUrl.toString());
		response.render(JavaScriptHeaderItem.forScript("Wicket.Ajax.baseUrl=\"" + ajaxBaseUrl +
			"\";", "wicket-ajax-base-url"));

		renderExtraHeaderContributors(component, response);
	}

	/**
	 * Renders header contribution by JavaScriptFunctionBody instances which additionally implement
	 * IComponentAwareHeaderContributor interface.
	 * 
	 * @param component
	 *            the component assigned to this behavior
	 * @param response
	 *            the current header response
	 */
	private void renderExtraHeaderContributors(Component component, IHeaderResponse response)
	{
		AjaxRequestAttributes attributes = getAttributes();

		List<IAjaxCallListener> ajaxCallListeners = attributes.getAjaxCallListeners();
		for (IAjaxCallListener ajaxCallListener : ajaxCallListeners)
		{
			if (ajaxCallListener instanceof IComponentAwareHeaderContributor)
			{
				IComponentAwareHeaderContributor contributor = (IComponentAwareHeaderContributor)ajaxCallListener;
				contributor.renderHead(component, response);
			}
		}

		List<JavaScriptPrecondition> preconditions = attributes.getPreconditions();
		for (JavaScriptPrecondition precondition : preconditions)
		{
			precondition.renderHead(component, response);
		}
	}

	/**
	 * @return the Ajax settings for this behavior
	 * @since 6.0
	 */
	protected final AjaxRequestAttributes getAttributes()
	{
		AjaxRequestAttributes attributes = new AjaxRequestAttributes();
		updateAjaxAttributesBackwardCompatibility(attributes);
		updateAjaxAttributes(attributes);
		return attributes;
	}

	/**
	 * Gives a chance to the specializations to modify the attributes.
	 * 
	 * @param attributes
	 * @since 6.0
	 */
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes)
	{
	}

	/**
	 * The code below handles backward compatibility.
	 * 
	 * @param attributes
	 */
	private void updateAjaxAttributesBackwardCompatibility(AjaxRequestAttributes attributes)
	{
		CharSequence preconditionScript = getPreconditionScript();
		if (Strings.isEmpty(preconditionScript) == false)
		{
			JavaScriptPrecondition precondition = new JavaScriptPrecondition(preconditionScript);
			attributes.getPreconditions().add(precondition);
		}

		AjaxCallListener backwardCompatibleAjaxCallListener = new AjaxCallListener()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public CharSequence getSuccessHandler(Component component)
			{
				return AbstractDefaultAjaxBehavior.this.getSuccessScript();
			}

			@Override
			public CharSequence getFailureHandler(Component component)
			{
				return AbstractDefaultAjaxBehavior.this.getFailureScript();
			}
		};
		attributes.getAjaxCallListeners().add(backwardCompatibleAjaxCallListener);

		AjaxChannel channel = getChannel();
		if (channel != null)
		{
			attributes.setChannel(channel);
		}
	}

	/**
	 * <pre>
	 * 				{
	 * 					u: 'editable-label?6-1.IBehaviorListener.0-text1-label',  // url
	 * 					m: 'POST',		// method name. Default: 'GET'
	 * 					c: 'label7',	// component id (String) or window for page
	 * 					e: 'click',		// event name
	 * 					sh: [],			// list of success handlers
	 * 					fh: [],			// list of failure handlers
	 * 					pre: [],		// list of preconditions. If empty set default : Wicket.$(settings{c}) !== null
	 * 					ep: {},			// extra parameters
	 * 					async: true|false,	// asynchronous XHR or not
	 * 					ch: 'someName|d',	// AjaxChannel
	 * 					i: 'indicatorId',	// indicator component id
	 * 					ad: true,			// allow default
	 * 					
	 * 				}
	 * </pre>
	 * 
	 * @param component
	 *            the component with that behavior
	 * @return the attributes as string in JSON format
	 */
	protected final CharSequence renderAjaxAttributes(final Component component)
	{
		AjaxRequestAttributes attributes = getAttributes();
		return renderAjaxAttributes(component, attributes);
	}

	/**
	 * 
	 * @param component
	 * @param attributes
	 * @return the attributes as string in JSON format
	 */
	protected final CharSequence renderAjaxAttributes(final Component component,
		AjaxRequestAttributes attributes)
	{
		JSONObject attributesJson = new JSONObject();

		try
		{
			attributesJson.put("u", getCallbackUrl());
			Method method = attributes.getMethod();
			if (Method.POST == method)
			{
				attributesJson.put("m", method);
			}

			if (component instanceof Page == false)
			{
				String componentId = component.getMarkupId();
				attributesJson.put("c", componentId);
			}

			String formId = attributes.getFormId();
			if (Strings.isEmpty(formId) == false)
			{
				attributesJson.put("f", formId);
			}

			if (attributes.isMultipart())
			{
				attributesJson.put("mp", true);
			}

			String submittingComponentId = attributes.getSubmittingComponentName();
			if (Strings.isEmpty(submittingComponentId) == false)
			{
				attributesJson.put("sc", submittingComponentId);
			}

			String indicatorId = findIndicatorId();
			if (Strings.isEmpty(indicatorId) == false)
			{
				attributesJson.put("i", indicatorId);
			}

			for (IAjaxCallListener ajaxCallListener : attributes.getAjaxCallListeners())
			{
				if (ajaxCallListener != null)
				{
					CharSequence beforeHandler = ajaxCallListener.getBeforeHandler(component);
					if (Strings.isEmpty(beforeHandler) == false)
					{
						attributesJson.append("bh", beforeHandler);
					}

					CharSequence afterHandler = ajaxCallListener.getAfterHandler(component);
					if (Strings.isEmpty(afterHandler) == false)
					{
						attributesJson.append("ah", afterHandler);
					}

					CharSequence successHandler = ajaxCallListener.getSuccessHandler(component);
					if (Strings.isEmpty(successHandler) == false)
					{
						attributesJson.append("sh", successHandler);
					}

					CharSequence failureHandler = ajaxCallListener.getFailureHandler(component);
					if (Strings.isEmpty(failureHandler) == false)
					{
						attributesJson.append("fh", failureHandler);
					}

					CharSequence completeHandler = ajaxCallListener.getCompleteHandler(component);
					if (Strings.isEmpty(completeHandler) == false)
					{
						attributesJson.append("coh", completeHandler);
					}
				}
			}

			for (JavaScriptPrecondition pre : attributes.getPreconditions())
			{
				String precondition = pre.toString();
				if (Strings.isEmpty(precondition) == false)
				{
					attributesJson.append("pre", precondition);
				}
			}

			JSONObject extraParameters = new JSONObject();
			Iterator<Entry<String, Object>> itor = attributes.getExtraParameters()
				.entrySet()
				.iterator();
			while (itor.hasNext())
			{
				Entry<String, Object> entry = itor.next();
				String name = entry.getKey();
				Object value = entry.getValue();
				extraParameters.accumulate(name, value);
			}
			if (extraParameters.length() > 0)
			{
				attributesJson.put("ep", extraParameters);
			}

			List<CharSequence> urlArgumentMethods = attributes.getDynamicExtraParameters();
			if (urlArgumentMethods != null)
			{
				for (CharSequence urlArgument : urlArgumentMethods)
				{
					attributesJson.append("dep", urlArgument);
				}
			}

			if (attributes.isAsynchronous() == false)
			{
				attributesJson.put("async", false);
			}

			String[] eventNames = attributes.getEventNames();
			if (eventNames.length == 1)
			{
				attributesJson.put("e", eventNames[0]);
			}
			else
			{
				for (String eventName : eventNames)
				{
					attributesJson.append("e", eventName);
				}
			}

			AjaxChannel channel = attributes.getChannel();
			if (channel != null)
			{
				attributesJson.put("ch", channel);
			}

			if (attributes.isAllowDefault())
			{
				attributesJson.put("ad", true);
			}

			Integer requestTimeout = attributes.getRequestTimeout();
			if (requestTimeout != null)
			{
				attributesJson.put("rt", requestTimeout);
			}

			boolean wicketAjaxResponse = attributes.isWicketAjaxResponse();
			if (wicketAjaxResponse == false)
			{
				attributesJson.put("wr", false);
			}

			String dataType = attributes.getDataType();
			if (AjaxRequestAttributes.XML_DATA_TYPE.equals(dataType) == false)
			{
				attributesJson.put("dt", dataType);
			}

			ThrottlingSettings throttlingSettings = attributes.getThrottlingSettings();
			if (throttlingSettings != null)
			{
				JSONObject throttlingSettingsJson = new JSONObject();
				throttlingSettingsJson.put("id", throttlingSettings.getId());
				throttlingSettingsJson.put("d", throttlingSettings.getDelay().getMilliseconds());
				if (throttlingSettings.getPostponeTimerOnUpdate())
				{
					throttlingSettingsJson.put("p", true);
				}
				attributesJson.put("tr", throttlingSettingsJson);
			}

			postprocessConfiguration(attributesJson, component);
		}
		catch (JSONException e)
		{
			throw new WicketRuntimeException(e);
		}

		String attributesAsJson = attributesJson.toString();

		return attributesAsJson;
	}

	/**
	 * Gives a chance to modify the JSON attributesJson that is going to be used as
	 * attributes for the Ajax call.
	 *
	 * @param attributesJson
	 *      the JSON object created by #renderAjaxAttributes()
	 * @param component
	 *      the component with the attached Ajax behavior
	 * @throws JSONException
	 *      thrown if an error occurs while modifying {@literal attributesJson} argument
	 */
	protected void postprocessConfiguration(JSONObject attributesJson, Component component)
		throws JSONException
	{
	}

	/**
	 * @return javascript that will generate an ajax GET request to this behavior
	 *  with its assigned component
	 */
	public CharSequence getCallbackScript()
	{
		return getCallbackScript(getComponent());
	}

	/**
	 * @param component the component to use when generating the attributes
	 * @return script that can be used to execute this Ajax behavior.
	 */
	// 'protected' because this method is intended to be called by other Behavior methods which
	// accept the component as parameter
	protected CharSequence getCallbackScript(final Component component)
	{
		CharSequence ajaxAttributes = renderAjaxAttributes(component);
		return "Wicket.Ajax.ajax("+ajaxAttributes+")";
	}

	/**
	 * Generates a javascript function that can take parameters and performs an AJAX call which
	 * includes these parameters. The generated code looks like this:
	 * 
	 * <pre>
	 * function(param1, param2) {
	 *    var attrs = attrsJson;
	 *    var params = {'param1': param1, 'param2': param2};
	 *    attrs.ep = jQuery.extend(attrs.ep, params);
	 *    Wicket.Ajax.ajax(attrs);
	 * }
	 * </pre>
	 * 
	 * @param extraParameters
	 * @return A function that can be used as a callback function in javascript
	 */
	protected CharSequence getCallbackFunction(String... extraParameters)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("function (");
		boolean first = true;
		for (String curExtraParameter : extraParameters)
		{
			if (!first)
				sb.append(",");
			else
				first = false;
			sb.append(curExtraParameter);
		}
		sb.append(") {\n");
		sb.append(getCallbackFunctionBody(extraParameters));
		sb.append("}\n");
		return sb;
	}

	/**
	 * Generates the body the {@linkplain #getCallbackFunction(String...) callback function}. To
	 * embed this code directly into a piece of javascript, make sure the extra parameters are
	 * available as local variables, global variables or within the closure.
	 * 
	 * @param extraParameters
	 * @return The body of the {@linkplain #getCallbackFunction(String...) callback function}.
	 */
	protected CharSequence getCallbackFunctionBody(String... extraParameters)
	{
		AjaxRequestAttributes attributes = getAttributes();
		CharSequence attrsJson = renderAjaxAttributes(getComponent(), attributes);
		StringBuilder sb = new StringBuilder();
		sb.append("var attrs = ");
		sb.append(attrsJson);
		sb.append(";\n");
		sb.append("var params = {");
		boolean first = true;
		for (String curExtraParameter : extraParameters)
		{
			if (!first)
				sb.append(",");
			else
				first = false;
			sb.append("'").append(curExtraParameter).append("': ").append(curExtraParameter);
		}
		sb.append("};\n");
		if (attributes.getExtraParameters().isEmpty())
			sb.append("attrs.ep = params;\n");
		else
			sb.append("attrs.ep = jQuery.extend({}, attrs.ep, params);\n");
		sb.append("Wicket.Ajax.ajax(attrs);\n");
		return sb;
	}

	/**
	 * @return an optional javascript expression that determines whether the request will actually
	 *         execute (in form of return XXX;);
	 * @deprecated Use {@link org.apache.wicket.ajax.attributes.AjaxRequestAttributes}
	 */
	@Deprecated
	protected CharSequence getPreconditionScript()
	{
		return null;
	}

	/**
	 * @return javascript that will run when the ajax call finishes with an error status
	 */
	@Deprecated
	protected CharSequence getFailureScript()
	{
		return null;
	}

	/**
	 * @return javascript that will run when the ajax call finishes successfully
	 */
	@Deprecated
	protected CharSequence getSuccessScript()
	{
		return null;
	}

	/**
	 * Provides an AjaxChannel for this Behavior.
	 * 
	 * @return an AjaxChannel - Defaults to null.
	 * @deprecated Use {@link org.apache.wicket.ajax.attributes.AjaxRequestAttributes}
	 */
	@Deprecated
	protected AjaxChannel getChannel()
	{
		return null;
	}

	/**
	 * Finds the markup id of the indicator. The default search order is: component, behavior,
	 * component's parent hierarchy.
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
	@Override
	public final void onRequest()
	{
		WebApplication app = (WebApplication)getComponent().getApplication();
		AjaxRequestTarget target = app.newAjaxRequestTarget(getComponent().getPage());

		RequestCycle requestCycle = RequestCycle.get();
		requestCycle.scheduleRequestHandlerAfterCurrent(target);

		respond(target);
	}

	/**
	 * @param target
	 *            The AJAX target
	 */
	protected abstract void respond(AjaxRequestTarget target);

}
