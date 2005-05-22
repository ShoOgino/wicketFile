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
package wicket.examples.wizard;

import wicket.ApplicationSettings;
import wicket.examples.WicketExampleApplication;
import wicket.examples.wizard.example.hotel.HotelPrefWizConfiguration;
import wicket.examples.wizard.framework.WizardConfiguration;

/**
 * Application class for the wizard example.
 *
 * @author Eelco Hillenius
 */
public class WizardApplication extends WicketExampleApplication
{
    /**
     * Constructor.
     */
    public WizardApplication()
    {
        getPages().setHomePage(Home.class);
		ApplicationSettings settings = getSettings();
		settings.setThrowExceptionOnMissingResource(false);
		settings.setStripWicketTags(true);
    }

	/**
	 * Create an instance of the preferences wizard.
	 * @return wizard configuration object
	 */
	public WizardConfiguration newPreferencesWizard()
	{
		return new HotelPrefWizConfiguration();
	}
}
