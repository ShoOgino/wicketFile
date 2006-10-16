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
package wicket.util.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Application;
import wicket.Component;
import wicket.WicketRuntimeException;
import wicket.application.IClassResolver;
import wicket.settings.IApplicationSettings;
import wicket.util.collections.MiniMap;
import wicket.util.io.ByteCountingOutputStream;

/**
 * Object utilities.
 * 
 * @author Jonathan Locke
 */
public final class Objects
{
	private static class ArrayInstanceHolder implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private Object array;
		private Object[] values;

	}

	private static class ClassRecordingObjectInputStream extends ObjectInputStream
	{
		private final HashMap<String, Class> usedClasses;

		/**
		 * Construct.
		 * 
		 * @param in
		 * @param classes
		 * @throws IOException
		 */
		public ClassRecordingObjectInputStream(InputStream in, HashMap<String, Class> classes)
				throws IOException
		{
			super(in);
			this.usedClasses = classes;
			enableResolveObject(true);
		}

		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
				ClassNotFoundException
		{
			String className = desc.getName();
			Class clzz = usedClasses.get(className);
			if (clzz != null)
			{
				return clzz;
			}
			else
			{
				log.debug("No Class  found for " + className + " trying through IClassResolver");
			}
			Application application = Application.get();
			IApplicationSettings applicationSettings = application.getApplicationSettings();
			IClassResolver classResolver = applicationSettings.getClassResolver();

			Class candidate = null;
			try
			{
				candidate = classResolver.resolveClass(className);
				if (candidate == null)
				{
					candidate = super.resolveClass(desc);
				}
			}
			catch (WicketRuntimeException ex)
			{
				if (ex.getCause() instanceof ClassNotFoundException)
				{
					throw (ClassNotFoundException)ex.getCause();
				}
			}
			return candidate;
		}
	}

	private static class ClassRecordingObjectOutputStream extends ObjectOutputStream
	{

		private HashMap<String, Class> usedClasses;

		/**
		 * Construct.
		 * 
		 * @param out
		 * @throws IOException
		 */
		public ClassRecordingObjectOutputStream(OutputStream out) throws IOException
		{
			super(out);
			usedClasses = new HashMap<String, Class>();
			enableReplaceObject(true);
		}

		/**
		 * @return The used classes that are recorded.
		 */
		public HashMap<String, Class> getUsedClasses()
		{
			return usedClasses;
		}

		@Override
		protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException
		{
			usedClasses.put(desc.getName(), desc.forClass());
			super.writeClassDescriptor(desc);
		}
	}

	private static class ObjectInstanceHashmapHolder implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private MiniMap<Field, Object> clonemap;
		private Object instance;

	}

	private static final class ReplaceObjectInputStream extends ClassRecordingObjectInputStream
	{
		private HashMap<String, Component> replacedComponents;

		private ReplaceObjectInputStream(InputStream in, HashMap<String, Class> classLoaders,
				HashMap<String, Component> replacedComponents) throws IOException
		{
			super(in, classLoaders);
			this.replacedComponents = replacedComponents;
		}

		@Override
		protected Object resolveObject(Object obj) throws IOException
		{
			Object replaced = replacedComponents.get(obj);
			if (replaced != null)
			{
				return replaced;
			}
			return super.resolveObject(obj);
		}


	}

	private static final class ReplaceObjectOutputStream extends ClassRecordingObjectOutputStream
	{
		private HashMap<String, Component> replacedComponents;

		private ReplaceObjectOutputStream(OutputStream out) throws IOException
		{
			super(out);
			this.replacedComponents = new HashMap<String, Component>();
		}

		/**
		 * @return The replaced components as String->Component
		 */
		public HashMap<String, Component> getReplacedComponents()
		{
			return replacedComponents;
		}

		@Override
		protected Object replaceObject(Object obj) throws IOException
		{
			if (obj instanceof Component)
			{
				Component component = (Component)obj;
				String name = component.getPath();
				replacedComponents.put(name, component);
				return name;
			}
			return super.replaceObject(obj);
		}

	}

	/** defaults for primitives. */
	static HashMap<Class, Comparable> primitiveDefaults = new HashMap<Class, Comparable>();

	/** Type tag meaning java.math.BigDecimal. */
	private static final int BIGDEC = 9;

	/** Type tag meaning java.math.BigInteger. */
	private static final int BIGINT = 6;

	/** Type tag meaning boolean. */
	private static final int BOOL = 0;

	/** Type tag meaning byte. */
	private static final int BYTE = 1;

	/** Type tag meaning char. */
	private static final int CHAR = 2;

	/** Type tag meaning double. */
	private static final int DOUBLE = 8;

	/** Type tag meaning float. */
	private static final int FLOAT = 7;

	/** Type tag meaning int. */
	private static final int INT = 4;

	/** Log for reporting. */
	private static final Log log = LogFactory.getLog(Objects.class);

	/** Type tag meaning long. */
	private static final int LONG = 5;

	/**
	 * The smallest type tag that represents reals as opposed to integers. You
	 * can see whether a type tag represents reals or integers by comparing the
	 * tag to this constant: all tags less than this constant represent
	 * integers, and all tags greater than or equal to this constant represent
	 * reals. Of course, you must also check for NONNUMERIC, which means it is
	 * not a number at all.
	 */
	private static final int MIN_REAL_TYPE = FLOAT;

	/** Type tag meaning something other than a number. */
	private static final int NONNUMERIC = 10;

	private final static String recursive = "_recu_rsive_";

	/** Type tag meaning short. */
	private static final int SHORT = 3;

	static
	{
		primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
		primitiveDefaults.put(Byte.TYPE, new Byte((byte)0));
		primitiveDefaults.put(Short.TYPE, new Short((short)0));
		primitiveDefaults.put(Character.TYPE, new Character((char)0));
		primitiveDefaults.put(Integer.TYPE, new Integer(0));
		primitiveDefaults.put(Long.TYPE, new Long(0L));
		primitiveDefaults.put(Float.TYPE, new Float(0.0f));
		primitiveDefaults.put(Double.TYPE, new Double(0.0));
		primitiveDefaults.put(BigInteger.class, new BigInteger("0"));
		primitiveDefaults.put(BigDecimal.class, new BigDecimal(0.0));
	}

	/**
	 * Evaluates the given object as a BigDecimal.
	 * 
	 * @param value
	 *            an object to interpret as a BigDecimal
	 * @return the BigDecimal value implied by the given object
	 * @throws NumberFormatException
	 *             if the given object can't be understood as a BigDecimal
	 */
	public static BigDecimal bigDecValue(Object value) throws NumberFormatException
	{
		if (value == null)
		{
			return BigDecimal.valueOf(0L);
		}
		Class<? extends Object> c = value.getClass();
		if (c == BigDecimal.class)
		{
			return (BigDecimal)value;
		}
		if (c == BigInteger.class)
		{
			return new BigDecimal((BigInteger)value);
		}
		if (c.getSuperclass() == Number.class)
		{
			return new BigDecimal(((Number)value).doubleValue());
		}
		if (c == Boolean.class)
		{
			return BigDecimal.valueOf(((Boolean)value).booleanValue() ? 1 : 0);
		}
		if (c == Character.class)
		{
			return BigDecimal.valueOf(((Character)value).charValue());
		}
		return new BigDecimal(stringValue(value, true));
	}

	/**
	 * Evaluates the given object as a BigInteger.
	 * 
	 * @param value
	 *            an object to interpret as a BigInteger
	 * @return the BigInteger value implied by the given object
	 * @throws NumberFormatException
	 *             if the given object can't be understood as a BigInteger
	 */
	public static BigInteger bigIntValue(Object value) throws NumberFormatException
	{
		if (value == null)
		{
			return BigInteger.valueOf(0L);
		}
		Class<? extends Object> c = value.getClass();
		if (c == BigInteger.class)
		{
			return (BigInteger)value;
		}
		if (c == BigDecimal.class)
		{
			return ((BigDecimal)value).toBigInteger();
		}
		if (c.getSuperclass() == Number.class)
		{
			return BigInteger.valueOf(((Number)value).longValue());
		}
		if (c == Boolean.class)
		{
			return BigInteger.valueOf(((Boolean)value).booleanValue() ? 1 : 0);
		}
		if (c == Character.class)
		{
			return BigInteger.valueOf(((Character)value).charValue());
		}
		return new BigInteger(stringValue(value, true));
	}

	/**
	 * Evaluates the given object as a boolean: if it is a Boolean object, it's
	 * easy; if it's a Number or a Character, returns true for non-zero objects;
	 * and otherwise returns true for non-null objects.
	 * 
	 * @param value
	 *            an object to interpret as a boolean
	 * @return the boolean value implied by the given object
	 */
	public static boolean booleanValue(Object value)
	{
		if (value == null)
		{
			return false;
		}
		Class<? extends Object> c = value.getClass();
		if (c == Boolean.class)
		{
			return ((Boolean)value).booleanValue();
		}
		if (c == Character.class)
		{
			return ((Character)value).charValue() != 0;
		}
		if (value instanceof Number)
		{
			return ((Number)value).doubleValue() != 0;
		}
		return true; // non-null
	}

	/**
	 * De-serializes an object from a byte array.
	 * 
	 * @param data
	 *            The serialized object
	 * @return The object
	 */
	public static Object byteArrayToObject(final byte[] data)
	{
		try
		{
			final ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream ois = null;
			try
			{
				ois = new ObjectInputStream(in);
				return ois.readObject();
			}
			finally
			{
				if (ois != null)
				{
					ois.close();
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Makes a deep clone of an object by serializing and deserializing it. The
	 * object must be fully serializable to be cloned. This method will not
	 * clone wicket Components, it will just reuse those instances so that the
	 * complete component tree is not copied over only the model data.
	 * 
	 * @param object
	 *            The object to clone
	 * @return A deep copy of the object
	 * @param <T>
	 *            The type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cloneModel(final T object)
	{
		if (object == null)
		{
			return null;
		}
		else
		{


			try
			{
				final ByteArrayOutputStream out = new ByteArrayOutputStream(256);
				ReplaceObjectOutputStream oos = new ReplaceObjectOutputStream(out);
				oos.writeObject(object);
				ObjectInputStream ois = new ReplaceObjectInputStream(new ByteArrayInputStream(out
						.toByteArray()), oos.getUsedClasses(), oos.getReplacedComponents());
				return (T)ois.readObject();
			}
			catch (ClassNotFoundException e)
			{
				throw new WicketRuntimeException("Internal error cloning object", e);
			}
			catch (IOException e)
			{
				throw new WicketRuntimeException("Internal error cloning object", e);
			}
		}
	}

	/**
	 * Makes a deep clone of an object by serializing and deserializing it. The
	 * object must be fully serializable to be cloned.
	 * 
	 * @param object
	 *            The object to clone
	 * @return A deep copy of the object
	 * @param <T>
	 *            The type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cloneObject(final T object)
	{
		if (object == null)
		{
			return null;
		}
		else
		{
			try
			{
				final ByteArrayOutputStream out = new ByteArrayOutputStream(256);
				ClassRecordingObjectOutputStream oos = new ClassRecordingObjectOutputStream(out);
				oos.writeObject(object);
				ClassRecordingObjectInputStream ois = new ClassRecordingObjectInputStream(
						new ByteArrayInputStream(out.toByteArray()), oos.getUsedClasses());
				return (T)ois.readObject();
			}
			catch (ClassNotFoundException e)
			{
				throw new WicketRuntimeException("Internal error cloning object", e);
			}
			catch (IOException e)
			{
				throw new WicketRuntimeException("Internal error cloning object", e);
			}
		}
	}

	/**
	 * Compares two objects for equality, even if it has to convert one of them
	 * to the other type. If both objects are numeric they are converted to the
	 * widest type and compared. If one is non-numeric and one is numeric the
	 * non-numeric is converted to double and compared to the double numeric
	 * value. If both are non-numeric and Comparable and the types are
	 * compatible (i.e. v1 is of the same or superclass of v2's type) they are
	 * compared with Comparable.compareTo(). If both values are non-numeric and
	 * not Comparable or of incompatible classes this will throw and
	 * IllegalArgumentException.
	 * 
	 * @param v1
	 *            First value to compare
	 * @param v2
	 *            second value to compare
	 * 
	 * @return integer describing the comparison between the two objects. A
	 *         negative number indicates that v1 < v2. Positive indicates that
	 *         v1 > v2. Zero indicates v1 == v2.
	 * 
	 * @throws IllegalArgumentException
	 *             if the objects are both non-numeric yet of incompatible types
	 *             or do not implement Comparable.
	 */
	@SuppressWarnings("unchecked")
	public static int compareWithConversion(Object v1, Object v2)
	{
		int result;

		if (v1 == v2)
		{
			result = 0;
		}
		else
		{
			int t1 = getNumericType(v1), t2 = getNumericType(v2), type = getNumericType(t1, t2,
					true);

			switch (type)
			{
				case BIGINT :
					result = bigIntValue(v1).compareTo(bigIntValue(v2));
					break;

				case BIGDEC :
					result = bigDecValue(v1).compareTo(bigDecValue(v2));
					break;

				case NONNUMERIC :
					if ((t1 == NONNUMERIC) && (t2 == NONNUMERIC))
					{
						if ((v1 instanceof Comparable)
								&& v1.getClass().isAssignableFrom(v2.getClass()))
						{
							result = ((Comparable)v1).compareTo(v2);
							break;
						}
						else
						{
							throw new IllegalArgumentException("invalid comparison: "
									+ v1.getClass().getName() + " and " + v2.getClass().getName());
						}
					}
					// else fall through
				case FLOAT :
				case DOUBLE :
					double dv1 = doubleValue(v1),
					dv2 = doubleValue(v2);

					return (dv1 == dv2) ? 0 : ((dv1 < dv2) ? -1 : 1);

				default :
					long lv1 = longValue(v1),
					lv2 = longValue(v2);

					return (lv1 == lv2) ? 0 : ((lv1 < lv2) ? -1 : 1);
			}
		}
		return result;
	}

	/**
	 * Returns the value converted numerically to the given class type
	 * 
	 * This method also detects when arrays are being converted and converts the
	 * components of one array to the type of the other.
	 * 
	 * @param value
	 *            an object to be converted to the given type
	 * @param toType
	 *            class type to be converted to
	 * @return converted value of the type given, or value if the value cannot
	 *         be converted to the given type.
	 */
	public static Object convertValue(Object value, Class toType)
	{
		Object result = null;

		if (value != null)
		{
			/* If array -> array then convert components of array individually */
			if (value.getClass().isArray() && toType.isArray())
			{
				Class componentType = toType.getComponentType();

				result = Array.newInstance(componentType, Array.getLength(value));
				for (int i = 0, icount = Array.getLength(value); i < icount; i++)
				{
					Array.set(result, i, convertValue(Array.get(value, i), componentType));
				}
			}
			else
			{
				if ((toType == Integer.class) || (toType == Integer.TYPE))
				{
					result = new Integer((int)longValue(value));
				}
				if ((toType == Double.class) || (toType == Double.TYPE))
				{
					result = new Double(doubleValue(value));
				}
				if ((toType == Boolean.class) || (toType == Boolean.TYPE))
				{
					result = booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
				}
				if ((toType == Byte.class) || (toType == Byte.TYPE))
				{
					result = new Byte((byte)longValue(value));
				}
				if ((toType == Character.class) || (toType == Character.TYPE))
				{
					result = new Character((char)longValue(value));
				}
				if ((toType == Short.class) || (toType == Short.TYPE))
				{
					result = new Short((short)longValue(value));
				}
				if ((toType == Long.class) || (toType == Long.TYPE))
				{
					result = new Long(longValue(value));
				}
				if ((toType == Float.class) || (toType == Float.TYPE))
				{
					result = new Float(doubleValue(value));
				}
				if (toType == BigInteger.class)
				{
					result = bigIntValue(value);
				}
				if (toType == BigDecimal.class)
				{
					result = bigDecValue(value);
				}
				if (toType == String.class)
				{
					result = stringValue(value);
				}
			}
		}
		else
		{
			if (toType.isPrimitive())
			{
				result = primitiveDefaults.get(toType);
			}
		}
		return result;
	}

	/**
	 * Evaluates the given object as a double-precision floating-point number.
	 * 
	 * @param value
	 *            an object to interpret as a double
	 * @return the double value implied by the given object
	 * @throws NumberFormatException
	 *             if the given object can't be understood as a double
	 */
	public static double doubleValue(Object value) throws NumberFormatException
	{
		if (value == null)
		{
			return 0.0;
		}
		Class<? extends Object> c = value.getClass();
		if (c.getSuperclass() == Number.class)
		{
			return ((Number)value).doubleValue();
		}
		if (c == Boolean.class)
		{
			return ((Boolean)value).booleanValue() ? 1 : 0;
		}
		if (c == Character.class)
		{
			return ((Character)value).charValue();
		}
		String s = stringValue(value, true);

		return (s.length() == 0) ? 0.0 : Double.parseDouble(s);
	}

	/**
	 * Returns true if a and b are equal. Either object may be null.
	 * 
	 * @param a
	 *            Object a
	 * @param b
	 *            Object b
	 * @return True if the objects are equal
	 */
	public static boolean equal(final Object a, final Object b)
	{
		if (a == b)
		{
			return true;
		}

		if ((a != null) && (b != null) && a.equals(b))
		{
			return true;
		}

		return false;
	}


	/**
	 * Returns the constant from the NumericTypes interface that best expresses
	 * the type of an operation, which can be either numeric or not, on the two
	 * given types.
	 * 
	 * @param t1
	 *            type of one argument to an operator
	 * @param t2
	 *            type of the other argument
	 * @param canBeNonNumeric
	 *            whether the operator can be interpreted as non-numeric
	 * @return the appropriate constant from the NumericTypes interface
	 */
	public static int getNumericType(int t1, int t2, boolean canBeNonNumeric)
	{
		if (t1 == t2)
		{
			return t1;
		}

		if (canBeNonNumeric && (t1 == NONNUMERIC || t2 == NONNUMERIC || t1 == CHAR || t2 == CHAR))
		{
			return NONNUMERIC;
		}

		if (t1 == NONNUMERIC)
		{
			t1 = DOUBLE; // Try to interpret strings as doubles...
		}
		if (t2 == NONNUMERIC)
		{
			t2 = DOUBLE; // Try to interpret strings as doubles...
		}

		if (t1 >= MIN_REAL_TYPE)
		{
			if (t2 >= MIN_REAL_TYPE)
			{
				return Math.max(t1, t2);
			}
			if (t2 < INT)
			{
				return t1;
			}
			if (t2 == BIGINT)
			{
				return BIGDEC;
			}
			return Math.max(DOUBLE, t1);
		}
		else if (t2 >= MIN_REAL_TYPE)
		{
			if (t1 < INT)
			{
				return t2;
			}
			if (t1 == BIGINT)
			{
				return BIGDEC;
			}
			return Math.max(DOUBLE, t2);
		}
		else
		{
			return Math.max(t1, t2);
		}
	}

	/**
	 * Returns a constant from the NumericTypes interface that represents the
	 * numeric type of the given object.
	 * 
	 * @param value
	 *            an object that needs to be interpreted as a number
	 * @return the appropriate constant from the NumericTypes interface
	 */
	public static int getNumericType(Object value)
	{
		if (value != null)
		{
			Class<? extends Object> c = value.getClass();
			if (c == Integer.class)
			{
				return INT;
			}
			if (c == Double.class)
			{
				return DOUBLE;
			}
			if (c == Boolean.class)
			{
				return BOOL;
			}
			if (c == Byte.class)
			{
				return BYTE;
			}
			if (c == Character.class)
			{
				return CHAR;
			}
			if (c == Short.class)
			{
				return SHORT;
			}
			if (c == Long.class)
			{
				return LONG;
			}
			if (c == Float.class)
			{
				return FLOAT;
			}
			if (c == BigInteger.class)
			{
				return BIGINT;
			}
			if (c == BigDecimal.class)
			{
				return BIGDEC;
			}
		}
		return NONNUMERIC;
	}

	/**
	 * Returns the constant from the NumericTypes interface that best expresses
	 * the type of a numeric operation on the two given objects.
	 * 
	 * @param v1
	 *            one argument to a numeric operator
	 * @param v2
	 *            the other argument
	 * @return the appropriate constant from the NumericTypes interface
	 */
	public static int getNumericType(Object v1, Object v2)
	{
		return getNumericType(v1, v2, false);
	}

	/**
	 * Returns the constant from the NumericTypes interface that best expresses
	 * the type of an operation, which can be either numeric or not, on the two
	 * given objects.
	 * 
	 * @param v1
	 *            one argument to an operator
	 * @param v2
	 *            the other argument
	 * @param canBeNonNumeric
	 *            whether the operator can be interpreted as non-numeric
	 * @return the appropriate constant from the NumericTypes interface
	 */
	public static int getNumericType(Object v1, Object v2, boolean canBeNonNumeric)
	{
		return getNumericType(getNumericType(v1), getNumericType(v2), canBeNonNumeric);
	}

	/**
	 * returns hashcode of the object by calling obj.hashcode(). safe to use
	 * when obj is null.
	 * 
	 * @param obj
	 * @return hashcode of the object or 0 if obj is null
	 */
	public static int hashCode(final Object... obj)
	{
		int result = 37;
		for (Object o : obj)
		{
			result = 37 * result + (o != null ? o.hashCode() : 0);
		}
		return result;
	}

	/**
	 * Returns true if object1 is equal to object2 in either the sense that they
	 * are the same object or, if both are non-null if they are equal in the
	 * <CODE>equals()</CODE> sense.
	 * 
	 * @param object1
	 *            First object to compare
	 * @param object2
	 *            Second object to compare
	 * 
	 * @return true if v1 == v2
	 */
	public static boolean isEqual(Object object1, Object object2)
	{
		boolean result = false;

		if (object1 == object2)
		{
			result = true;
		}
		else
		{
			if ((object1 != null) && object1.getClass().isArray())
			{
				if ((object2 != null) && object2.getClass().isArray()
						&& (object2.getClass() == object1.getClass()))
				{
					result = (Array.getLength(object1) == Array.getLength(object2));
					if (result)
					{
						for (int i = 0, icount = Array.getLength(object1); result && (i < icount); i++)
						{
							result = isEqual(Array.get(object1, i), Array.get(object2, i));
						}
					}
				}
			}
			else
			{
				// Check for converted equivalence first, then equals()
				// equivalence
				result = (object1 != null)
						&& (object2 != null)
						&& ((compareWithConversion(object1, object2) == 0) || object1
								.equals(object2));
			}
		}
		return result;
	}

	/**
	 * Evaluates the given object as a long integer.
	 * 
	 * @param value
	 *            an object to interpret as a long integer
	 * @return the long integer value implied by the given object
	 * @throws NumberFormatException
	 *             if the given object can't be understood as a long integer
	 */
	public static long longValue(Object value) throws NumberFormatException
	{
		if (value == null)
		{
			return 0L;
		}
		Class<? extends Object> c = value.getClass();
		if (c.getSuperclass() == Number.class)
		{
			return ((Number)value).longValue();
		}
		if (c == Boolean.class)
		{
			return ((Boolean)value).booleanValue() ? 1 : 0;
		}
		if (c == Character.class)
		{
			return ((Character)value).charValue();
		}
		return Long.parseLong(stringValue(value, true));
	}

	/**
	 * @param object
	 * @return The hashmap with the field->value objects
	 */
	public static MiniMap<Field, Object> mapObject(final Object object)
	{
		return mapObject(object, new ArrayList<Object>());
	}

	/**
	 * Returns a new Number object of an appropriate type to hold the given
	 * integer value. The type of the returned object is consistent with the
	 * given type argument, which is a constant from the NumericTypes interface.
	 * 
	 * @param type
	 *            the nominal numeric type of the result, a constant from the
	 *            NumericTypes interface
	 * @param value
	 *            the integer value to convert to a Number object
	 * @return a Number object with the given value, of type implied by the type
	 *         argument
	 */
	public static Number newInteger(int type, long value)
	{
		switch (type)
		{
			case BOOL :
			case CHAR :
			case INT :
				return new Integer((int)value);

			case FLOAT :
				if (value == value)
				{
					return new Float(value);
				}
				// else fall through:
			case DOUBLE :
				if (value == value)
				{
					return new Double(value);
				}
				// else fall through:
			case LONG :
				return new Long(value);

			case BYTE :
				return new Byte((byte)value);

			case SHORT :
				return new Short((short)value);

			default :
				return BigInteger.valueOf(value);
		}
	}

	/**
	 * Serializes an object into a byte array.
	 * 
	 * @param object
	 *            The object
	 * @return The serialized object
	 */
	public static byte[] objectToByteArray(final Object object)
	{
		try
		{
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			try
			{
				oos = new ObjectOutputStream(out);
				oos.writeObject(object);
			}
			finally
			{
				if (oos != null)
				{
					oos.close();
				}
			}
			return out.toByteArray();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param object
	 * @param clonemap
	 */
	public static void restoreObject(Object object, MiniMap<Field, Object> clonemap)
	{
		for (Map.Entry<Field, Object> entry : clonemap.entrySet())
		{
			Field field = entry.getKey();
			Object value = entry.getValue();
			value = checkAndConvertValue(value);
			try
			{
				if (!Modifier.isFinal(field.getModifiers()))
				{
					field.set(object, value);
				}
			}
			catch (Exception e)
			{
				throw new WicketRuntimeException(
						"Internal error cloning/restoring from map the object: " + object, e);
			}
		}
	}

	/**
	 * Computes the size of an object by serializing it to a byte array.
	 * 
	 * @param object
	 *            Object to compute size of
	 * @return The size of the object in bytes
	 */
	public static long sizeof(final Object object)
	{
		try
		{
			final ByteCountingOutputStream out = new ByteCountingOutputStream();
			new ObjectOutputStream(out).writeObject(object);
			out.close();
			return out.size();
		}
		catch (IOException e)
		{
			return -1;
		}
	}

	/**
	 * Evaluates the given object as a String.
	 * 
	 * @param value
	 *            an object to interpret as a String
	 * @return the String value implied by the given object as returned by the
	 *         toString() method, or "null" if the object is null.
	 */
	public static String stringValue(Object value)
	{
		return stringValue(value, false);
	}

	/**
	 * Evaluates the given object as a String and trims it if the trim flag is
	 * true.
	 * 
	 * @param value
	 *            an object to interpret as a String
	 * @param trim
	 *            whether to trim the string
	 * @return the String value implied by the given object as returned by the
	 *         toString() method, or "null" if the object is null.
	 */
	public static String stringValue(Object value, boolean trim)
	{
		String result;

		if (value == null)
		{
			result = "null";
		}
		else
		{
			result = value.toString();
			if (trim)
			{
				result = result.trim();
			}
		}
		return result;
	}

	private static Object analyzeObject(Object value, List<Object> addedObjects)
	{
		if (addedObjects.contains(value))
		{
			return recursive;
		}
		if (value == null || value instanceof String || value instanceof Number
				|| value instanceof Boolean || value instanceof Class)
		{
			return value;
		}
		else if (value != null && value.getClass().isArray())
		{
			if (!value.getClass().getComponentType().isPrimitive())
			{
				int length = Array.getLength(value);
				Object[] arrayValues = new Object[length];
				while (length-- != 0)
				{
					Object arrayValue = Array.get(value, length);
					arrayValues[length] = analyzeObject(arrayValue, addedObjects);
				}
				ArrayInstanceHolder holder = new ArrayInstanceHolder();
				holder.array = value;
				holder.values = arrayValues;
				return holder;
			}
			else
			{
				// just clone the primitive arrays
				return cloneObject(value);
			}
		}
		// all other values go through map again
		ObjectInstanceHashmapHolder holder = new ObjectInstanceHashmapHolder();
		holder.instance = value;
		holder.clonemap = mapObject(value, addedObjects);
		return holder;
	}

	private static Object checkAndConvertValue(Object value)
	{
		if (value instanceof ObjectInstanceHashmapHolder)
		{
			ObjectInstanceHashmapHolder holder = (ObjectInstanceHashmapHolder)value;
			restoreObject(holder.instance, holder.clonemap);
			value = holder.instance;
		}
		else if (value instanceof ArrayInstanceHolder)
		{
			ArrayInstanceHolder holder = (ArrayInstanceHolder)value;
			restoreArray(holder.array, holder.values);
			value = holder.array;
		}
		return value;
	}

	private static List<Field> getFields(Class clz)
	{
		ArrayList<Field> fieldList = new ArrayList<Field>();
		Field[] fields = clz.getDeclaredFields();
		for (Field field : fields)
		{
			if (!Modifier.isStatic(field.getModifiers())
					&& !Modifier.isTransient(field.getModifiers()))
			{
				fieldList.add(field);
			}
		}
		Class superClz = clz.getSuperclass();
		if (superClz != Object.class)
		{
			fieldList.addAll(getFields(superClz));
		}
		return fieldList;
	}

	private static MiniMap<Field, Object> mapObject(final Object object, List<Object> addedObjects)
	{
		addedObjects.add(object);
		try
		{
			List<Field> fields = getFields(object.getClass());
			MiniMap<Field, Object> values = new MiniMap<Field, Object>(fields.size());
			for (Field field : fields)
			{
				field.setAccessible(true);
				Object value = field.get(object);
				Object analyzed = analyzeObject(value, addedObjects);
				if (recursive == analyzed)
					continue;
				if (!Modifier.isFinal(field.getModifiers())
						|| analyzed instanceof ArrayInstanceHolder
						|| analyzed instanceof ObjectInstanceHashmapHolder)
				{
					values.put(field, analyzed);
				}
			}
			return values;
		}
		catch (Exception e)
		{
			throw new WicketRuntimeException("Internal error cloning model: " + object, e);
		}
	}

	private static void restoreArray(Object array, Object[] values)
	{
		for (int i = 0; i < values.length; i++)
		{
			Object value = values[i];
			value = checkAndConvertValue(value);
			Array.set(array, i, value);
		}
	}

	/**
	 * Instantiation not allowed
	 */
	private Objects()
	{
	}

}
