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
package com.opensymphony.xwork2.interceptor.annotations;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Invokes any annotated methods on the action. Specifically, it supports the following
 * annotations:
 * <ul>
 * <li> &#64;{@link Before} - will be invoked before the action method. If the returned value is not null, it is
 * returned as the action result code</li>
 * <li> &#64;{@link BeforeResult} - will be invoked after the action method but before the result execution</li>
 * <li> &#64;{@link After} - will be invoked after the action method and result execution</li>
 * </ul>
 * </p>
 * <p/>
 * <p>There can be multiple methods marked with the same annotations, but the order of their execution
 * is not guaranteed. However, the annotated methods on the superclass chain are guaranteed to be invoked before the
 * annotated method in the current class in the case of a {@link Before} annotations and after, if the annotations is
 * {@link After}.</p>
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * <pre>
 * <!-- START SNIPPET: javacode -->
 *  public class BaseAnnotatedAction {
 *  	protected String log = "";
 * <p/>
 *  	&#64;Before
 *  	public String baseBefore() {
 *  		log = log + "baseBefore-";
 *  		return null;
 *  	}
 *  }
 * <p/>
 *  public class AnnotatedAction extends BaseAnnotatedAction {
 *  	&#64;Before
 *  	public String before() {
 *  		log = log + "before";
 *  		return null;
 *  	}
 * <p/>
 *  	public String execute() {
 *  		log = log + "-execute";
 *  		return Action.SUCCESS;
 *  	}
 * <p/>
 *  	&#64;BeforeResult
 *  	public void beforeResult() throws Exception {
 *  		log = log +"-beforeResult";
 *  	}
 * <p/>
 *  	&#64;After
 *  	public void after() {
 *  		log = log + "-after";
 *  	}
 *  }
 * <!-- END SNIPPET: javacode -->
 *  </pre>
 * <p/>
 * <!-- START SNIPPET: example -->
 * <p>With the interceptor applied and the action executed on <code>AnnotatedAction</code> the log
 * instance variable will contain <code>baseBefore-before-execute-beforeResult-after</code>.</p>
 * <!-- END SNIPPET: example -->
 * <p/>
 * <p/>Configure a stack in xwork.xml that replaces the PrepareInterceptor with the AnnotationWorkflowInterceptor:
 * <pre>
 * <!-- START SNIPPET: stack -->
 * &lt;interceptor-stack name="annotatedStack"&gt;
 * 	&lt;interceptor-ref name="staticParams"/&gt;
 * 	&lt;interceptor-ref name="params"/&gt;
 * 	&lt;interceptor-ref name="conversionError"/&gt;
 * 	&lt;interceptor-ref name="annotationWorkflow"/&gt;
 * &lt;/interceptor-stack&gt;
 *  <!-- END SNIPPET: stack -->
 * </pre>
 *
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 * @author Dan Oxlade, dan d0t oxlade at gmail d0t c0m
 */
public class AnnotationWorkflowInterceptor extends AbstractInterceptor implements PreResultListener {

    /**
     * Discovers annotated methods on the action and calls them according to the workflow
     *
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        invocation.addPreResultListener(this);
        List<Method> methods = new ArrayList<Method>(AnnotationUtils.getAnnotatedMethods(action.getClass(), Before.class));
        if (methods.size() > 0) {
            // methods are only sorted by priority
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return comparePriorities(method1.getAnnotation(Before.class).priority(),
                                method2.getAnnotation(Before.class).priority());
                }
            });
            for (Method m : methods) {
                final String resultCode = (String) m
                        .invoke(action, (Object[]) null);
                if (resultCode != null) {
                    // shortcircuit execution
                    return resultCode;
                }
            }
        }

        String invocationResult = invocation.invoke();

        // invoke any @After methods
        methods = new ArrayList<Method>(AnnotationUtils.getAnnotatedMethods(action.getClass(), After.class));

        if (methods.size() > 0) {
            // methods are only sorted by priority
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return comparePriorities(method1.getAnnotation(After.class).priority(),
                                method2.getAnnotation(After.class).priority());
                }
            });
            for (Method m : methods) {
                m.invoke(action, (Object[]) null);
            }
        }

        return invocationResult;
    }

    protected static int comparePriorities(int val1, int val2) {
        if (val2 < val1) {
            return -1;
        } else if (val2 > val1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Invokes any &#64;BeforeResult annotated methods
     *
     * @see com.opensymphony.xwork2.interceptor.PreResultListener#beforeResult(com.opensymphony.xwork2.ActionInvocation,String)
     */
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        Object action = invocation.getAction();
        List<Method> methods = new ArrayList<Method>(AnnotationUtils.getAnnotatedMethods(action.getClass(), BeforeResult.class));

        if (methods.size() > 0) {
            // methods are only sorted by priority
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return comparePriorities(method1.getAnnotation(BeforeResult.class).priority(),
                                method2.getAnnotation(BeforeResult.class).priority());
                }
            });
            for (Method m : methods) {
                try {
                    m.invoke(action, (Object[]) null);
                } catch (Exception e) {
                    throw new XWorkException(e);
                }
            }
        }
    }

}
