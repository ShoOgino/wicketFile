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
package org.apache.wicket.feedback;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Session;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import sun.swing.SwingUtilities2.Section;

/**
 * Collects feedback messages from all the places where they can be stored.
 * 
 * @author igor
 */
public final class FeedbackCollector
{
	private final Component component;
	private boolean includeSession = true;
	private boolean recursive = true;

	/**
	 * Constructs a collector that will only collect messages from {@link Session}. To collect
	 * messages from session and components use {@link #FeedbackCollector(Component)}.
	 */
	public FeedbackCollector()
	{
		this(null);
	}

	/**
	 * Constrcuts a collector that will collect messages from {@link Session} and specified
	 * {@code container}
	 * 
	 * @param component
	 *            root component from which feedback will be collected
	 */
	public FeedbackCollector(Component component)
	{
		this.component = component;
	}

	/**
	 * Controls whether or not feedback from the {@link Session} will be collected
	 * 
	 * See {@link Section#getFeedbackMessages}
	 * 
	 * @param value
	 * @return {@code this} for chaining
	 */
	public FeedbackCollector setIncludeSession(boolean value)
	{
		includeSession = value;
		return this;
	}

	/**
	 * Controls whether or not feedback will be collected recursively from the decendants of the
	 * specified component.
	 * 
	 * @param value
	 * @return {@code this} for chaining
	 */
	public FeedbackCollector setRecursive(boolean value)
	{
		recursive = value;
		return this;
	}

	/**
	 * Collects all feedback messages
	 * 
	 * @return a {@link List} of collected messages
	 */
	public List<FeedbackMessage> collect()
	{
		return collect(IFeedbackMessageFilter.ALL);
	}

	/**
	 * Collects all feedback messages that match the specified {@code filter}
	 * 
	 * @param filter
	 * @return a {@link List} of collected messages
	 */
	public List<FeedbackMessage> collect(final IFeedbackMessageFilter filter)
	{
		final List<FeedbackMessage> messages = new ArrayList<FeedbackMessage>();

		if (includeSession && Session.exists())
		{
			messages.addAll(Session.get().getFeedbackMessages().messages(filter));
		}

		if (component != null && component.hasFeedbackMessage())
		{
			messages.addAll(component.getFeedbackMessages().messages(filter));
		}

		if (component != null && recursive && component instanceof MarkupContainer)
		{
			((MarkupContainer)component).visitChildren(new IVisitor<Component, Void>()
			{

				@Override
				public void component(Component object, IVisit<Void> visit)
				{
					if (object.hasFeedbackMessage())
					{
						messages.addAll(object.getFeedbackMessages().messages(filter));
					}
				}
			});
		}

		return messages;
	}
}
