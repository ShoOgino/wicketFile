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
package org.apache.wicket;

import org.apache.wicket.util.tester.FormTester;

/**
 * @author jcompagner
 */
public class FormDispatchEventTest extends WicketTestCase
{
	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public FormDispatchEventTest(String name)
	{
		super(name);
	}

	/**
	 * @throws Exception
	 */
	public void testDropDownEvent() throws Exception
	{
		tester.startPage(MockPageWithForm.class);


		FormTester formTester = tester.newFormTester("form");
		formTester.select("dropdown", 0);
		formTester.submit();

		MockPageWithForm page = (MockPageWithForm)tester.getLastRenderedPage();

		assertTrue("Form.onSubmit() should have been called", page.isSubmitted());
		assertTrue("DropDownChoice.onSelectionChanged() should have been called", page.isSelected());
	}
}
