/*
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
package wicket.jmx;

/**
 * Exposes Application related functionality for JMX.
 * 
 * @author eelcohillenius
 */
public class SecuritySettings implements SecuritySettingsMBean
{
	private final wicket.Application application;

	/**
	 * Create.
	 * 
	 * @param application
	 */
	public SecuritySettings(wicket.Application application)
	{
		this.application = application;
	}

	/**
	 * @see wicket.jmx.SecuritySettingsMBean#getAuthorizationStrategy()
	 */
	public String getAuthorizationStrategy()
	{
		return Stringz.className(application.getSecuritySettings().getAuthorizationStrategy());
	}

	/**
	 * @see wicket.jmx.SecuritySettingsMBean#getCryptFactory()
	 */
	public String getCryptFactory()
	{
		return Stringz.className(application.getSecuritySettings().getCryptFactory());
	}

	/**
	 * @see wicket.jmx.SecuritySettingsMBean#getUnauthorizedComponentInstantiationListener()
	 */
	public String getUnauthorizedComponentInstantiationListener()
	{
		return Stringz.className(application.getSecuritySettings()
				.getUnauthorizedComponentInstantiationListener());
	}
}
