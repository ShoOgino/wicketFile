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
package org.apache.wicket.request.target.coding;

import org.apache.wicket.PageParameters;
import org.apache.wicket.WicketTestCase;

/**
 * @author jcompagner
 */
public class IndexParamWithStatelessIFrameTest extends WicketTestCase
{
	/**
	 * @throws Exception
	 */
	public void testIndexedUrlMountedPageWithComponentThatUsesUrlForResourceListener()
		throws Exception
	{
		tester.getApplication().mount(
			new IndexedParamUrlCodingStrategy("/test", TestPageWithIFrame.class));
		TestPageWithIFrame test = new TestPageWithIFrame(new PageParameters("0=foo,1=bar"));
		tester.startPage(test);
		tester.assertRenderedPage(TestPageWithIFrame.class);
		tester.assertNoErrorMessage();
		tester
			.assertContains("src=\"test/wicket:interface/%3A0%3Aframe%3A%3AIResourceListener%3A%3A/\"");
	}
}
