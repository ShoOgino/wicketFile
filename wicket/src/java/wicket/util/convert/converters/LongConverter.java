/*
 * $Id$
 * $Revision$ $Date$
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

import java.util.Locale;

import wicket.util.convert.ITypeConverter;

/**
 * Converts from Object to Long.
 * 
 * @author Eelco Hillenius
 * @author Jonathan Locke
 */
public final class LongConverter extends AbstractIntegerConverter
{
	private static final long serialVersionUID = 1L;

	/**
	 * The singleton instance for a long converter
	 */
	public static final ITypeConverter INSTANCE = new LongConverter();
	
	/**
	 * @see wicket.util.convert.ITypeConverter#convert(java.lang.Object,java.util.Locale)
	 */
	public Object convert(final Object value, Locale locale)
	{
		final Number number = value instanceof Number ? (Number)value : parse(value,
				Long.MIN_VALUE, Long.MAX_VALUE,locale);

        if (number == null)
        {
        	return null;
        }

		return new Long(number.longValue());
	}

	/**
	 * @see wicket.util.convert.converters.AbstractConverter#getTargetType()
	 */
	protected Class getTargetType()
	{
		return Long.class;
	}
}
