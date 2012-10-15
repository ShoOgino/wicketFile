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
package org.apache.wicket.core.util.objects.checker;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for AbstractObjectChecker
 */
public class AbstractObjectCheckerTest extends Assert
{
	@Test
	public void doCheckIsNotCalledForExcludedTypes()
	{
		List exclusions = Arrays.asList(CharSequence.class);

		IObjectChecker checker = new AbstractObjectChecker(exclusions)
		{
			@Override
			protected Result doCheck(Object object)
			{
				throw new AssertionError("Must not be called");
			}
		};

		IObjectChecker.Result result = checker.check("A String. It's type is excluded by CharSequence");
		assertEquals(IObjectChecker.Result.SUCCESS, result);
	}
}
