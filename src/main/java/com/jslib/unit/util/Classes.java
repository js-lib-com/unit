package com.jslib.unit.util;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import com.jslib.unit.JsUnitException;

public class Classes
{
  @SuppressWarnings("unchecked")
  public static <T> Class<T> forName(String className)
  {
    if(className == null) {
      throw new NullPointerException("Null class name.");
    }
    try {
      return (Class<T>)Class.forName(className);
    }
    catch(ClassNotFoundException unused) {
      throw new RuntimeException("Class not found: " + className);
    }
  }

  /** Default implementations for collection interfaces. */
  private static Map<Class<?>, Class<?>> COLLECTIONS = new HashMap<Class<?>, Class<?>>();
  static {
    Map<Class<?>, Class<?>> m = new HashMap<Class<?>, Class<?>>();
    m.put(Collection.class, Vector.class);
    m.put(List.class, ArrayList.class);
    m.put(ArrayList.class, ArrayList.class);
    m.put(Vector.class, Vector.class);
    m.put(Set.class, HashSet.class);
    m.put(HashSet.class, HashSet.class);
    m.put(TreeSet.class, TreeSet.class);
    COLLECTIONS = Collections.unmodifiableMap(m);
  }

  /**
   * Create new collection of given type.
   * 
   * @param type collection type.
   * @param <T> auto-cast.
   * @return newly create collection.
   */
  public static <T extends Collection<?>> T newCollection(Type type)
  {
    return newRegisteredInstance(COLLECTIONS, type);
  }

  /**
   * Lookup implementation for requested interface into given registry and return a new instance of it.
   * 
   * @param implementationsRegistry implementations registry,
   * @param interfaceType interface to lookup into registry.
   * @param <T> instance type.
   * @return implementation instance.
   * @throws JsUnitException if implementation is not found into registry.
   */
  @SuppressWarnings("unchecked")
  private static <T> T newRegisteredInstance(Map<Class<?>, Class<?>> implementationsRegistry, Type interfaceType)
  {
    Class<?> implementation = implementationsRegistry.get(interfaceType);
    if(implementation == null) {
      throw new JsUnitException("No registered implementation for type |%s|.", interfaceType);
    }
    try {
      return (T)implementation.newInstance();
    }
    catch(IllegalAccessException e) {
      // illegal access exception is thrown if the class or its no-arguments constructor is not accessible
      // since we use well known JRE classes this condition may never meet
      throw new JsUnitException(e);
    }
    catch(InstantiationException e) {
      // instantiation exception is thrown if class is abstract, interface, array, primitive or void
      // since we use well known JRE classes this condition may never meet
      throw new JsUnitException(e);
    }
  }

  /** Default implementations for maps interfaces. */
  private static Map<Class<?>, Class<?>> MAPS = new HashMap<Class<?>, Class<?>>();
  static {
    Map<Class<?>, Class<?>> m = new HashMap<Class<?>, Class<?>>();
    m.put(Map.class, HashMap.class);
    m.put(HashMap.class, HashMap.class);
    m.put(SortedMap.class, TreeMap.class);
    m.put(TreeMap.class, TreeMap.class);
    m.put(Hashtable.class, Hashtable.class);
    m.put(Properties.class, Properties.class);
    MAPS = Collections.unmodifiableMap(m);
  }

  /**
   * Create new map of given type.
   * 
   * @param type map type.
   * @param <T> map type.
   * @return newly created map.
   * @throws IllegalAccessException if map constructor cannot be invoked.
   * @throws InstantiationException if map instantiation fails.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Map<?, ?>> T newMap(Type type) throws InstantiationException, IllegalAccessException
  {
    Class<?> implementation = MAPS.get(type);
    if(implementation == null) {
      throw new JsUnitException("No registered implementation for map |%s|.", type);
    }
    return (T)implementation.newInstance();
  }

  /**
   * Get instance or class field value. Retrieve named field value from given instance; if <code>object</code> argument
   * is a {@link Class} retrieve class static field.
   * 
   * @param object instance or class to retrieve field value from,
   * @param fieldName field name.
   * @param <T> value type.
   * @return instance or class field value.
   * @throws NullPointerException if object argument is null.
   * @throws JsUnitException if field is missing or if <code>object</code> is a class and field is not static or if
   *           <code>object</code> is an instance and field is static.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getFieldValue(Object object, String fieldName)
  {
    if(object instanceof Class<?>) {
      return getFieldValue(null, (Class<? extends T>)object, fieldName, null, false);
    }

    Class<?> clazz = object.getClass();
    try {
      Field f = clazz.getDeclaredField(fieldName);
      f.setAccessible(true);
      return (T)f.get(Modifier.isStatic(f.getModifiers()) ? null : object);
    }
    catch(Exception e) {
      throw new JsUnitException(e);
    }
  }

  /**
   * Helper method for field value retrieval. Get object field value, declared into specified class which can be object
   * class or superclass. If desired field type is not null retrieved field should have the type; otherwise returns
   * null. If field not found this method behavior depends on <code>optional</code> argument: if true returns null,
   * otherwise throws exception.
   * 
   * @param object instance to retrieve field value from or null if static field,
   * @param clazz class or superclass where field is actually declared,
   * @param fieldName field name,
   * @param fieldType desired field type or null,
   * @param optional if true, return null if field is missing.
   * @param <T> instance type.
   * @return field value or null.
   * @throws JsUnitException if optional flag is false and field is missing or if object is null and field is not static
   *           or if object is not null and field is static.
   */
  @SuppressWarnings("unchecked")
  private static <T> T getFieldValue(Object object, Class<? extends T> clazz, String fieldName, Class<T> fieldType, boolean optional)
  {
    try {
      Field f = clazz.getDeclaredField(fieldName);
      if(fieldType != null && fieldType != f.getType()) {
        return null;
      }
      f.setAccessible(true);
      if(object == null ^ Modifier.isStatic(f.getModifiers())) {
        throw new JsUnitException("Cannot access static field from instance or instance field from null object.");
      }
      return (T)f.get(object);
    }
    catch(Exception e) {
      throw new JsUnitException(e);
    }
  }

  /**
   * Invoke instance or class method with arguments. If this method <code>object</code> argument is a {@link Class}
   * delegate {@link #invoke(Object, Class, String, Object...)} with first argument set to null; otherwise
   * <code>object</code> is passed as first argument and its class the second.
   * 
   * @param object object instance or class,
   * @param methodName method name,
   * @param arguments variable number of arguments.
   * @param <T> auto-cast.
   * @return value returned by method or null.
   * @throws JsUnitException if method is not found.
   * @throws Exception if invocation fail for whatever reason including method internals.
   */
  @SuppressWarnings("unchecked")
  public static <T> T invoke(Object object, String methodName, Object... arguments) throws Exception
  {
    if(object instanceof Class<?>) {
      return invoke(null, (Class<? extends T>)object, methodName, arguments);
    }
    else {
      return (T)invoke(object, object.getClass(), methodName, arguments);
    }
  }

  /**
   * Reflexively executes a method on an object. Locate the method on given class, that is not necessarily object class,
   * e.g. it can be a superclass, and execute it. Given arguments are used for both method discovery and invocation.
   * <p>
   * Implementation note: this method is a convenient way to invoke a method when one knows actual parameters but not
   * strictly formal parameters types. When formal parameters include interfaces or abstract classes or an actual
   * parameter is null there is no way to infer formal parameter type from actual parameter instance. The only option
   * left is to locate method by name and if overloads found uses best effort to determine the right parameter list. For
   * this reason, on limit is possible to invoke the wrong method. Anyway, <b>this method is designed for tests
   * logic</b> and best effort is good enough. The same is true for {@link #invoke(Object, String, Object...)}.
   * 
   * @param object object instance,
   * @param clazz object class one of its superclass,
   * @param methodName method name,
   * @param arguments variable number of arguments.
   * @param <T> instance type.
   * @return value returned by method or null.
   * @throws JsUnitException if method is not found.
   * @throws Exception if invocation fail for whatever reason including method internals.
   */
  @SuppressWarnings("unchecked")
  public static <T> T invoke(Object object, Class<? extends T> clazz, String methodName, Object... arguments) throws Exception
  {
    Class<?>[] parameterTypes = getParameterTypes(arguments);
    try {
      Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
      return (T)invoke(object, method, arguments);
    }
    catch(NoSuchMethodException e) {
      // optimistic attempt to locate the method has failed
      // maybe because method parameters list includes interfaces, primitives or null
      // there is no other option but to search through all object methods

      methodsLoop: for(Method method : clazz.getDeclaredMethods()) {
        Class<?>[] methodParameters = method.getParameterTypes();
        if(!method.getName().equals(methodName)) {
          continue;
        }
        if(methodParameters.length != arguments.length) {
          continue;
        }
        // test if concrete arguments list match method formal parameters; if not continue methods loop
        // null is accepted as any type
        for(int i = 0; i < arguments.length; i++) {
          if(arguments[i] != null && !Types.isInstanceOf(arguments[i], methodParameters[i])) {
            continue methodsLoop;
          }
        }
        return (T)invoke(object, method, arguments);
      }
      throw new JsUnitException("Method %s(%s) not found.", methodName, parameterTypes);
    }
  }

  /**
   * Do the actual reflexive method invocation.
   * 
   * @param object object instance,
   * @param method reflexive method,
   * @param arguments variable number of arguments.
   * @return value returned by method execution.
   * @throws Exception if invocation fail for whatever reason including method internals.
   */
  private static Object invoke(Object object, Method method, Object... arguments) throws Exception
  {
    Throwable cause = null;
    try {
      method.setAccessible(true);
      return method.invoke(object instanceof Class<?> ? null : object, arguments);
    }
    catch(IllegalAccessException e) {
      throw new JsUnitException(e);
    }
    catch(InvocationTargetException e) {
      cause = e.getCause();
      if(cause instanceof Exception) {
        throw (Exception)cause;
      }
      if(cause instanceof AssertionError) {
        throw (AssertionError)cause;
      }
    }
    throw new JsUnitException("Method |%s| invocation fails: %s", method, cause);
  }

  /**
   * Get method formal parameter types inferred from actual invocation arguments. This utility is a helper for method
   * discovery when have access to the actual invocation argument, but not the formal parameter types list declared by
   * method signature.
   * 
   * @param arguments variable number of method arguments.
   * @return parameter types.
   */
  public static Class<?>[] getParameterTypes(Object... arguments)
  {
    Class<?>[] types = new Class<?>[arguments.length];
    for(int i = 0; i < arguments.length; i++) {
      Object argument = arguments[i];
      if(argument == null) {
        types[i] = Object.class;
        continue;
      }
      types[i] = argument.getClass();
      if(types[i].isAnonymousClass()) {
        Class<?>[] interfaces = types[i].getInterfaces();
        Class<?> superclass = interfaces.length > 0 ? interfaces[0] : null;
        if(superclass == null) {
          superclass = types[i].getSuperclass();
        }
        types[i] = superclass;
      }
    }
    return types;
  }
  /**
   * Retrieve resource, identified by qualified name, as input stream. This method does its best to load requested
   * resource but throws exception if fail. Resource is loaded using {@link ClassLoader#getResourceAsStream(String)} and
   * <code>name</code> argument should follow Java class loader convention: it is always considered as absolute path,
   * that is, should contain package but does not start with leading path separator, e.g. <code>js/fop/config.xml</code>
   * .
   * <p>
   * Resource is searched into next class loaders, in given order:
   * <ul>
   * <li>current thread context class loader,
   * <li>this utility class loader,
   * <li>system class loader, as returned by {@link ClassLoader#getSystemClassLoader()}
   * </ul>
   * 
   * @param name resource qualified name, using path separators instead of dots.
   * @return resource input stream.
   */
  public static InputStream getResourceAsStream(String name)
  {
    // not documented behavior: accept but ignore trailing path separator
    if(name.charAt(0) == '/') {
      name = name.substring(1);
    }

    InputStream stream = getResourceAsStream(name, new ClassLoader[]
    {
        Thread.currentThread().getContextClassLoader(), Classes.class.getClassLoader(), ClassLoader.getSystemClassLoader()
    });
    if(stream == null) {
      throw new RuntimeException(String.format("Resource |%s| not found.", name));
    }
    return stream;
  }

  /**
   * Get named resource input stream from a list of class loaders. Traverses class loaders in given order searching for
   * requested resource. Return first resource found or null if none found.
   * 
   * @param name resource name with syntax as required by Java ClassLoader,
   * @param classLoaders target class loaders.
   * @return found resource as input stream or null.
   */
  private static InputStream getResourceAsStream(String name, ClassLoader[] classLoaders)
  {
    // Java standard class loader require resource name to be an absolute path without leading path separator
    // at this point <name> argument is guaranteed to not start with leading path separator

    for(ClassLoader classLoader : classLoaders) {
      InputStream stream = classLoader.getResourceAsStream(name);
      if(stream == null) {
        // it seems there are class loaders that require leading path separator
        // not confirmed rumor but found in similar libraries
        stream = classLoader.getResourceAsStream('/' + name);
      }
      if(stream != null) {
        return stream;
      }
    }
    return null;
  }
}
