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
package org.apache.wicket.markup.html.panel;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a no-op sourcing strategy.
 * 
 * @author Juergen Donnerstag
 */
public final class DefaultMarkupSourcingStrategy implements IMarkupSourcingStrategy
{
	/** Log for reporting. */
	private static final Logger log = LoggerFactory.getLogger(DefaultMarkupSourcingStrategy.class);

	private static DefaultMarkupSourcingStrategy instance = new DefaultMarkupSourcingStrategy();

	/**
	 * 
	 * @return A singleton of the strategy
	 */
	public final static DefaultMarkupSourcingStrategy get()
	{
		return instance;
	}

	/**
	 * Construct. Please use {@link #get()} instead.
	 */
	private DefaultMarkupSourcingStrategy()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public void onComponentTag(final Component component, final ComponentTag tag)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public void onComponentTagBody(final Component component, final MarkupStream markupStream,
		final ComponentTag openTag)
	{
		component.onComponentTagBody(markupStream, openTag);
	}

	/**
	 * {@inheritDoc}
	 */
	public IMarkupFragment getMarkup(final MarkupContainer container, final Component child)
	{
		// If the sourcing strategy did not provide one, than ask the component.
		// Get the markup for the container
		IMarkupFragment markup = container.getMarkup();
		if (markup == null)
		{
			return null;
		}

		if (child == null)
		{
			return markup;
		}

		// Find the child's markup
		markup = markup.find(child.getId());
		if (markup != null)
		{
			return markup;
		}

		// This is to make migration for Items from 1.4 to 1.5 more easy
		if (Character.isDigit(child.getId().charAt(0)))
		{
			String id = child.getId();
			boolean miss = false;
			for (int i = 1; i < id.length(); i++)
			{
				if (Character.isDigit(id.charAt(i)) == false)
				{
					miss = true;
					break;
				}
			}

			if (miss == false)
			{
				// The LoopItems markup is equal to the Loops markup
				markup = container.getMarkup();

				if (log.isWarnEnabled())
				{
					log.warn("1.4 to 1.5 migration issue: your item component should be derived from AbstractItem. Item=" +
						child.toString());
				}
			}
		}

		return markup;
	}

	/**
	 * {@inheritDoc}
	 */
	public void renderHead(final Component component, HtmlHeaderContainer container)
	{
	}
}
