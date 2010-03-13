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
package org.apache.wicket.contrib.velocity;

import junit.framework.TestCase;

import org.apache.wicket.util.tester.WicketTester;

/**
 * Test header contributions.
 */
public class VelocityJavascriptContributorTest extends TestCase
{
	/**
	 * Test that the header contribution is added correctly.
	 */
	public void testRenderHead()
	{
		WicketTester tester = new WicketTester();
		tester.startPage(VelocityJavascriptPage.class);
		System.out.println(tester.getLastResponseAsString());
		tester.assertContains("msg1: " + VelocityJavascriptPage.MSG1);
		tester.dumpPage();
		tester.destroy();
	}
}
