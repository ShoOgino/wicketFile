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
package wicket.util.convert.converters;


import java.util.Locale;

import wicket.util.convert.Formatter;

/**
 * Formatter that uses a locale.
 */
public interface LocaleFormatter extends Formatter
{ // TODO finalize javadoc
    /**
     * Set the locale for this instance.
     * @param locale the locale for this instance
     */
    public void setLocale(Locale locale);

    /**
     * Get the locale for this instance
     * @return Locale the locale for this instance
     */
    public Locale getLocale();
}
