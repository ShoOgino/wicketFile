/*
 * $Id$
 * $Revision$
 * $Date$
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
package wicket.examples.navmenu;

import wicket.extensions.markup.html.navmenu.MenuModel;
import wicket.extensions.markup.html.navmenu.style.tabs.TabsNavigationMenu;
import wicket.markup.html.border.Border;

/**
 * Border component that holds the menu.
 * @author Eelco Hillenius
 */
public class MenuBorder extends Border
{
	/**
	 * Construct.
	 * @param id
	 * @param currentPageClass 
	 */
	public MenuBorder(String id, Class currentPageClass)
	{
		super(id);
		MenuModel model = NavMenuApplication.getMenu();
		add(new TabsNavigationMenu("navmenu", model));
	}
}