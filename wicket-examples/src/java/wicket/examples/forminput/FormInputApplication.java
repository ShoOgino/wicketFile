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
package wicket.examples.forminput;

import java.util.Locale;

import wicket.Application;
import wicket.examples.WicketExampleApplication;
import wicket.markup.html.image.resource.DefaultButtonImageResource;

/**
 * Application class for form input example.
 *
 * @author Eelco Hillenius
 */
public class FormInputApplication extends WicketExampleApplication
{
    /**
     * Constructor.
     */
    public FormInputApplication()
    {
        getPages().setHomePage(FormInput.class);
        // insert an alias for the wicket.Application class so that all images don't have the wicket.Application in there url. 
        getPages().putClassAlias(Application.class, "application");
		getSettings().setThrowExceptionOnMissingResource(false);

		getSharedResources().add("save", Locale.SIMPLIFIED_CHINESE,
				new DefaultButtonImageResource("\u4E4B\u5916"));
		getSharedResources().add("reset", Locale.SIMPLIFIED_CHINESE,
				new DefaultButtonImageResource("\u91CD\u65B0\u8BBE\u7F6E"));
    }
}
