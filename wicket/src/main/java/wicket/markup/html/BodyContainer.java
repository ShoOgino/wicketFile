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
package wicket.markup.html;

import java.io.Serializable;

import wicket.AttributeModifier;
import wicket.Component;
import wicket.markup.html.body.BodyTagAttributeModifier;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * This is not realy a Container component in the standard Wicket sense. It
 * rather is a facade which allows to easily add attribute modifier to the
 * MarkupContainer associated with &lt;body&gt;. That container might be a
 * Wicket generated one (automatically) or one manually added by a user the
 * standard Wicket way.
 * <p>
 * Container for the page body. This is mostly an internal class that is used
 * for contributions to the body tag's onload event handler.
 * 
 * @author jcompagner
 * 
 * TODO Post 1.2: Change the name. It is not derived from MarkupContainer
 */
public final class BodyContainer implements Serializable
{
	/** body tag "onunload" attribute */
	public static final String ONUNLOAD = "onunload";

	/** body tag "onload" attribute */
	public static final String ONLOAD = "onload";

	private static final long serialVersionUID = 1L;

	/** The container id */
	private final String id;

	/** The webpage where the body container is in */
	private final WebPage page;

	/**
	 * Helper class
	 */
	public static class AppendingAttributeModifier extends AttributeModifier
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param attribute
		 * @param replaceModel
		 */
		public AppendingAttributeModifier(final String attribute,
				IModel<? extends CharSequence> replaceModel)
		{
			super(attribute, true, replaceModel);
		}

		/**
		 * 
		 * @see wicket.AttributeModifier#newValue(java.lang.String,
		 *      java.lang.String)
		 */
		@Override
		protected String newValue(final String currentValue, final String replacementValue)
		{
			if (currentValue != null && !currentValue.trim().endsWith(";"))
			{
				return currentValue + ";" + replacementValue;
			}
			return (currentValue == null ? replacementValue : currentValue + replacementValue);
		}
	}

	/**
	 * Construct.
	 * 
	 * @param page
	 *            The webpage where the body container is in
	 * @param id
	 *            The container id
	 */
	public BodyContainer(final WebPage page, final String id)
	{
		this.page = page;
		this.id = id;
	}

	/**
	 * Add a new AttributeModifier to the body container which appends the
	 * 'value' to the 'attribute' of the body tag. Remember the component
	 * which requested to add the modified to the body container. This for
	 * example is required in cases where on a dynamic page the Component (e.g.
	 * a Panel) gets removed and/or replaced and the body attribute modifier
	 * must be removed/replaced as well.
	 * 
	 * @param attribute
	 *            The name of the attribute
	 * @param model
	 *            The model which holds the value to be appended to 'onLoad'
	 * @param behaviorOwner
	 *            The component which 'owns' the attribute modifier. Null is a
	 *            allowed value.
	 * @return this
	 * 
	 * @TODO Post 1.2: A listener hook on IBheavior which gets called on removal
	 *       of the component would be the better solution
	 */
	public final BodyContainer addModifier(final String attribute,
			final IModel<? extends CharSequence> model, final Component behaviorOwner)
	{
		final Component bodyContainer = page.get(id);
		if (behaviorOwner == null)
		{
			bodyContainer.add(new AppendingAttributeModifier(attribute, model));
		}
		else
		{

			bodyContainer.add(new BodyTagAttributeModifier(attribute, true, model, behaviorOwner));
		}
		return this;
	}

	/**
	 * Add a new AttributeModifier to the body container which appends the
	 * 'value' to the onLoad attribute of the body tag. Remember the component
	 * which requested to add the modified to the body container. This for
	 * example is required in cases where on a dynamic page the Component (e.g.
	 * a Panel) gets removed and/or replaced and the body attribute modifier
	 * must be removed/replaced as well.
	 * 
	 * @param attribute
	 *            The name of the attribute
	 * @param value
	 *            The value to append to 'onLoad'
	 * @param behaviorOwner
	 *            The component which 'owns' the attribute modifier. Null is a
	 *            allowed value.
	 * @return this
	 * 
	 * @TODO Post 1.2: A listener hook on IBheavior which gets called on removal
	 *       of the component would be the better solution
	 */
	public final BodyContainer addModifier(final String attribute, final CharSequence value,
			final Component behaviorOwner)
	{
		final IModel<CharSequence> model = new Model<CharSequence>(value);
		return addModifier(attribute, model, behaviorOwner);
	}

	/**
	 * Add a new AttributeModifier to the body container which appends the
	 * 'value' to the onLoad attribute of the body tag. Remember the component
	 * which requested to add the modified to the body container. This for
	 * example is required in cases where on a dynamic page the Component (e.g.
	 * a Panel) gets removed and/or replaced and the body attribute modifier
	 * must be removed/replaced as well.
	 * 
	 * @param model
	 *            The model which holds the value to be appended to 'onLoad'
	 * @param behaviorOwner
	 *            The component which 'owns' the attribute modifier. Null is a
	 *            allowed value.
	 * @return this
	 * 
	 * @TODO Post 1.2: A listener hook on IBheavior which gets called on removal
	 *       of the component would be the better solution
	 */
	public final BodyContainer addOnLoadModifier(final IModel<? extends CharSequence> model,
			final Component behaviorOwner)
	{
		return addModifier(ONLOAD, model, behaviorOwner);
	}

	/**
	 * Add a new AttributeModifier to the body container which appends the
	 * 'value' to the onLoad attribute of the body tag. Remember the component
	 * which requested to add the modified to the body container. This for
	 * example is required in cases where on a dynamic page the Component (e.g.
	 * a Panel) gets removed and/or replaced and the body attribute modifier
	 * must be removed/replaced as well.
	 * 
	 * @param value
	 *            The value to append to 'onLoad'
	 * @param behaviorOwner
	 *            The component which 'owns' the attribute modifier. Null is a
	 *            allowed value.
	 * @return this
	 * 
	 * @TODO Post 1.2: A listener hook on IBheavior which gets called on removal
	 *       of the component would be the better solution
	 */
	public final BodyContainer addOnLoadModifier(final CharSequence value,
			final Component behaviorOwner)
	{
		return addModifier(ONLOAD, value, behaviorOwner);
	}

	/**
	 * Add a new AttributeModifier to the body container which appends the
	 * 'value' to the onUnLoad attribute of the body tag. Remember the component
	 * which requested to add the modified to the body container. This for
	 * example is required in cases where on a dynamic page the Component (e.g.
	 * a Panel) gets removed and/or replaced and the body attribute modifier
	 * must be removed/replaced as well.
	 * 
	 * @param model
	 *            The model which holds the value to be appended to 'onUnLoad'
	 * @param behaviorOwner
	 *            The component which 'owns' the attribute modifier. Null is a
	 *            allowed value.
	 * @return this
	 * 
	 * @TODO Post 1.2: A listener hook on IBheavior which gets called on removal
	 *       of the component would be the better solution
	 */
	public final BodyContainer addOnUnLoadModifier(final IModel<? extends CharSequence> model,
			final Component behaviorOwner)
	{
		return addModifier(ONUNLOAD, model, behaviorOwner);
	}

	/**
	 * Add a new AttributeModifier to the body container which appends the
	 * 'value' to the onUnLoad attribute of the body tag. Remember the component
	 * which requested to add the modified to the body container. This for
	 * example is required in cases where on a dynamic page the Component (e.g.
	 * a Panel) gets removed and/or replaced and the body attribute modifier
	 * must be removed/replaced as well.
	 * 
	 * @param value
	 *            The value to append to 'onUnLoad'
	 * @param behaviorOwner
	 *            The component which 'owns' the attribute modifier. Null is a
	 *            allowed value.
	 * @return this
	 * 
	 * @TODO Post 1.2: A listener hook on IBehavior which gets called on removal
	 *       of the component would be the better solution
	 */
	public final BodyContainer addOnUnLoadModifier(final CharSequence value,
			final Component behaviorOwner)
	{
		return addModifier(ONUNLOAD, value, behaviorOwner);
	}

	/**
	 * Get the real body container (WebMarkupContainer)
	 * 
	 * @return WebMarkupContainer associated with the &lt;body&gt; tag
	 */
	public WebMarkupContainer getBodyContainer()
	{
		return (WebMarkupContainer)this.page.get(this.id);
	}
}
