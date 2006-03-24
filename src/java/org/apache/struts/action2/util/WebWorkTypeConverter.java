/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.util;

import ognl.DefaultTypeConverter;

import java.util.Map;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Base class for type converters used in WebWork. This class provides two abstract methods that are used to convert
 * both to and from strings -- the critical functionality that is core to WebWork's type coversion system.
 *
 * <p/> Type converters do not have to use this class. It is merely a helper base class, although it is recommended that
 * you use this class as it provides the common type conversion contract required for all web-based type conversion.
 *
 * <p/> There's a hook (fall back method) called <code>performFallbackConversion</code> of which 
 * could be used to perform some fallback conversion if <code>convertValue</code> method of this 
 * failed. By default it just ask its super class (Ognl's DefaultTypeConverter) to do the conversion.
 *
 * <!-- END SNIPPET: javadoc -->
 * 
 * @version $Date: 2006/03/18 15:49:04 $ $Id: WebWorkTypeConverter.java,v 1.5 2006/03/18 15:49:04 rainerh Exp $
 * 
 */
public abstract class WebWorkTypeConverter extends DefaultTypeConverter {
    public Object convertValue(Map context, Object o, Class toClass) {
        if (toClass.equals(String.class)) {
            return convertToString(context, o);
        } else if (o instanceof String[]) {
            return convertFromString(context, (String[]) o, toClass);
        } else if (o instanceof String) {
            return convertFromString(context, new String[]{(String) o}, toClass);
        } else {
        	return performFallbackConversion(context, o, toClass);
        }
    }
    
    /**
     * Hook to perform a fallback conversion if every default options failed. By default
     * this will ask Ognl's DefaultTypeConverter (of which this class extends) to 
     * perform the conversion.
     * 
     * @param context
     * @param o
     * @param toClass
     * @return The fallback conversion
     */
    protected Object performFallbackConversion(Map context, Object o, Class toClass) {
    	return super.convertValue(context, o, toClass);
    }
    

    /**
     * Converts one or more String values to the specified class.
     *
     * @param context the action context
     * @param values  the String values to be converted, such as those submitted from an HTML form
     * @param toClass the class to convert to
     * @return the converted object
     */
    public abstract Object convertFromString(Map context, String[] values, Class toClass);

    /**
     * Converts the specified object to a String.
     *
     * @param context the action context
     * @param o       the object to be converted
     * @return the converted String
     */
    public abstract String convertToString(Map context, Object o);
}
