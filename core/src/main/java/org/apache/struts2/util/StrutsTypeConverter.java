/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.util;

import java.util.Map;

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Base class for type converters used in Struts. This class provides two abstract methods that are used to convert
 * both to and from strings -- the critical functionality that is core to Struts's type coversion system.
 *
 * <p/> Type converters do not have to use this class. It is merely a helper base class, although it is recommended that
 * you use this class as it provides the common type conversion contract required for all web-based type conversion.
 *
 * <p/> There's a hook (fall back method) called <code>performFallbackConversion</code> of which
 * could be used to perform some fallback conversion if <code>convertValue</code> method of this
 * failed. By default it just ask its super class (Ognl's DefaultTypeConverter) to do the conversion.
 *
 * <p/> To allow the framework to recognize that a conversion error has occurred, throw an XWorkException or
 * preferable a TypeConversionException.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 */
public abstract class StrutsTypeConverter extends DefaultTypeConverter {
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
