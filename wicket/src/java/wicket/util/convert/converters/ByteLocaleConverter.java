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


import java.text.ParseException;

import java.util.Locale;

import wicket.util.convert.ConversionException;

/**
 * localized byte converter.
 */
public class ByteLocaleConverter extends DecimalLocaleConverter
{
    // ----------------------------------------------------------- Constructors

    /**
     * Create a {@link LocaleConverter}that will throw a {@link ConversionException}if a
     * conversion error occurs. The locale is the default locale for this instance of the
     * Java Virtual Machine and an unlocalized pattern is used for the convertion.
     */
    public ByteLocaleConverter()
    {
        this(Locale.getDefault());
    }

    /**
     * Create a {@link LocaleConverter}that will throw a {@link ConversionException}if a
     * conversion error occurs. An unlocalized pattern is used for the convertion.
     * @param locale The locale
     */
    public ByteLocaleConverter(Locale locale)
    {
        this(locale, null);
    }

    /**
     * Create a {@link LocaleConverter}that will throw a {@link ConversionException}if a
     * conversion error occurs. An unlocalized pattern is used for the convertion.
     * @param locale The locale
     * @param pattern The convertion pattern
     */
    public ByteLocaleConverter(Locale locale, String pattern)
    {
        this(locale, pattern, false);
    }

    /**
     * Create a {@link LocaleConverter}that will throw a {@link ConversionException}if a
     * conversion error occurs.
     * @param locale The locale
     * @param pattern The convertion pattern
     * @param locPattern Indicate whether the pattern is localized or not
     */
    public ByteLocaleConverter(Locale locale, String pattern, boolean locPattern)
    {
        super(locale, pattern, locPattern);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type. This method will return values of type Byte.
     * @param value The input object to be converted
     * @param pattern The pattern is used for the convertion
     * @return parsed object
     * @throws ParseException
     * @exception ConversionException if conversion cannot be performed successfully
     */
    protected Object parse(Object value, String pattern) throws ParseException
    {
        final Number parsed = (Number) super.parse(value, pattern);

        if (parsed.longValue() != parsed.byteValue())
        {
            throw new ConversionException("Supplied number is not of type Byte: "
                    + parsed.longValue());
        }

        // now return property Byte
        return new Byte(parsed.byteValue());
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type.
     * @param type Data type to which this value should be converted
     * @param value The input object to be converted
     * @param pattern The pattern is used for the convertion
     * @return converted object
     * @exception ConversionException if conversion cannot be performed successfully
     */
    public Object convert(Class type, Object value, String pattern)
    {
        if (value == null)
        {
            return null;
        }

        Number temp = getNumber(value, pattern);

        return (temp instanceof Byte) ? (Byte) temp : new Byte(temp.byteValue());
    }
}
