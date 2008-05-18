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
package org.apache.wicket.markup.html.border;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Fragment;


/**
 * Mock page for testing.
 */
public class BoxBorderTestPage_9 extends WebPage<Void>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * 
	 */
	public BoxBorderTestPage_9()
	{
		Border myBorder = new BorderComponent1("myBorder");
		add(myBorder);

		Fragment panel1 = new Fragment("fragmentsWillBeRenderedHere", "fragmentSource");
		myBorder.add(panel1);
	}
}
