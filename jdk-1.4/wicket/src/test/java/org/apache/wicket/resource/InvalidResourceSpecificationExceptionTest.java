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

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.ExceptionTestBase;


/**
 * Unit tests for the <code>InvalidResourceSpecificationException</code>.
 * 
 * @author Chris Turner
 */
public class InvalidResourceSpecificationExceptionTest extends ExceptionTestBase
{

	/**
	 * Create the test.
	 * 
	 * @param s
	 *            The test name
	 */
	public InvalidResourceSpecificationExceptionTest(String s)
	{
		super(s);
	}

	/**
	 * Return the name of the exception class to be tested.
	 * 
	 * @return The name of the exception class
	 */
	protected String getExceptionClassName()
	{
		return WicketRuntimeException.class.getName();
	}
}
