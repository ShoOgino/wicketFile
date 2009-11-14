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
package org.apache.wicket.ng.request.cycle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.ng.request.RequestHandler;
import org.apache.wicket.ng.request.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages stack of {@link RequestHandler}s.
 * 
 * @author Matej Knopp
 */
public abstract class RequestHandlerStack
{
	private Response response;

	/**
	 * Construct.
	 * 
	 * @param response
	 */
	public RequestHandlerStack(Response response)
	{
		this.response = response;
	}

	// we need both Queue and List interfaces
	private final LinkedList<RequestHandler> requestHandlers = new LinkedList<RequestHandler>();

	private final List<RequestHandler> inactiveRequestHandlers = new ArrayList<RequestHandler>();

	private RequestHandler scheduledAfterCurrent = null;

	/**
	 * Returns currently active {@link RequestHandler}.
	 * 
	 * @return Active RequestHandler or <code>null</code> if no handler is active.
	 */
	public RequestHandler getActiveRequestHandler()
	{
		return requestHandlers.peek();
	}

	/**
	 * Executes the specified {@link RequestHandler}. When the specified {@link RequestHandler}
	 * finishes, the {@link RequestHandler} that invoked this method continues (unless the new
	 * {@link RequestHandler} called {@link #replaceAllRequestHandlers(RequestHandler)}.
	 * 
	 * @param handler
	 */
	public void executeRequestHandler(RequestHandler handler)
	{
		final boolean first = requestHandlers.isEmpty();
		requestHandlers.add(handler);

		RequestHandler replacementHandler = null;
		Response originalResponse = response;
		try
		{
			handler.respond(getRequestCycle());
		}
		catch (ReplaceHandlerException exception)
		{
			if (exception.removeAll && !first)
			{
				throw exception;
			}
			replacementHandler = exception.replacementRequestHandler;
		}
		finally
		{
			response = originalResponse;
			requestHandlers.poll();
			inactiveRequestHandlers.add(handler);
		}

		RequestHandler scheduled = scheduledAfterCurrent;
		scheduledAfterCurrent = null;

		if (replacementHandler != null)
		{
			executeRequestHandler(replacementHandler);
		}
		else if (scheduled != null)
		{
			executeRequestHandler(scheduled);
		}
	}

	/**
	 * Schedules the request handler to be executed after current request handler finishes. If there
	 * is already another request handler scheduled it will be discarded and overwritten by the new
	 * one. If {@link #replaceCurrentRequestHandler(RequestHandler)} or
	 * {@link #replaceAllRequestHandlers(RequestHandler)} is invoked during current request handler
	 * execution the scheduled handler will be also discarded.
	 * 
	 * @param handler
	 *            handler to be executed after current request handler finishes
	 */
	public void scheduleRequestHandlerAfterCurrent(RequestHandler handler)
	{
		scheduledAfterCurrent = handler;
	}

	/**
	 * Replaces the currently executed {@link RequestHandler} with new {@link RequestHandler}. The
	 * currently executed {@link RequestHandler} is terminated and the new {@link RequestHandler} is
	 * executed.
	 * 
	 * @param handler
	 */
	// FIXME
	// Is this method really useful for anything? To execute request handler after current
	// #scheduleRequestHandlerAfterCurrent is better alternative because it doesn't terminate
	// current
	// request handler.
	// To restart request processing #replaceAllRequestHandlers is better alternative because it
	// unrolls
	// entire stack and cancels all request handlers in stack
	public void replaceCurrentRequestHandler(RequestHandler handler)
	{
		if (requestHandlers.isEmpty())
		{
			executeRequestHandler(handler);
		}
		else
		{
			throw new ReplaceHandlerException(handler, false);
		}
	}

	/**
	 * Removes the whole {@link RequestHandler} stack, terminates currently running
	 * {@link RequestHandler} and executes the new {@link RequestHandler}.
	 * 
	 * @param handler
	 */
	public void replaceAllRequestHandlers(RequestHandler handler)
	{
		if (requestHandlers.isEmpty())
		{
			executeRequestHandler(handler);
		}
		else
		{
			throw new ReplaceHandlerException(handler, true);
		}
	}

	/**
	 * Exception to stop current request handler and execute a new one.
	 * 
	 * @author Matej Knopp
	 */
	public static class ReplaceHandlerException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		private final boolean removeAll;
		private final RequestHandler replacementRequestHandler;

		/**
		 * Construct.
		 * 
		 * @param replacementRequestHandler
		 * @param removeAll
		 */
		public ReplaceHandlerException(RequestHandler replacementRequestHandler, boolean removeAll)
		{
			this.replacementRequestHandler = replacementRequestHandler;
			this.removeAll = removeAll;
		}

		@Override
		public synchronized Throwable fillInStackTrace()
		{
			// don't do anything here
			return null;
		}
	};

	/**
	 * Returns the active {@link Response}.
	 * 
	 * @return response object.
	 */
	public Response getResponse()
	{
		return response;
	}

	/**
	 * Replaces current {@link Response} with new {@link Response} instance. The original response
	 * is always restored after the {@link RequestHandler#respond(RequestCycle)} method is finished.
	 * 
	 * @param response
	 * @return Response being replaced.
	 */
	public Response setResponse(Response response)
	{
		Response current = this.response;
		this.response = response;
		return current;
	}

	protected abstract RequestCycle getRequestCycle();

	/**
	 * Detaches all {@link RequestHandler}s.
	 */
	public void detach()
	{
		if (!requestHandlers.isEmpty())
		{
			// All requests handlers should be inactive at this point
			log.warn("Some of the request handlers are still active.");

			inactiveRequestHandlers.addAll(requestHandlers);
			requestHandlers.clear();
		}

		for (RequestHandler handler : inactiveRequestHandlers)
		{
			try
			{
				handler.detach(getRequestCycle());
			}
			catch (Throwable exception)
			{
				log.error("Error detaching RequestHandler", exception);
			}
		}
	}

	private static final Logger log = LoggerFactory.getLogger(RequestHandlerStack.class);
}
