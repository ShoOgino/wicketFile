/*
 * $Id$ $Revision:
 * 1.6 $ $Date$
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
package wicket.util.convert.converters;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts from Object to Date.
 * 
 * @author Eelco Hillenius
 */
public final class DateConverter extends AbstractConverter
{
	/** The date format to use for the specific locales (used as the key)*/
	private Map dateFormats = new HashMap();

	/**
	 * @see wicket.util.convert.ITypeConverter#convert(java.lang.Object,java.util.Locale)
	 */
	public Object convert(final Object value, Locale locale)
	{
        return parse(getDateFormat(locale), value);
	}

	/**
	 * @param locale 
	 * @return Returns the date format.
	 */
	public final DateFormat getDateFormat(Locale locale)
	{
		DateFormat dateFormat = (DateFormat)dateFormats.get(locale);
		if (dateFormat == null)
		{
			dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			dateFormats.put(locale, dateFormat);
		}
		return dateFormat;
	}

	/**
	 * @param locale 
	 * @param dateFormat
	 *            The dateFormat to set.
	 */
	public void setDateFormat(final Locale locale, final DateFormat dateFormat)
	{
		this.dateFormats.put(locale,dateFormat);
	}

	/**
	 * @see wicket.util.convert.converters.AbstractConverter#getTargetType()
	 */
	protected Class getTargetType()
	{
		return Date.class;
	}
}