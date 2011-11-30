/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensymphony.xwork2.util.reflection;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages variables in the reflection context and returns values
 * to be used by the application.
 *
 * @author Gabe
 */
public class ReflectionContextState {

    public static final String CURRENT_PROPERTY_PATH="current.property.path";
    public static final String FULL_PROPERTY_PATH="current.property.path";
    private static final String GETTING_BY_KEY_PROPERTY="xwork.getting.by.key.property";

    private static final String SET_MAP_KEY="set.map.key";

    public static boolean isCreatingNullObjects(Map<String, Object> context) {
		//TODO
		return getBooleanProperty(ReflectionContextState.CREATE_NULL_OBJECTS, context);
	}

	public static void setCreatingNullObjects(Map<String, Object> context, boolean creatingNullObjects) {
		setBooleanValue(ReflectionContextState.CREATE_NULL_OBJECTS, context, creatingNullObjects);
	}

	public static boolean isGettingByKeyProperty(Map<String, Object> context) {
		return getBooleanProperty(GETTING_BY_KEY_PROPERTY, context);
	}
	
	public static void setDenyMethodExecution(Map<String, Object> context, boolean denyMethodExecution) {
		setBooleanValue(ReflectionContextState.DENY_METHOD_EXECUTION, context, denyMethodExecution);
	}
	
	public static boolean isDenyMethodExecution(Map<String, Object> context) {
		return getBooleanProperty(ReflectionContextState.DENY_METHOD_EXECUTION, context);
	}

	public static void setGettingByKeyProperty(Map<String, Object> context, boolean gettingByKeyProperty) {
		setBooleanValue(GETTING_BY_KEY_PROPERTY, context, gettingByKeyProperty);
	}	
	
	public static boolean isReportingConversionErrors(Map<String, Object> context) {
		return getBooleanProperty(XWorkConverter.REPORT_CONVERSION_ERRORS, context);
	}

	public static void setReportingConversionErrors(Map<String, Object> context, boolean reportingErrors) {
		setBooleanValue(XWorkConverter.REPORT_CONVERSION_ERRORS, context, reportingErrors);
	}

	public static Class getLastBeanClassAccessed(Map<String, Object> context) {
		return (Class)context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
	}

	public static void setLastBeanPropertyAccessed(Map<String, Object> context, String property) {
		context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, property);
	}

	public static String getLastBeanPropertyAccessed(Map<String, Object> context) {
		return (String)context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
	}

	public static void setLastBeanClassAccessed(Map<String, Object> context, Class clazz) {
		context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, clazz);
	}
	/**
	 * Gets the current property path but not completely.
	 * It does not use the [ and ] used in some representations
	 * of Maps and Lists. The reason for this is that the current
	 * property path is only currently used for caching purposes
	 * so there is no real reason to have an exact replica.
     *
	 * <p/>So if the real path is myProp.myMap['myKey'] this would
	 * return myProp.myMap.myKey.
     * 
	 * @param context
	 */
	public static String getCurrentPropertyPath(Map<String, Object> context) {
		return (String)context.get(CURRENT_PROPERTY_PATH);
	}

	public static String getFullPropertyPath(Map<String, Object> context) {
		return (String)context.get(FULL_PROPERTY_PATH);
	}

	public static void setFullPropertyPath(Map<String, Object> context, String path) {
		context.put(FULL_PROPERTY_PATH, path);

	}

	public static void updateCurrentPropertyPath(Map<String, Object> context, Object name) {
		String currentPath=getCurrentPropertyPath(context);
		if (name!=null) {
			if (currentPath!=null) {
                StringBuilder sb = new StringBuilder(currentPath);
                sb.append(".");
                sb.append(name.toString());
				currentPath = sb.toString();
			}	else {
				currentPath = name.toString();
			}
			context.put(CURRENT_PROPERTY_PATH, currentPath);
		}
	}

	public static void setSetMap(Map<String, Object> context, Map<Object, Object> setMap, String path) {
		Map<Object, Map<Object, Object>> mapOfSetMaps=(Map)context.get(SET_MAP_KEY);
		if (mapOfSetMaps==null) {
			mapOfSetMaps=new HashMap<Object, Map<Object, Object>>();
			context.put(SET_MAP_KEY, mapOfSetMaps);
		}
		mapOfSetMaps.put(path, setMap);
	}

	public static Map<Object, Object> getSetMap(Map<String, Object> context, String path) {
		Map<Object, Map<Object, Object>> mapOfSetMaps=(Map)context.get(SET_MAP_KEY);
		if (mapOfSetMaps==null) {
			return null;
		}
		return mapOfSetMaps.get(path);
	}

	private static boolean getBooleanProperty(String property, Map<String, Object> context) {
		Boolean myBool=(Boolean)context.get(property);
		return (myBool==null)?false:myBool.booleanValue();
	}

	private static void setBooleanValue(String property, Map<String, Object> context, boolean value) {
		context.put(property, new Boolean(value));
	}

	/**
	 *
	 */
	public static void clearCurrentPropertyPath(Map<String, Object> context) {
		context.put(CURRENT_PROPERTY_PATH, null);

	}


    public static void clear(Map<String, Object> context) {
        if (context != null) {
            context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED,null);
            context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED,null);
    
            context.put(CURRENT_PROPERTY_PATH,null);
            context.put(FULL_PROPERTY_PATH,null);
        }

    }


    public static final String CREATE_NULL_OBJECTS = "xwork.NullHandler.createNullObjects";
    public static final String DENY_METHOD_EXECUTION = "xwork.MethodAccessor.denyMethodExecution";
    public static final String DENY_INDEXED_ACCESS_EXECUTION = "xwork.IndexedPropertyAccessor.denyMethodExecution";



}
