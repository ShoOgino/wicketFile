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
package wicket.util.tester;

import java.util.ArrayList;
import java.util.List;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.form.AjaxSubmitLink;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Check;
import wicket.markup.html.form.CheckGroup;
import wicket.markup.html.form.Form;
import wicket.model.Model;
import wicket.model.PropertyModel;

/**
 * Mock page with form and checkgroup.
 * 
 * @author Frank Bille (billen)
 */
public class MockPageWithFormAndCheckGroup extends WebPage
{
	private static final long serialVersionUID = 1L;

	private List selected = new ArrayList();

	/**
	 * Construct.
	 */
	public MockPageWithFormAndCheckGroup()
	{
		Form form = new Form(this, "form");

		CheckGroup checkGroup = new CheckGroup(form, "checkGroup", new PropertyModel(this, "selected"));

		new Check(checkGroup, "check1", new Model(new Integer(1)));
		new Check(checkGroup, "check2", new Model(new Integer(2)));

		new AjaxSubmitLink(this, "submitLink", form)
		{
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form form)
			{
			}
		};
	}

	/**
	 * @return selected
	 */
	public List getSelected()
	{
		return selected;
	}

	/**
	 * @param selected
	 */
	public void setSelected(List selected)
	{
		this.selected = selected;
	}
}
