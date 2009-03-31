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
package org.apache.wicket.util.tester;

import org.apache.wicket.markup.html.WebPage;

/**
 * A dummy <code>Panel</code> Component.
 * 
 * @author Ingram Chen
 * @since 1.2.6
 */
public class DummyPanelPage extends WebPage
{
	private static final long serialVersionUID = 1L;

	/** The dummy <code>Panel</code> <code>Component</code> id */
	public static final String TEST_PANEL_ID = "panel";

	/**
	 * Constructor.
	 * 
	 * @param testPanelSource
	 *            a <code>TestPanelSource</code>
	 */
	public DummyPanelPage(final TestPanelSource testPanelSource)
	{
		add(testPanelSource.getTestPanel(TEST_PANEL_ID));
	}
}
