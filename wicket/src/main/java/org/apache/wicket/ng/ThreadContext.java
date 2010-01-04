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
package org.apache.wicket.ng;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ng.request.cycle.RequestCycle;

/**
 * Holds thread local state for Wicket data.
 * 
 * @author Matej Knopp
 */
public class ThreadContext
{
	private Application application;

	private RequestCycle requestCycle;

	private Session session;

	private static ThreadLocal<ThreadContext> threadLocal = new ThreadLocal<ThreadContext>();

	/**
	 * INTERNAL METHOD
	 * 
	 * @param createIfDoesNotExist
	 * @return
	 */
	public static ThreadContext get(boolean createIfDoesNotExist)
	{
		ThreadContext context = threadLocal.get();
		if (createIfDoesNotExist && context == null)
		{
			context = new ThreadContext();
			threadLocal.set(context);
		}
		return context;
	}

	/**
	 * @return {@link Application} bound to current thread
	 */
	public static Application getApplication()
	{
		ThreadContext context = get(false);
		return context != null ? context.application : null;
	}

	/**
	 * Binds the specified application to current thread.
	 * 
	 * @param application
	 */
	public static void setApplication(Application application)
	{
		ThreadContext context = get(true);
		context.application = application;
	}

	/**
	 * @return {@link RequestCycle} bound to current thrad
	 */
	public static RequestCycle getRequestCycle()
	{
		ThreadContext context = get(false);
		return context != null ? context.requestCycle : null;
	}

	/**
	 * Binds the {@link RequestCycle} to current thread.
	 * 
	 * @param requestCycle
	 */
	public static void setRequestCycle(RequestCycle requestCycle)
	{
		ThreadContext context = get(true);
		context.requestCycle = requestCycle;
	}

	/**
	 * @return {@link Session} bound to current thread
	 */
	public static Session getSession()
	{
		ThreadContext context = get(false);
		return context != null ? context.session : null;
	}

	/**
	 * Binds the session to current thread.
	 * 
	 * @param session
	 */
	public static void setSession(Session session)
	{
		ThreadContext context = get(true);
		context.session = session;
	}

	/**
	 * Cleans the {@link ThreadContext} and returns previous context.
	 * 
	 * @return old {@link ThreadContext}
	 */
	public static ThreadContext detach()
	{
		ThreadContext value = threadLocal.get();
		threadLocal.remove();
		return value;
	}

	/**
	 * Restores the context
	 * 
	 * @param threadContext
	 * @see #detach()
	 */
	public static void restore(ThreadContext threadContext)
	{
		threadLocal.set(threadContext);
	}

	/**
	 * Construct.
	 */
	private ThreadContext()
	{
	}
}
