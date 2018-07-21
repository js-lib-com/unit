package js.unit.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import js.unit.JsUnitException;

public class Types {
	/**
	 * Test if a requested type is identity equal with one from a given types list. If <code>type</code> is null return false.
	 * If a type to match happened to be null is considered no match.
	 * 
	 * @param t type to search for, possible null,
	 * @param typesToMatch types list to compare with.
	 * @return true if requested type is one from given types list.
	 * @throws IllegalArgumentException if <code>typesToMach</code> is empty.
	 */
	public static boolean equalsAny(Type t, Type... typesToMatch) throws IllegalArgumentException {
		if (t == null) {
			return false;
		}
		for (Type typeToMatch : typesToMatch) {
			if (t.equals(typeToMatch)) {
				return true;
			}
		}
		return false;
	}

	/** Java language primitive values boxing classes. */
	private static Map<Type, Type> BOXING_MAP = new HashMap<Type, Type>();
	static {
		BOXING_MAP.put(boolean.class, Boolean.class);
		BOXING_MAP.put(byte.class, Byte.class);
		BOXING_MAP.put(char.class, Character.class);
		BOXING_MAP.put(short.class, Short.class);
		BOXING_MAP.put(int.class, Integer.class);
		BOXING_MAP.put(long.class, Long.class);
		BOXING_MAP.put(float.class, Float.class);
		BOXING_MAP.put(double.class, Double.class);
	}

	/**
	 * Get boxing class for requested type. If <code>type</code> is primitive returns related boxing class. If <code>type</code>
	 * is already a boxing type returns it as it is. It is considered a bug if <code>type</code> is not a primitive or a boxing
	 * type.
	 * 
	 * @param t primitive or boxing type.
	 * @return boxing class representing requested type.
	 * @throws JsUnitException if <code>type</code> is not a primitive or boxing type.
	 */
	public static Class<?> getBoxingClass(Type t) {
		Type boxingClass = BOXING_MAP.get(t);
		if (boxingClass == null) {
			if (!BOXING_MAP.values().contains(t)) {
				throw new JsUnitException("Trying to get boxing class from not boxed type.");
			}
			boxingClass = t;
		}
		return (Class<?>) boxingClass;
	}

	/**
	 * Determine if a given type is a kind of a requested type to match. Returns true if <code>type</code> is a subclass or
	 * implements <code>typeToMatch</code> - not necessarily direct. Boxing classes for primitive values are compatible. This
	 * depart from {@link Class#isAssignableFrom(Class)} that consider primitive and related boxing class as different.
	 * <p>
	 * If either type or type to match are parameterized types uses the raw class. If either type or type to match are null
	 * returns false.
	 * 
	 * @param t type to test,
	 * @param typeToMatch desired type to match.
	 * @return true if <code>type</code> is subclass of or implements <code>typeToMatch</code>.
	 */
	private static boolean isKindOf(Type t, Type typeToMatch) {
		if (t == null || typeToMatch == null) {
			return false;
		}
		if (t.equals(typeToMatch)) {
			return true;
		}

		Class<?> clazz = typeToClass(t);
		Class<?> classToMatch = typeToClass(typeToMatch);

		if (clazz.isPrimitive()) {
			return BOXING_MAP.get(clazz) == classToMatch;
		}
		if (classToMatch.isPrimitive()) {
			return BOXING_MAP.get(classToMatch) == clazz;
		}

		return classToMatch.isAssignableFrom(clazz);
	}

	/**
	 * Test if object instance is not null and extends or implements expected type. This predicate consider primitive and
	 * related boxing types as equivalent, e.g. <code>1.23</code> is instance of {@link Double}.
	 * 
	 * @param o object instance to test, possible null,
	 * @param t expected type.
	 * @return true if instance is not null and extends or implements requested type.
	 */
	public static boolean isInstanceOf(Object o, Type t) {
		if (o == null) {
			return false;
		}
		if (t instanceof Class) {
			Class<?> clazz = (Class<?>) t;
			if (clazz.isPrimitive()) {
				return BOXING_MAP.get(clazz) == o.getClass();
			}
			return clazz.isInstance(o);
		}
		return false;
	}

	/**
	 * Test if type is a boolean primitive or boxing class.
	 * 
	 * @param t type to test.
	 * @return true if type is boolean.
	 */
	public static boolean isBoolean(Type t) {
		return equalsAny(t, boolean.class, Boolean.class);
	}

	/**
	 * Test if type is a character, primitive or boxing.
	 * 
	 * @param t type to test.
	 * @return true if type is character.
	 */
	public static boolean isCharacter(Type t) {
		return equalsAny(t, char.class, Character.class);
	}

	/**
	 * Test if type is enumeration. This predicate delegates {@link Class#isEnum()} if type is a class. If not, returns false.
	 * 
	 * @param t type to test.
	 * @return true if type is enumeration.
	 */
	public static boolean isEnum(Type t) {
		if (t instanceof Class<?>) {
			return ((Class<?>) t).isEnum();
		}
		return false;
	}

	/** Java standard classes used to represent numbers, including primitives. */
	private static Type[] NUMERICAL_TYPES = new Type[] { int.class, long.class, double.class, Integer.class, Long.class, Double.class, byte.class, short.class, float.class, Byte.class, Short.class, Float.class, BigDecimal.class };

	/**
	 * Test if type is numeric. A type is considered numeric if is a Java standard class representing a number.
	 * 
	 * @param t type to test.
	 * @return true if <code>type</code> is numeric.
	 */
	public static boolean isNumber(Type t) {
		for (int i = 0; i < NUMERICAL_TYPES.length; i++) {
			if (NUMERICAL_TYPES[i] == t) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test if type is a calendar date.
	 * 
	 * @param t type to test.
	 * @return true if type is a calendar date.
	 */
	public static boolean isDate(Type t) {
		return isKindOf(t, Date.class);
	}

	/**
	 * Test if type is like a primitive? Return true only if given type is a number, boolean, enumeration, character or string.
	 * 
	 * @param t type to test.
	 * @return true if this type is like a primitive.
	 */
	public static boolean isPrimitiveLike(Type t) {
		if (isNumber(t)) {
			return true;
		}
		if (isBoolean(t)) {
			return true;
		}
		if (isEnum(t)) {
			return true;
		}
		if (isCharacter(t)) {
			return true;
		}
		if (t == String.class) {
			return true;
		}
		return false;
	}

	/**
	 * Test if type is array. If type is a class return {@link Class#isArray()} predicate value; otherwise test if type is
	 * {@link GenericArrayType}.
	 * 
	 * @param t type to test.
	 * @return true if type is array.
	 */
	public static boolean isArray(Type t) {
		if (t instanceof Class<?>) {
			return ((Class<?>) t).isArray();
		}
		if (t instanceof GenericArrayType) {
			return true;
		}
		return false;
	}

	/**
	 * Test if type is collection. Returns true if type implements, directly or through inheritance, {@link Collection}
	 * interface.
	 * 
	 * @param t type to test.
	 * @return true if type is collection.
	 */
	public static boolean isCollection(Type t) {
		return Types.isKindOf(t, Collection.class);
	}

	/**
	 * Test if type is map. Returns true if type implements, directly or through inheritance, {@link Map} interface.
	 * 
	 * @param t type to test.
	 * @return true if type is map.
	 */
	public static boolean isMap(Type t) {
		return Types.isKindOf(t, Map.class);
	}

	/**
	 * Cast Java reflective type to language class. If <code>type</code> is instance of {@link Class} just return it. If is
	 * parameterized type returns the raw class.
	 * 
	 * @param t Java reflective type.
	 * @return the class described by given <code>type</code>.
	 */
	private static Class<?> typeToClass(Type t) {
		if (t instanceof Class<?>) {
			return (Class<?>) t;
		}
		if (t instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) t).getRawType();
		}
		throw new JsUnitException("Unknown type %s to convert to class.", t);
	}
}
