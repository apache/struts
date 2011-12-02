package com.opensymphony.xwork2.util.reflection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public interface ReflectionProvider {
    
    Method getGetMethod(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException;
    
    Method getSetMethod(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException;
    
    Field getField(Class inClass, String name);
    
    /**
     * Sets the object's properties using the default type converter, defaulting to not throw
     * exceptions for problems setting the properties.
     *
     * @param props   the properties being set
     * @param o       the object
     * @param context the action context
     */
    void setProperties(Map<String, String> props, Object o, Map<String, Object> context);

    /**
     * Sets the object's properties using the default type converter.
     *
     * @param props                   the properties being set
     * @param o                       the object
     * @param context                 the action context
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the properties
     */
    void setProperties(Map<String, String> props, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException;
    
    /**
     * Sets the properties on the object using the default context, defaulting to not throwing
     * exceptions for problems setting the properties.
     *
     * @param properties
     * @param o
     */
    void setProperties(Map<String, String> properties, Object o);
    
    /**
     *  This method returns a PropertyDescriptor for the given class and property name using
     * a Map lookup (using getPropertyDescriptorsMap()).
     */
    PropertyDescriptor getPropertyDescriptor(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException;

    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link com.opensymphony.xwork2.conversion.impl.XWorkConverter} if none
     * is specified.
     *
     * @param from       the source object
     * @param to         the target object
     * @param context    the action context we're running under
     * @param exclusions collection of method names to excluded from copying ( can be null)
     * @param inclusions collection of method names to included copying  (can be null)
     *                   note if exclusions AND inclusions are supplied and not null nothing will get copied.
     */
    void copy(Object from, Object to, Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions);
    
    /**
     * Looks for the real target with the specified property given a root Object which may be a
     * CompoundRoot.
     *
     * @return the real target or null if no object can be found with the specified property
     */
    Object getRealTarget(String property, Map<String, Object> context, Object root) throws ReflectionException;
    
    /**
     * Sets the named property to the supplied value on the Object,
     *
     * @param name    the name of the property to be set
     * @param value   the value to set into the named property
     * @param o       the object upon which to set the property
     * @param context the context which may include the TypeConverter
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the properties
     */
    void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions);

    /**
     * Sets the named property to the supplied value on the Object, defaults to not throwing
     * property exceptions.
     *
     * @param name    the name of the property to be set
     * @param value   the value to set into the named property
     * @param o       the object upon which to set the property
     * @param context the context which may include the TypeConverter
     */
    void setProperty(String name, Object value, Object o, Map<String, Object> context);
    
    /**
     * Creates a Map with read properties for the given source object.
     * <p/>
     * If the source object does not have a read property (i.e. write-only) then
     * the property is added to the map with the value <code>here is no read method for property-name</code>.
     * 
     * @param source   the source object.
     * @return  a Map with (key = read property name, value = value of read property).
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    Map<String, Object> getBeanMap(Object source) throws IntrospectionException, ReflectionException;
    
    /**
     * Evaluates the given OGNL expression to extract a value from the given root
     * object in a given context
     *
     * @param expression the OGNL expression to be parsed
     * @param context the naming context for the evaluation
     * @param root the root object for the OGNL expression
     * @return the result of evaluating the expression
     */
    Object getValue( String expression, Map<String, Object> context, Object root ) throws ReflectionException;
    
    /**
     * Evaluates the given OGNL expression to insert a value into the object graph
     * rooted at the given root object given the context.
     *
     * @param expression the OGNL expression to be parsed
     * @param root the root object for the OGNL expression
     * @param context the naming context for the evaluation
     * @param value the value to insert into the object graph
     */
    void setValue( String expression, Map<String, Object> context, Object root, Object value ) throws ReflectionException;
    
    /**
     * Get's the java beans property descriptors for the given source.
     * 
     * @param source  the source object.
     * @return  property descriptors.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException;
}
