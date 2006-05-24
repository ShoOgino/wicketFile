/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package wicket.markup.html.form;

import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.model.Model;

/**
 * Tests rendering of the RadioGroup and Radio components
 * 
 * @author igor
 */
public class RadioGroupTestPage2 extends WebPage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor
	 */
	public RadioGroupTestPage2()
	{
		Form form = new Form(this,"form");
		RadioGroup group = new RadioGroup(form,"group", new Model("radio2"));
		WebMarkupContainer container = new WebMarkupContainer(group,"container");
		Radio radio1 = new Radio(group,"radio1", new Model("radio1"));
		Radio radio2 = new Radio(form,"radio2", new Model("radio2"));


		add(form);
		form.add(group);
		group.add(radio1);
		group.add(container);
		// here by mistake we add the radio component not under the group to
		// test the error when it cannot find its group
		form.add(radio2);
	}
}
