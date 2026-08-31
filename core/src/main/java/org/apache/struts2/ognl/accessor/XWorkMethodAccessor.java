/*
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
package org.apache.struts2.ognl.accessor;

import org.apache.struts2.util.reflection.ReflectionContextState;
import ognl.MethodFailedException;
import ognl.OgnlException;
import ognl.ObjectMethodAccessor;
import ognl.ObjectIndexedPropertyDescriptor;
import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Allows methods to be executed under normal cirumstances, except when {@link ReflectionContextState#DENY_METHOD_EXECUTION}
 * is in the action context with a value of true.
 *
 * @author Patrick Lightbody
 * @author tmjee
 */
public class XWorkMethodAccessor extends ObjectMethodAccessor {

    private static final Logger LOG = LogManager.getLogger(XWorkMethodAccessor.class);

    @Override
    public Object callMethod(OgnlContext context, Object object, String string, Object[] objects) throws MethodFailedException {

        //Collection property accessing
        //this if statement ensures that ognl
        //statements of the form someBean.mySet('keyPropVal')
        //return the set element with value of the keyProp given

        if (objects.length == 1) {
            try {
                if (OgnlRuntime.hasSetProperty(context, object, string)) {
                    PropertyDescriptor descriptor = OgnlRuntime.getPropertyDescriptor(object.getClass(), string);
                    Class propertyType = descriptor.getPropertyType();
                    if ((Collection.class).isAssignableFrom(propertyType)) {
                        //go directly through OgnlRuntime here
                        //so that property strings are not cleared
                        //i.e. OgnlUtil should be used initially, OgnlRuntime
                        //thereafter

                        Object propVal = OgnlRuntime.getProperty(context, object, string);
                        //use the Collection property accessor instead of the individual property accessor, because
                        //in the case of Lists otherwise the index property could be used
                        PropertyAccessor accessor = OgnlRuntime.getPropertyAccessor(Collection.class);
                        ReflectionContextState.setGettingByKeyProperty(context, true);
                        return accessor.getProperty(context, propVal, objects[0]);
                    }
                }
            } catch (Exception oe) {
                //this exception should theoretically never happen
                //log it
                LOG.error("An unexpected exception occurred", oe);
            }

        }

        if (!ReflectionContextState.isDenyMethodExecution(context)) {
            return callMethodWithDebugInfo(context, object, string, objects);
        }

        //Method execution is denied. Indexed property access, i.e. the getXXX(A) / setXXX(A,B) pattern, is
        //the one exception, because reading a['k'] must keep working during parameter binding. It is
        //restricted to calls which really are the indexed accessor of a property on the target type: a name
        //prefix and an argument count alone would let any method be called while execution is denied.
        if (isIndexedPropertyAccessor(object, string, objects)
                && !isIndexedAccessDenied(context)) {
            return callMethodWithDebugInfo(context, object, string, objects);
        }
        return null;
    }

    @SuppressWarnings("removal") // the constant is deprecated for removal in 8.0.0 (WW-5699); until then it is still honoured
    private static boolean isIndexedAccessDenied(OgnlContext context) {
        Boolean denied = (Boolean) context.get(ReflectionContextState.DENY_INDEXED_ACCESS_EXECUTION);
        return denied != null && denied;
    }

    /**
     * Whether this call is the indexed accessor of a property on the target type, as opposed to an ordinary
     * method which merely shares the {@code get}/{@code set} prefix and argument count of one.
     * <p>
     * The property name alone is not enough to decide, for two reasons. A class declaring the indexed pair
     * {@code getItem(int)} / {@code setItem(int, String)} may <em>also</em> declare an unrelated
     * {@code getItem(String)} overload, and it is that overload OGNL dispatches a one-argument call to, since
     * the argument types pick the method and the caller chooses those. And an indexed property may be
     * read-only, whose name would otherwise legitimise an unrelated two-argument {@code setItem(String, String)}.
     * So the descriptor's own accessor must be the method that will actually be invoked: same name, same
     * direction, and no same-arity overload for the dispatcher to prefer instead.
     */
    private boolean isIndexedPropertyAccessor(Object object, String methodName, Object[] args) {
        boolean reading = args.length == 1 && methodName.startsWith("get");
        boolean writing = args.length == 2 && methodName.startsWith("set");
        if (object == null || methodName.length() <= 3 || (!reading && !writing)) {
            return false;
        }
        Class<?> targetType = object.getClass();
        String propertyName = Introspector.decapitalize(methodName.substring(3));
        try {
            Method accessor = indexedAccessorOf(OgnlRuntime.getPropertyDescriptor(targetType, propertyName), reading);
            return accessor != null
                    && accessor.getName().equals(methodName)
                    && isTheOnlyDispatchCandidate(targetType, methodName, args.length);
        } catch (OgnlException e) {
            LOG.debug("Could not determine whether [{}] is an indexed property of [{}]", propertyName, targetType, e);
            return false;
        }
    }

    /**
     * The indexed accessor a descriptor declares for the requested direction, or {@code null} when the
     * descriptor is not an indexed one or declares no accessor that way round. Both flavours are covered:
     * JavaBeans int-indexed properties, and OGNL's arbitrary-object-indexed ones.
     */
    private static Method indexedAccessorOf(PropertyDescriptor descriptor, boolean reading) {
        if (descriptor instanceof IndexedPropertyDescriptor indexed) {
            return reading ? indexed.getIndexedReadMethod() : indexed.getIndexedWriteMethod();
        }
        if (descriptor instanceof ObjectIndexedPropertyDescriptor objectIndexed) {
            return reading ? objectIndexed.getIndexedReadMethod() : objectIndexed.getIndexedWriteMethod();
        }
        return null;
    }

    /**
     * Whether the named method is the only one of that argument count, and so is certainly the one OGNL
     * dispatches to. With an overload present the argument values decide, and those come from the caller.
     * <p>
     * Candidates are counted by signature rather than by {@link Method}, because {@code getMethods} reports
     * an overridden method and the method overriding it separately. Those two share a parameter list, so
     * they are not a choice the dispatcher makes - only one implementation can ever run - and an accessor
     * refined in a subclass must not lose the bean its indexed property access. Distinct parameter lists of
     * the same arity are the real overloads, and still deny.
     */
    private static boolean isTheOnlyDispatchCandidate(Class<?> targetType, String methodName, int argCount) {
        List<Method> candidates = OgnlRuntime.getMethods(targetType, methodName, false);
        return candidates != null
                && candidates.stream()
                        .filter(candidate -> candidate.getParameterCount() == argCount)
                        .map(candidate -> Arrays.asList(candidate.getParameterTypes()))
                        .distinct()
                        .count() == 1;
    }

    private Object callMethodWithDebugInfo(OgnlContext context, Object object, String methodName, Object[] objects) throws MethodFailedException {
        try {
            return super.callMethod(context, object, methodName, objects);
        } catch (MethodFailedException e) {
            if (LOG.isDebugEnabled()) {
                if (!(e.getReason() instanceof NoSuchMethodException)) {
                    // the method exists on the target object, but something went wrong
                    LOG.debug("Error calling method through OGNL: object: [{}] method: [{}] args: [{}] - {}", object, methodName, Arrays.toString(objects), e.getReason());
                }
            }
            throw e;
        }
    }

    @Override
    public Object callStaticMethod(OgnlContext context, Class aClass, String string, Object[] objects) throws MethodFailedException {
        boolean e = ReflectionContextState.isDenyMethodExecution(context);

        if (!e) {
            return callStaticMethodWithDebugInfo(context, aClass, string, objects);
        } else {
            return null;
        }
    }

    private Object callStaticMethodWithDebugInfo(OgnlContext context, Class aClass, String methodName,
                                                 Object[] objects) throws MethodFailedException {
        try {
            return super.callStaticMethod(context, aClass, methodName, objects);
        } catch (MethodFailedException e) {
            if (LOG.isDebugEnabled()) {
                if (!(e.getReason() instanceof NoSuchMethodException)) {
                    // the method exists on the target class, but something went wrong
                    LOG.debug("Error calling method through OGNL, class: [{}] method: [{}] args: [{}] - {}", aClass.getName(), methodName, Arrays.toString(objects), e.getReason());
                }
            }
            throw e;
        }
    }
}
