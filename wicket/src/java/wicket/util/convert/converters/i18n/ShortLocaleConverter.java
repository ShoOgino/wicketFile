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
package wicket.util.convert.converters.i18n;

import wicket.util.convert.ConversionException;


/**
 * Converts to and from Short objects using the current locale and optionally
 * a pattern for it conversion.
 *
 * @author Eelco Hillenius
 */
public class ShortLocaleConverter extends DecimalLocaleConverter
{
	/**
	 * Construct.
	 */
	public ShortLocaleConverter()
	{
	}

	/**
	 * Construct. An unlocalized pattern is used for the convertion.
	 * @param pattern The convertion pattern
	 */
	public ShortLocaleConverter(String pattern)
	{
		this(pattern, false);
	}

	/**
	 * Construct.
	 * @param pattern The convertion pattern
	 * @param locPattern Indicate whether the pattern is localized or not
	 */
	public ShortLocaleConverter(String pattern, boolean locPattern)
	{
		super(pattern, locPattern);
	}

	/**
	 * @see wicket.util.convert.IConverter#convert(java.lang.Object, java.lang.Class)
	 */
	public Object convert(Object value, Class c)
	{
		if (value == null)
		{
			return null;
		}
		if(Number.class.isAssignableFrom(c))
		{
			Number temp = getNumber(value);
			return (temp instanceof Short) ? (Short)temp : new Short(temp.shortValue());
		}
		if(String.class.isAssignableFrom(c))
		{
			return toString(value);
		}
		throw new ConversionException(this +
				" cannot handle conversions of type " + c);
	}
}