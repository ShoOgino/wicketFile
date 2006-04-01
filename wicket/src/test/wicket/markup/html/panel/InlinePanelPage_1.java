/*
 * $Id$ $Revision:
 * 4550 $ $Date$
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
package wicket.markup.html.panel;

import wicket.markup.html.WebPage;


/**
 * 
 * @author Juergen Donnerstag
 */
public class InlinePanelPage_1 extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 */
	public InlinePanelPage_1()
	{
		Fragment panel1 = new Fragment("myPanel1", "frag1");
		add(panel1);

		Fragment panel2 = new Fragment("myPanel2", "frag2");
		add(panel2);
	}
}
