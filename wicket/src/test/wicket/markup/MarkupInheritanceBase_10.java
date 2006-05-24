/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup;

import wicket.AttributeModifier;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.model.Model;


/**
 */
public class MarkupInheritanceBase_10 extends WebPage 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct.
	 * 
	 */
	public MarkupInheritanceBase_10() 
	{
		WebMarkupContainer css = new WebMarkupContainer(this,"css");
		css.add(new AttributeModifier("src", true, new Model("myStyle.css")));
		add(css);
		
	    add(new Label(this,"label1", "base label 1"));
	    add(new Label(this,"label2", "base label 2"));
    }
}
