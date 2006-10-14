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

import wicket.markup.html.PackageResourceGuard;
import wicket.settings.IResourceSettings;
import wicket.util.file.IResourceFinder;
import wicket.util.time.Duration;


/**
 * MBean interface for exposing application related information and
 * functionality.
 * 
 * @author eelcohillenius
 */
public interface ResourceSettingsMBean
{
	/**
	 * Get the application's localizer.
	 * 
	 * @return The application wide localizer instance
	 */
	String getLocalizer();

	/**
	 * Gets the {@link PackageResourceGuard package resource guard}.
	 * 
	 * @return The package resource guard
	 */
	String getPackageResourceGuard();

	/**
	 * Get the property factory which will be used to load property files
	 * 
	 * @return PropertiesFactory
	 */
	String getPropertiesFactory();

	/**
	 * Gets the resource finder to use when searching for resources.
	 * 
	 * @return Returns the resourceFinder.
	 * @see IResourceSettings#setResourceFinder(IResourceFinder)
	 */
	String getResourceFinder();

	/**
	 * @return Returns the resourcePollFrequency.
	 * @see IResourceSettings#setResourcePollFrequency(Duration)
	 */
	String getResourcePollFrequency();

	/**
	 * @return Resource locator for this application
	 */
	String getResourceStreamLocator();

	/**
	 * @return an unmodifiable list of all available string resource loaders
	 */
	String[] getStringResourceLoaders();

	/**
	 * @see wicket.settings.IExceptionSettings#getThrowExceptionOnMissingResource()
	 * 
	 * @return boolean
	 */
	boolean getThrowExceptionOnMissingResource();

	/**
	 * @return Whether to use a default value (if available) when a missing
	 *         resource is requested
	 */
	boolean getUseDefaultOnMissingResource();

	/**
	 * @see wicket.settings.IExceptionSettings#setThrowExceptionOnMissingResource(boolean)
	 * 
	 * @param throwExceptionOnMissingResource
	 */
	void setThrowExceptionOnMissingResource(final boolean throwExceptionOnMissingResource);

	/**
	 * @param useDefaultOnMissingResource
	 *            Whether to use a default value (if available) when a missing
	 *            resource is requested
	 */
	void setUseDefaultOnMissingResource(final boolean useDefaultOnMissingResource);
}
