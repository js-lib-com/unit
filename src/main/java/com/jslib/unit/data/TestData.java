package com.jslib.unit.data;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jslib.unit.JsUnitException;
import com.jslib.unit.util.GType;
import com.jslib.unit.util.Types;

public final class TestData {
	private static Map<Method, Object> methodValueCache = new HashMap<Method, Object>();
	private static Config config = new Config();

	public static void config(Document configXML) throws ClassNotFoundException, SecurityException, NoSuchFieldException {
		config = configXML != null ? new Config(configXML) : new Config();
	}

	public static void register(Class<?> type, Class<? extends RandomValue> randomValue) {
		Context.randomValueGenerators.put(type, randomValue);
	}

	public static void unregister(Class<?> type) {
		Context.randomValueGenerators.remove(type);
	}

	/**
	 * Create a new instance of given type and initialize it with random values. If given type is an interface creates a proxy
	 * whose invocation handler returns random values for all interface getters. This behavior is in contrast with TestData
	 * philosophy that used fields to initialize random values.
	 * 
	 * @param type newly instance type.
	 * @param <T> auto-cast.
	 * @return newly created instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Type type) {
		if (type instanceof Class<?>) {
			final Class<?> clazz = (Class<?>) type;
			if (clazz.isInterface()) {
				return (T) Proxy.newProxyInstance(TestData.class.getClassLoader(), new Class<?>[] { clazz }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (method.getName().equals("toString"))
							return "TestData proxy for " + clazz;
						Object value = methodValueCache.get(method);
						if (value == null) {
							value = TestData.newInstance(method.getGenericReturnType());
							methodValueCache.put(method, value);
						}
						return value;
					}
				});
			}
		}
		Context context = new Context();
		return context.createObject(type);
	}

	@SuppressWarnings("rawtypes")
	public static <T> T newInstance(Class<? extends Collection> collectionType, Class<?> elementType) {
		Context context = new Context();
		return context.createObject(new GType(collectionType, elementType));
	}

	@SuppressWarnings("rawtypes")
	public static <T> T newInstance(Class<? extends Map> mapType, Class<?> keyType, Class<?> valueType) {
		Context context = new Context();
		return context.createObject(new GType(mapType, keyType, valueType));
	}

	public static class Context {
		private static Map<Class<?>, Class<? extends RandomValue>> randomValueGenerators;
		static {
			randomValueGenerators = new HashMap<Class<?>, Class<? extends RandomValue>>();
			randomValueGenerators.put(URL.class, RandomURL.class);
			randomValueGenerators.put(File.class, RandomFile.class);
			randomValueGenerators.put(TimeZone.class, RandomTimeZone.class);
		}

		private int level;

		public <T> T createObject(Type type, int... maxLength) {
			assert maxLength.length < 2;
			T t = null;
			this.level++;
			if (this.level < 8) {
				int length = maxLength.length == 1 ? maxLength[0] : 0;
				t = _createObject(type, length);
			}
			this.level--;
			return t;
		}

		@SuppressWarnings("unchecked")
		public <T> T _createObject(Type type, int maxLength) {
			Type rawType = type;
			if (type instanceof ParameterizedType) {
				rawType = ((ParameterizedType) type).getRawType();
			}

			try {
				if (rawType == String.class) {
					return (T) new RandomString().value(maxLength);
				}
				if (Types.isEnum(rawType)) {
					// enumeration is a primitive like so we need to process is first
					return (T) new RandomEnum(rawType).value(maxLength);
				}
				if (Types.isPrimitiveLike(rawType)) {
					return (T) new RandomPrimitive(rawType).value(maxLength);
				}
				if (Types.isDate(rawType)) {
					return (T) new RandomDate(rawType).value(maxLength);
				}
				if (Types.isArray(rawType)) {
					return (T) new RandomArray(this, rawType).value(maxLength);
				}
				if (Types.isCollection(rawType)) {
					return (T) new RandomCollection(this, type).value(maxLength);
				}
				if (Types.isMap(rawType)) {
					return (T) new RandomMap(this, type).value(maxLength);
				}

				Class<? extends RandomValue> randomValueClass = randomValueGenerators.get(rawType);
				if (randomValueClass != null) {
					return (T) newRandomGenerator(rawType, randomValueClass).value(maxLength);
				}

				if (!(rawType instanceof Class<?>)) {
					throw new JsUnitException("Object field type is not a class.");
				}
				Class<T> clazz = (Class<T>) rawType;
				T t = clazz.newInstance();

				ClassEx classEx = config.getClassEx(clazz);
				for (FieldEx field : classEx) {
					field.set(t, createObject(field.getType(), field.getLength()));
				}
				return t;
			} catch (IllegalArgumentException e) {
				throw e;
			} catch (IllegalStateException e) {
				throw e;
			} catch (Exception e) {
				throw new JsUnitException(e);
			}
		}

		@SuppressWarnings("unchecked")
		private RandomValue newRandomGenerator(Type type, Class<? extends RandomValue> clazz) throws Exception {
			Constructor<? extends RandomValue>[] ctors = (Constructor<? extends RandomValue>[]) clazz.getConstructors();
			if (ctors.length == 0) {
				return clazz.newInstance();
			}
			try {
				return clazz.getConstructor().newInstance();
			} catch (NoSuchMethodException e) {
				return clazz.getConstructor(Type.class).newInstance(type);
			}
		}
	}

	private static class ClassEx implements Iterable<FieldEx> {
		private Class<?> baseClass;
		private List<FieldEx> fields = new ArrayList<FieldEx>();

		public ClassEx(Class<?> baseClass) {
			this.baseClass = baseClass;
			for (Field field : baseClass.getDeclaredFields()) {
				if (field.getName().equals("id"))
					continue;
				int m = field.getModifiers();
				if (Modifier.isFinal(m))
					continue;
				this.fields.add(new FieldEx(field));
			}
		}

		public ClassEx(Element classEl) throws ClassNotFoundException, SecurityException, NoSuchFieldException {
			this.baseClass = Class.forName(classEl.getAttribute("name"));
			NodeList nodeList = classEl.getElementsByTagName("field");
			for (int i = 0; i < nodeList.getLength(); ++i) {
				this.fields.add(new FieldEx(this.baseClass, (Element) nodeList.item(i)));
			}
		}

		public Class<?> getBaseClass() {
			return this.baseClass;
		}

		@Override
		public Iterator<FieldEx> iterator() {
			return this.fields.iterator();
		}
	}

	private static class FieldEx {
		private Field field;
		private int length;

		public FieldEx(Class<?> baseClass, Element fieldEl) throws SecurityException, NoSuchFieldException {
			String fieldName = fieldEl.getAttribute("name");
			this.field = baseClass.getDeclaredField(fieldName);
			this.field.setAccessible(true);
			String lengthAttr = fieldEl.getAttribute("length");
			if (!lengthAttr.isEmpty()) {
				this.length = Integer.parseInt(lengthAttr);
			}
		}

		public FieldEx(Field field) {
			this.field = field;
			field.setAccessible(true);
		}

		public Type getType() {
			return this.field.getGenericType();
		}

		public void set(Object instance, Object value) throws IllegalArgumentException, IllegalAccessException {
			int m = this.field.getModifiers();
			if (Modifier.isStatic(m)) {
				instance = null;
			}
			this.field.set(instance, value);
		}

		public int getLength() {
			return this.length;
		}
	}

	private static class Config {
		private static Map<Class<?>, ClassEx> classes = new HashMap<Class<?>, ClassEx>();

		public Config() {
		}

		public Config(Document configXML) throws ClassNotFoundException, SecurityException, NoSuchFieldException {
			NodeList nodeList = configXML.getElementsByTagName("class");
			for (int i = 0; i < nodeList.getLength(); ++i) {
				ClassEx classEx = new ClassEx((Element) nodeList.item(i));
				classes.put(classEx.getBaseClass(), classEx);
			}
		}

		public ClassEx getClassEx(Class<?> baseClass) {
			ClassEx classEx = classes.get(baseClass);
			if (classEx == null) {
				classEx = new ClassEx(baseClass);
				classes.put(baseClass, classEx);
			}
			return classEx;
		}
	}
}
