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
package wicket.protocol.http;

/**
 * A factory interface used by wicket servlet to create application objects.
 * Configure your webapplication to use this factory in web.xml like:
 * <pre>
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;applicationFactoryClassName&lt;/param-name&gt;
 *       &lt;param-value&gt;teachscape.platform.web.wicket.SpringApplicationFactory&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * </pre>
 * 
 * @author Igor Vaynberg ( ivaynberg at privesec dot com )
 */
public interface IWebApplicationFactory
{
	/**
	 * Create application object
	 * 
	 * @param servlet
	 *            the wicket servlet
	 * @return application object instance
	 */
	WebApplication createApplication(WicketServlet servlet);
}
