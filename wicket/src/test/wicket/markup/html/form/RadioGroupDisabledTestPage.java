/*
 * $Id: RadioGroupTestPage1.java 3034 2005-10-21 07:34:47Z ivaynberg $
 * $Revision: 3034 $ $Date: 2005-10-21 09:34:47 +0200 (vr, 21 okt 2005) $
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
 * @author igor
 */
public class RadioGroupDisabledTestPage extends WebPage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor
	 */
	@SuppressWarnings("unchecked") // It's not the warnings which are important here.
	public RadioGroupDisabledTestPage() {
		Form form=new Form(this, "form");
		RadioGroup group=new RadioGroup(form, "group", new Model("radio2"));
		group.setRenderBodyOnly(false);
		WebMarkupContainer container=new WebMarkupContainer(group, "container");
		Radio radio1=new Radio(group, "radio1", new Model("radio1"));
		Radio radio2=new Radio(container, "radio2", new Model("radio2"));
		
		group.setEnabled(false);
	}
}
