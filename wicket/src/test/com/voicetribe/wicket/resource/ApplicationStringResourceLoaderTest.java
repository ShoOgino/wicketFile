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
package com.voicetribe.wicket.resource;

import java.util.Locale;
import com.voicetribe.wicket.ApplicationSettings;
import com.voicetribe.wicket.IApplication;
import junit.framework.Assert;

/**
 * Tests for the <code>ApplicationStringResourceLoader</code>
 * class.
 *
 * @author Chris Turner
 */
public class ApplicationStringResourceLoaderTest extends StringResourceLoaderTestBase {

    /**
     * Create the test case.
     *
     * @param message The test name
     */
    public ApplicationStringResourceLoaderTest(String message) {
        super(message);
    }

    /**
     * Return the loader instance
     *
     * @return The loader instance to test
     */
    protected IStringResourceLoader createLoader() {
        return new ApplicationStringResourceLoader(application);
    }

    public void testLoaderUnknownResources() {
        IApplication app = new IApplication() {
            public String getName() {
                return "MissingResourceApp";
            }

            public ApplicationSettings getSettings() {
                return settings;
            }

            private ApplicationSettings settings = new ApplicationSettings(this);
        };

        IStringResourceLoader loader = new ApplicationStringResourceLoader(app);
        Assert.assertNull("Unknown resource should return null",
                          loader.get(component, "test.string", Locale.getDefault(), null));
    }

    public void testNullApplication() {
        try {
            new ApplicationStringResourceLoader(null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Extected result
        }
    }

}

///////////////////////////////// End of File /////////////////////////////////
