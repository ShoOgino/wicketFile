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
package org.apache.wicket.request.handler.basic;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * The empty request target does nothing in itself but instead relies on some other source to
 * generate a response. It can be regarded as a dummy implementation.
 * 
 * @author Eelco Hillenius
 */
public final class EmptyRequestHandler implements IRequestHandler
{
	/** immutable hashcode. */
	private static final int HASH = 17 * 3214424;

	/** singleton instance. */
	private static final EmptyRequestHandler instance = new EmptyRequestHandler();

	/**
	 * Construct.
	 */
	private EmptyRequestHandler()
	{
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static final EmptyRequestHandler getInstance()
	{
		return instance;
	}

	/**
	 * Does nothing at all.
	 * 
	 * @see org.apache.wicket.request.IRequestHandler#respond(org.apache.wicket.request.cycle.RequestCycle)
	 */
	public void respond(RequestCycle requestCycle)
	{
	}

	/**
	 * @see org.apache.wicket.request.IRequestHandler#detach(org.apache.wicket.request.cycle.RequestCycle)
	 */
	public void detach(RequestCycle requestCycle)
	{
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof EmptyRequestHandler)
		{
			return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return HASH;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EmptyRequestTarget";
	}
}
