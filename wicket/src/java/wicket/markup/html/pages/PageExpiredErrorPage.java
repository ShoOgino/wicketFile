/*
 * $Id$
 * $Revision$ $Date$
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the Licens *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html.pages;

import wicket.markup.html.WebPage;

/**
 * Page expired error page.
 *
 * @author Jonathan Locke
 */
public class PageExpiredErrorPage extends WebPage
{
	/** Serial Version ID. */
	private static final long serialVersionUID = -4566588225634687534L;

	/**
	 * Constructor.
	 */
	public PageExpiredErrorPage()
	{
		add(homePageLink("homePageLink"));
	}
}