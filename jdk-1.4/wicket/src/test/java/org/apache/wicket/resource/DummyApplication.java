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
package org.apache.wicket.resource;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.session.ISessionStore;


/**
 * Dummy application used for resource loader testing.
 * 
 * @author Chris Turner
 */
public class DummyApplication extends WebApplication
{
	public Class getHomePage()
	{
		return null;
	}

	protected WebResponse newWebResponse(final HttpServletResponse servletResponse)
	{
		return new WebResponse(servletResponse);
	}
	
	protected void outputDevelopmentModeWarning()
	{
		// Do nothing.
	}
	
	/**
	 * @see org.apache.wicket.protocol.http.WebApplication#newSessionStore()
	 */
	protected ISessionStore newSessionStore()
	{
		return new HttpSessionStore(this);
	}
}
