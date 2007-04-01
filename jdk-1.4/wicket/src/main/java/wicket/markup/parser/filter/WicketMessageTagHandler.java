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
package wicket.markup.parser.filter;

import java.text.ParseException;

import wicket.Component;
import wicket.MarkupContainer;
import wicket.WicketRuntimeException;
import wicket.behavior.AbstractBehavior;
import wicket.behavior.IBehavior;
import wicket.markup.ComponentTag;
import wicket.markup.MarkupElement;
import wicket.markup.MarkupStream;
import wicket.markup.html.WebComponent;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.parser.AbstractMarkupFilter;
import wicket.markup.resolver.IComponentResolver;
import wicket.util.string.Strings;

/**
 * This is a markup inline filter and a component resolver. It identifies
 * wicket:message attributes and adds an attribute modifier to the component tag
 * that can localize
 * wicket:message="attr-name:i18n-key,attr-name-2:i18n-key-2,..." expressions,
 * replacing values of attributes specified by attr-name with a localizer lookup
 * with key i18n-key. If an attribute being localized has a set value that value
 * will be used as the default value for the localization lookup. This handler
 * also resolves and localizes raw markup with wicket:message attribute.
 * 
 * @author Juergen Donnerstag
 * @author Igor Vaynberg
 */
public final class WicketMessageTagHandler extends AbstractMarkupFilter
		implements
			IComponentResolver
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** TODO Post 1.2: General: Namespace should not be a constant */
	private final static String WICKET_MESSAGE_ATTRIBUTE_NAME = "wicket:message";

	/**
	 * The id automatically assigned to tags with wicket:message attribute but
	 * without id
	 */
	public final static String WICKET_MESSAGE_CONTAINER_ID = "-message_attr";

	/** singleton instance of {@link AttributeLocalizer} */
	public static final IBehavior ATTRIBUTE_LOCALIZER = new AttributeLocalizer();

	/**
	 * Construct.
	 */
	public WicketMessageTagHandler()
	{
	}

	/**
	 * 
	 * @see wicket.markup.parser.IMarkupFilter#nextTag()
	 * @return The next tag to be processed. Null, if not more tags are
	 *         available
	 */
	public final MarkupElement nextTag() throws ParseException
	{
		// Get the next tag from the next MarkupFilter in the chain
		// If null, no more tags are available
		final ComponentTag tag = nextComponentTag();
		if (tag == null)
		{
			return tag;
		}

		final String wicketMessageAttribute = tag.getAttributes().getString(
				WICKET_MESSAGE_ATTRIBUTE_NAME);
		if ((wicketMessageAttribute != null) && (wicketMessageAttribute.trim().length() > 0))
		{
			// check if this tag is raw markup
			if (tag.getId() == null)
			{
				// if this is a raw tag we need to set the id to something so
				// that wicket will not merge this as raw markup and instead
				// pass it on to a resolver
				tag.setId(WICKET_MESSAGE_CONTAINER_ID);

				// There is no point attaching the attributelocalizer to this
				// tag because it will be represented by an auto component and
				// they do not inherit the behaviors from their component tag
				// unlike regular components, instead the attributelocalizer
				// will be added by the code that creates the auto component
			}
			else
			{
				// if this is a component tag we attach a behavior to it that
				// will in turn be attached to the component that is attached to
				// this tag
				tag.addBehavior(ATTRIBUTE_LOCALIZER);
			}
		}

		return tag;
	}

	/**
	 * Attribute localizing behavior. See the javadoc of
	 * {@link WicketMessageTagHandler} for details.
	 * 
	 * @author Igor Vaynberg (ivaynberg)
	 */
	public static class AttributeLocalizer extends AbstractBehavior
	{
		private static final long serialVersionUID = 1L;

		/**
		 * @see wicket.behavior.AbstractBehavior#onComponentTag(wicket.Component,
		 *      wicket.markup.ComponentTag)
		 */
		public void onComponentTag(final Component component, final ComponentTag tag)
		{
			String expr = tag.getAttributes().getString(WICKET_MESSAGE_ATTRIBUTE_NAME);
			if (!Strings.isEmpty(expr))
			{
				expr = expr.trim();

				String[] attrsAndKeys = expr.split(",");

				for (int i = 0; i < attrsAndKeys.length; i++)
				{
					String attrAndKey = attrsAndKeys[i];
					int colon = attrAndKey.indexOf(":");
					// make sure the attribute-key pair is valid
					if (attrAndKey.length() < 3 || colon < 1 || colon > attrAndKey.length() - 2)
					{
						throw new WicketRuntimeException(
								"wicket:message attribute contains an invalid value [[" + expr
										+ "]], must be of form (attr:key)+");
					}

					String attr = attrAndKey.substring(0, colon);
					String key = attrAndKey.substring(colon + 1);

					String value;
					// we need to call the proper getString() method based on
					// whether or not we have a default value
					if (tag.getAttributes().containsKey(attr))
					{
						value = component.getString(key, null, tag.getAttributes().getString(attr));
					}
					else
					{
						value = component.getString(key);
					}
					tag.put(attr, value);
				}
			}
		}
	}

	public boolean resolve(MarkupContainer container, MarkupStream markupStream, ComponentTag tag)
	{
		// localize any raw markup that has wicket:message attrs

		if (WICKET_MESSAGE_CONTAINER_ID.equals(tag.getId()))
		{
			Component wc = null;
			if (tag.isOpenClose())
			{
				wc = new WebComponent(WICKET_MESSAGE_CONTAINER_ID
						+ container.getPage().getAutoIndex());
			}
			else
			{
				wc = new WebMarkupContainer(WICKET_MESSAGE_CONTAINER_ID
						+ container.getPage().getAutoIndex());
			}
			wc.add(new AttributeLocalizer());
			container.autoAdd(wc);
			return true;
		}
		return false;
	}
}
