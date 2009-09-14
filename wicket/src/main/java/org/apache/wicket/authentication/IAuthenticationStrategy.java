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
package org.apache.wicket.authentication;

import org.apache.wicket.Application;

/**
 * The interface of an authorization strategy which is accessible via
 * {@link Application#getSecuritySettings()}. Implementations determine how logon data (username and
 * password) are persisted (e.g. Cookie), retrieved and removed.
 * 
 * @author Juergen Donnerstag
 */
public interface IAuthenticationStrategy
{
	/**
	 * If "rememberMe" is enabled, than load login name and password from the persistence storage
	 * (e.g. Cookie) for automatic sign in. This is useful for applications which users typically
	 * have open the whole day but where the server invalidates the session after a timeout and you
	 * want to force the user to sign in again and again during the day.
	 * 
	 * @return [0] = username, [1] = password, null if not found
	 */
	String[] load();

	/**
	 * If "rememberMe" is enabled and login was successful, than store username and password in the
	 * persistence store (e.g. Cookie).
	 * 
	 * @param username
	 * @param password
	 */
	void save(final String username, final String password);

	/**
	 * When the user logs out (session invalidation), than remove username and password from the
	 * persistence store
	 */
	void remove();
}
