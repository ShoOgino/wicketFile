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
package org.apache.wicket.markup.html.baseBorder;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.border.BaseBorder;
import org.apache.wicket.markup.html.form.Form;


/**
 * Test the component: PageView
 * 
 * @author Juergen Donnerstag
 */
public class FormBorder extends BaseBorder
{
	private static final long serialVersionUID = 1L;

	private final Form form;

	/**
	 * 
	 * @param id
	 */
	public FormBorder(final String id)
	{
		super(id);

		form = new Form<Void>("myForm");
		add(form);

		form.add(getBodyContainer());
	}

	/**
	 * 
	 * @param child
	 * @return MarkupContainer
	 */
	public MarkupContainer addToForm(final Component child)
	{
		form.add(child);
		return this;
	}
}