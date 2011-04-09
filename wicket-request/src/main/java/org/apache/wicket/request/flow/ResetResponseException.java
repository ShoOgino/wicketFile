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
package org.apache.wicket.request.flow;

import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.RequestHandlerStack.ReplaceHandlerException;

/**
 * An exception that resets the response before executing the specified request handler
 * 
 * @author Igor Vaynberg (ivaynberg)
 * @author Jonathan Locke
 */
public abstract class ResetResponseException extends ReplaceHandlerException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param handler
	 */
	protected ResetResponseException(final IRequestHandler handler)
	{
		super(new ResponseResettingDecorator(handler), true);
	}

	private static class ResponseResettingDecorator implements IRequestHandler
	{
		private final IRequestHandler delegate;

		/**
		 * Construct.
		 * 
		 * @param delegate
		 */
		public ResponseResettingDecorator(final IRequestHandler delegate)
		{
			this.delegate = delegate;
		}

		public void detach(final IRequestCycle requestCycle)
		{
			delegate.detach(requestCycle);
		}

		public void respond(final IRequestCycle requestCycle)
		{
			requestCycle.getResponse().reset();
			delegate.respond(requestCycle);
		}
	}
}
