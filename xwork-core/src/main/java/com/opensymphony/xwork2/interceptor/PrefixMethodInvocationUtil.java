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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A utility class for invoking prefixed methods in action class.
 * 
 * Interceptors that made use of this class are:
 * <ul>
 * 	 <li>DefaultWorkflowInterceptor</li>
 *   <li>PrepareInterceptor</li>
 * </ul>
 * 
 * <p/>
 * 
 * <!-- START SNIPPET: javadocDefaultWorkflowInterceptor -->
 * 
 * <b>In DefaultWorkflowInterceptor</b>
 * <p>applies only when action implements {@link com.opensymphony.xwork2.Validateable}</p>
 * <ol>
 *    <li>if the action class have validate{MethodName}(), it will be invoked</li>
 *    <li>else if the action class have validateDo{MethodName}(), it will be invoked</li>
 *    <li>no matter if 1] or 2] is performed, if alwaysInvokeValidate property of the interceptor is "true" (which is by default "true"), validate() will be invoked.</li>
 * </ol>
 * 
 * <!-- END SNIPPET: javadocDefaultWorkflowInterceptor -->
 * 
 * 
 * <!-- START SNIPPET: javadocPrepareInterceptor -->
 * 
 * <b>In PrepareInterceptor</b>
 * <p>Applies only when action implements Preparable</p>
 * <ol>
 *    <li>if the action class have prepare{MethodName}(), it will be invoked</li>
 *    <li>else if the action class have prepareDo(MethodName()}(), it will be invoked</li>
 *    <li>no matter if 1] or 2] is performed, if alwaysinvokePrepare property of the interceptor is "true" (which is by default "true"), prepare() will be invoked.</li>
 * </ol>
 * 
 * <!-- END SNIPPET: javadocPrepareInterceptor -->
 * 
 * @author Philip Luppens
 * @author tm_jee
 */
public class PrefixMethodInvocationUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(PrefixMethodInvocationUtil.class);

    private static final String DEFAULT_INVOCATION_METHODNAME = "execute";

    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    /**
	 * This method will prefix <code>actionInvocation</code>'s <code>ActionProxy</code>'s
	 * <code>method</code> with <code>prefixes</code> before invoking the prefixed method.
	 * Order of the <code>prefixes</code> is important, as this method will return once 
	 * a prefixed method is found in the action class.
	 * 
	 * <p/>
	 * 
	 * For example, with
	 * <pre>
	 *   invokePrefixMethod(actionInvocation, new String[] { "prepare", "prepareDo" });
	 * </pre>
	 * 
	 * Assuming <code>actionInvocation.getProxy(),getMethod()</code> returns "submit", 
	 * the order of invocation would be as follows:-
	 * <ol>
	 *   <li>prepareSubmit()</li>
	 *   <li>prepareDoSubmit()</li>
	 * </ol>
	 * 
	 * If <code>prepareSubmit()</code> exists, it will be invoked and this method 
	 * will return, <code>prepareDoSubmit()</code> will NOT be invoked. 
	 * 
	 * <p/>
	 * 
	 * On the other hand, if <code>prepareDoSubmit()</code> does not exists, and 
	 * <code>prepareDoSubmit()</code> exists, it will be invoked.
	 * 
	 * <p/>
	 * 
	 * If none of those two methods exists, nothing will be invoked.
	 * 
	 * @param actionInvocation  the action invocation
	 * @param prefixes  prefixes for method names
	 * @throws InvocationTargetException is thrown if invocation of a method failed.
	 * @throws IllegalAccessException  is thrown if invocation of a method failed.
	 */
	public static void invokePrefixMethod(ActionInvocation actionInvocation, String[] prefixes) throws InvocationTargetException, IllegalAccessException {
		Object action = actionInvocation.getAction();
		
		String methodName = actionInvocation.getProxy().getMethod();
		
		if (methodName == null) {
			// if null returns (possible according to the docs), use the default execute 
	        methodName = DEFAULT_INVOCATION_METHODNAME;
		}
		
		Method method = getPrefixedMethod(prefixes, methodName, action);
		if (method != null) {
			method.invoke(action, new Object[0]);
		}
	}
	
	
	/**
	 * This method returns a {@link Method} in <code>action</code>. The method 
	 * returned is found by searching for method in <code>action</code> whose method name
	 * is equals to the result of appending each <code>prefixes</code>
	 * to <code>methodName</code>. Only the first method found will be returned, hence
	 * the order of <code>prefixes</code> is important. If none is found this method
	 * will return null.
	 * 
	 * @param prefixes the prefixes to prefix the <code>methodName</code>
	 * @param methodName the method name to be prefixed with <code>prefixes</code>
	 * @param action the action class of which the prefixed method is to be search for.
	 * @return a {@link Method} if one is found, else <tt>null</tt>.
	 */
	public static Method getPrefixedMethod(String[] prefixes, String methodName, Object action) {
		assert(prefixes != null);
		String capitalizedMethodName = capitalizeMethodName(methodName);
        for (String prefixe : prefixes) {
            String prefixedMethodName = prefixe + capitalizedMethodName;
            try {
                return action.getClass().getMethod(prefixedMethodName, EMPTY_CLASS_ARRAY);
            }
            catch (NoSuchMethodException e) {
                // hmm -- OK, try next prefix
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cannot find method [#0] in action [#1]", prefixedMethodName, action.toString());
                }
            }
        }
		return null;
	}
	
	/**
	 * This method capitalized the first character of <code>methodName</code>.
	 * <br/>
	 * eg. <code>capitalizeMethodName("someMethod");</code> will return <code>"SomeMethod"</code>.
	 * 
	 * @param methodName the method name
	 * @return capitalized method name
	 */
	public static String capitalizeMethodName(String methodName) {
		assert(methodName != null);
		return methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
	}

}
