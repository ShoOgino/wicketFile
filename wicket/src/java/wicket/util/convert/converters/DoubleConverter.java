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

import wicket.util.convert.ConversionException;
import wicket.util.convert.IConverter;

/**
 * {@link wicket.util.convert.IConverter} implementation that converts an incoming String
 * into a <code>java.lang.Double</code> object, throwing a
 * {@link wicket.util.convert.ConversionException} if a conversion error occurs.
 * </p>
 */
public final class DoubleConverter implements IConverter
{
    /**
     * Construct.
     */
    public DoubleConverter()
    {
    }

    /**
     * Converts the specified input object into an output object of the specified type.
     * @param value The input value to be converted
     * @return converted object
     * @exception ConversionException if conversion cannot be performed successfully
     */
    public Object convert(Object value)
    {
        if (value == null)
        {
            return null;
        }
        else if (value instanceof Double)
        {
            return (value);
        }
        else if (value instanceof Number)
        {
            return new Double(((Number) value).doubleValue());
        }
        else
        {
            try
            {
                return (new Double(value.toString()));
            }
            catch (Exception e)
            {
                throw new ConversionException(e);
            }
        }
    }
}
