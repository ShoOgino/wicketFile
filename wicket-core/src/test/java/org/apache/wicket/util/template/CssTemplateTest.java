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
package org.apache.wicket.util.template;

import java.util.Map;

import org.junit.Test;


/**
 * Test of {@link CssTemplate}
 */
public class CssTemplateTest
{

	/**
	 * Test that a {@link CssTemplate} can be constructed without problems.
	 * 
	 * @see <a href="https://issues.apache.org/jira/browse/WICKET-3187">WICKET-3187</a>
	 */
	@Test
	public void simpleConstructor()
	{
		new CssTemplate(new TextTemplate()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public TextTemplate interpolate(Map<String, ?> variables)
			{
				return this;
			}

			@Override
			public String getString()
			{
				return "";
			}
		});
	}
}
