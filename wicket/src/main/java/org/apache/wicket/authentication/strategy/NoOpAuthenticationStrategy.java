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
package org.apache.wicket.authentication.strategy;

import org.apache.wicket.authentication.IAuthenticationStrategy;

/**
 * A no-op implementation. No username or password will be persisted or retrieved.
 * 
 * @author Juergen Donnerstag
 */
public class NoOpAuthenticationStrategy implements IAuthenticationStrategy
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public NoOpAuthenticationStrategy()
	{
	}

	/**
	 * @see org.apache.wicket.authentication.IAuthenticationStrategy#load()
	 */
	public String[] load()
	{
		return null;
	}

	/**
	 * @see org.apache.wicket.authentication.IAuthenticationStrategy#save(java.lang.String,
	 *      java.lang.String)
	 */
	public void save(final String username, final String password)
	{
	}

	/**
	 * @see org.apache.wicket.authentication.IAuthenticationStrategy#remove()
	 */
	public void remove()
	{
	}
}
