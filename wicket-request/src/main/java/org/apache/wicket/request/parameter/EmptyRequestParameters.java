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
package org.apache.wicket.request.parameter;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.string.StringValue;

/**
 * Read only empty {@link IRequestParameters}.
 * 
 * @author Matej Knopp
 */
public class EmptyRequestParameters implements IRequestParameters
{
	/** Singleton instance. */
	public static final EmptyRequestParameters INSTANCE = new EmptyRequestParameters();

	/**
	 * Construct.
	 */
	private EmptyRequestParameters()
	{
	}

	/**
	 * @see org.apache.wicket.request.IRequestParameters#getParameterNames()
	 */
	public Set<String> getParameterNames()
	{
		return Collections.emptySet();
	}

	/**
	 * @see org.apache.wicket.request.IRequestParameters#getParameterValue(java.lang.String)
	 */
	public StringValue getParameterValue(final String name)
	{
		return StringValue.valueOf((String)null);
	}

	/**
	 * @see org.apache.wicket.request.IRequestParameters#getParameterValues(java.lang.String)
	 */
	public List<StringValue> getParameterValues(final String name)
	{
		return null;
	}
}
