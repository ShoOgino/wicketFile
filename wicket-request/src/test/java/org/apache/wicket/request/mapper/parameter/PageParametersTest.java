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
package org.apache.wicket.request.mapper.parameter;

import java.util.List;

import org.apache.wicket.util.string.StringValue;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link PageParameters}
 */
public class PageParametersTest extends Assert
{

	/**
	 * Tests that adding a key with String[] value is properly parsed and there a several
	 * StringValue's for that key
	 */
	@Test
	public void addStringArrayValue()
	{
		PageParameters parameters = new PageParameters();

		String[] input = new String[] { "v1", "v2" };
		parameters.add("key", input);

		List<StringValue> stringValue = parameters.getValues("key");

		for (String in : input)
		{

			boolean found = false;
			for (StringValue value : stringValue)
			{
				if (value.toString().equals(in))
				{
					found = true;
					break;
				}
			}

			if (found == false)
			{
				throw new IllegalStateException("Expected to find a StringValue with value: " + in);
			}
		}
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-3906
	 */
	@Test
	public void getPosition()
	{
		PageParameters parameters = new PageParameters();
		parameters.set("named1", "value1", 3);
		assertEquals(
			"Adding a parameter at position out of the size of the list will just append it", 0,
			parameters.getPosition("named1"));

		parameters.set("named2", "value2", 0);
		assertEquals(0, parameters.getPosition("named2"));
		assertEquals("'named1' should be moved back", 1, parameters.getPosition("named1"));


		parameters.set("named3", "value3", -100);
		assertEquals(0, parameters.getPosition("named2"));
		assertEquals(1, parameters.getPosition("named1"));
		assertEquals("Adding a parameter with negative position will just append it.", 2,
			parameters.getPosition("named3"));
	}
}
