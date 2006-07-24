/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar 2006) jannehietamaki $
 * $Revision: 5004 $
 * $Date: 2006-03-17 20:47:08 -0800 (Fri, 17 Mar 2006) $
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
package wicket.protocol.http.portlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.markup.html.pages.AccessDeniedPage;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketServlet;

/**
 * 
 * Dummy utility servlet to support dynamic resources with portlets
 * 
 * @author Janne Hietam&auml;ki
 */
public class WicketPortletServlet extends WicketServlet
{
	private static final Log log = LogFactory.getLog(WicketPortletServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final IWebApplicationFactory getApplicationFactory()
	{
		return new IWebApplicationFactory(){

			public WebApplication createApplication(WicketServlet servlet)
			{
				return new WebApplication(){

					public Class getHomePage()
					{
						return AccessDeniedPage.class;
					}					
				};
			}

		};
	}
}