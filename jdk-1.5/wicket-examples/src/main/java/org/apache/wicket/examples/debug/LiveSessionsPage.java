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
package org.apache.wicket.examples.debug;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.RequestLogger.SessionData;
import org.apache.wicket.util.lang.Bytes;


/**
 * @author jcompagner
 */
public class LiveSessionsPage extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public LiveSessionsPage()
	{
		add(new Image("bug"));

		add(new ApplicationView("application", Application.get()));

		Link link = new Link("togglelink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				WebApplication webApplication = (WebApplication)Application.get();
				webApplication.getRequestLoggerSettings().setRequestsWindowSize(500);
				boolean enabled = webApplication.getRequestLoggerSettings()
					.isRequestLoggerEnabled();
				webApplication.getRequestLoggerSettings().setRequestLoggerEnabled(!enabled);
			}
		};
		link.add(new Label("toggletext", new Model<String>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject()
			{
				WebApplication webApplication = (WebApplication)Application.get();
				IRequestLogger requestLogger = webApplication.getRequestLogger();
				if (requestLogger == null)
				{
					return "Enable request recording";
				}
				else
				{
					return "Disable request recording";
				}
			}
		}));
		add(link);
		add(new Label("totalSessions", new Model<Integer>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Integer getObject()
			{
				return getRequestLogger().getTotalCreatedSessions();
			}
		}));
		add(new Label("peakSessions", new Model<Integer>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Integer getObject()
			{
				return getRequestLogger().getPeakSessions();
			}
		}));
		add(new Label("liveSessions", new Model<Integer>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Integer getObject()
			{
				return getRequestLogger().getPeakSessions();
			}
		}));

		Model<ArrayList<SessionData>> sessionModel = new Model<ArrayList<SessionData>>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public ArrayList<SessionData> getObject()
			{
				return new ArrayList<SessionData>(
					Arrays.asList(getRequestLogger().getLiveSessions()));
			}
		};
		PageableListView listView = new PageableListView("sessions", sessionModel, 50)
		{
			private static final long serialVersionUID = 1L;

			private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm:ss.SSS");

			@Override
			protected void populateItem(ListItem item)
			{
				final SessionData sd = (SessionData)item.getModelObject();
				Link link = new Link("id")
				{
					private static final long serialVersionUID = 1L;

					/**
					 * @see org.apache.wicket.markup.html.link.Link#onClick()
					 */
					@Override
					public void onClick()
					{
						setResponsePage(new RequestsPage(sd));
					}
				};
				link.add(new Label("id", new Model<String>(sd.getSessionId())));
				item.add(link);
				item.add(new Label("lastRequestTime", new Model<String>(
					sdf.format(sd.getLastActive()))));
				item.add(new Label("requestCount", new Model<Long>(sd.getNumberOfRequests())));
				item.add(new Label("requestsTime", new Model<Long>(sd.getTotalTimeTaken())));
				item.add(new Label("sessionSize",
					new Model<Bytes>(Bytes.bytes(sd.getSessionSize()))));
			}
		};
		add(listView);

		PagingNavigator navigator = new PagingNavigator("navigator", listView);
		add(navigator);
	}

	IRequestLogger getRequestLogger()
	{
		WebApplication webApplication = (WebApplication)Application.get();
		final IRequestLogger requestLogger;
		if (webApplication.getRequestLogger() == null)
		{
			// make default one.
			requestLogger = new RequestLogger();
		}
		else
		{
			requestLogger = webApplication.getRequestLogger();
		}
		return requestLogger;
	}
}
