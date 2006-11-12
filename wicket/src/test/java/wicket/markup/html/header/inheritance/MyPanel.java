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
package wicket.markup.html.header.inheritance;

import wicket.MarkupContainer;
import wicket.markup.html.panel.Panel;

/**
 * Panel for testing wicket:head in markup inheritance strategies.
 * 
 * @author Martijn Dashorst
 */
public class MyPanel extends Panel
{
	/** For serialization. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param id
	 *            component identifier
	 */
	public MyPanel(MarkupContainer parent, String id)
	{
		super(parent, id);
	}
}
