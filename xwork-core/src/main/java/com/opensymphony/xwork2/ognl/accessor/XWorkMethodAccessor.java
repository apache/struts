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
package com.opensymphony.xwork2.ognl.accessor;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import ognl.MethodFailedException;
import ognl.ObjectMethodAccessor;
import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;


/**
 * Allows methods to be executed under normal cirumstances, except when {@link ReflectionContextState#DENY_METHOD_EXECUTION}
 * is in the action context with a value of true.
 *
 * @author Patrick Lightbody
 * @author tmjee
 */
public class XWorkMethodAccessor extends ObjectMethodAccessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(XWorkMethodAccessor.class);

    /**
     * @deprecated Use {@link ReflectionContextState#DENY_METHOD_EXECUTION} instead
     */
    @Deprecated public static final String DENY_METHOD_EXECUTION = ReflectionContextState.DENY_METHOD_EXECUTION;
    /**
     * @deprecated Use {@link ReflectionContextState#DENY_INDEXED_ACCESS_EXECUTION} instead
     */
    @Deprecated public static final String DENY_INDEXED_ACCESS_EXECUTION = ReflectionContextState.DENY_INDEXED_ACCESS_EXECUTION;


    @Override
    public Object callMethod(Map context, Object object, String string, Object[] objects) throws MethodFailedException {

        //Collection property accessing
        //this if statement ensures that ognl
        //statements of the form someBean.mySet('keyPropVal')
        //return the set element with value of the keyProp given
        
        if (objects.length==1 
                && context instanceof OgnlContext) {
            try {
              OgnlContext ogContext=(OgnlContext)context;
              if (OgnlRuntime.hasSetProperty(ogContext, object, string))  {
                  	PropertyDescriptor descriptor=OgnlRuntime.getPropertyDescriptor(object.getClass(), string);
                  	Class propertyType=descriptor.getPropertyType();
                  	if ((Collection.class).isAssignableFrom(propertyType)) {
                  	    //go directly through OgnlRuntime here
                  	    //so that property strings are not cleared
                  	    //i.e. OgnlUtil should be used initially, OgnlRuntime
                  	    //thereafter
                  	    
                  	    Object propVal=OgnlRuntime.getProperty(ogContext, object, string);
                  	    //use the Collection property accessor instead of the individual property accessor, because 
                  	    //in the case of Lists otherwise the index property could be used
                  	    PropertyAccessor accessor=OgnlRuntime.getPropertyAccessor(Collection.class);
                  	    ReflectionContextState.setGettingByKeyProperty(ogContext,true);
                  	    return accessor.getProperty(ogContext,propVal,objects[0]);
                  	}
              }
            }	catch (Exception oe) {
                //this exception should theoretically never happen
                //log it
            	LOG.error("An unexpected exception occurred", oe);
            }

        }

        //HACK - we pass indexed method access i.e. setXXX(A,B) pattern
        if (
                (objects.length == 2 && string.startsWith("set"))
                        ||
                        (objects.length == 1 && string.startsWith("get"))
                ) {
            Boolean exec = (Boolean) context.get(ReflectionContextState.DENY_INDEXED_ACCESS_EXECUTION);
            boolean e = ((exec == null) ? false : exec.booleanValue());
            if (!e) {
                return callMethodWithDebugInfo(context, object, string, objects);
            }
        }
        Boolean exec = (Boolean) context.get(ReflectionContextState.DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return callMethodWithDebugInfo(context, object, string, objects);
        } else {
            return null;
        }
    }

	private Object callMethodWithDebugInfo(Map context, Object object, String methodName,
			Object[] objects) throws MethodFailedException {
		try {
			return super.callMethod(context, object, methodName, objects);
		}
		catch(MethodFailedException e) {
			if (LOG.isDebugEnabled()) {
				if (!(e.getReason() instanceof NoSuchMethodException)) {
					// the method exists on the target object, but something went wrong
					String s = "Error calling method through OGNL: object: [#0] method: [#1] args: [#2]";
					LOG.debug(s, e.getReason(), object.toString(), methodName, Arrays.toString(objects));
				}
			}
			throw e;
		}
	}

    @Override
    public Object callStaticMethod(Map context, Class aClass, String string, Object[] objects) throws MethodFailedException {
        Boolean exec = (Boolean) context.get(ReflectionContextState.DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return callStaticMethodWithDebugInfo(context, aClass, string, objects);
        } else {
            return null;
        }
    }

	private Object callStaticMethodWithDebugInfo(Map context, Class aClass, String methodName,
			Object[] objects) throws MethodFailedException {
		try {
			return super.callStaticMethod(context, aClass, methodName, objects);
		}
		catch(MethodFailedException e) {
			if (LOG.isDebugEnabled()) {
				if (!(e.getReason() instanceof NoSuchMethodException)) {
					// the method exists on the target class, but something went wrong
					String s = "Error calling method through OGNL, class: [#0] method: [#1] args: [#2]";
					LOG.debug(s, e.getReason(), aClass.getName(), methodName, Arrays.toString(objects));
				}
			}
			throw e;
		}
	}
}
