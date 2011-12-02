/*
 * $Id$
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.lang.reflect.InvocationTargetException;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor calls <code>prepare()</code> on actions which implement
 * {@link Preparable}. This interceptor is very useful for any situation where
 * you need to ensure some logic runs before the actual execute method runs.
 *
 * <p/> A typical use of this is to run some logic to load an object from the
 * database so that when parameters are set they can be set on this object. For
 * example, suppose you have a User object with two properties: <i>id</i> and
 * <i>name</i>. Provided that the params interceptor is called twice (once
 * before and once after this interceptor), you can load the User object using
 * the id property, and then when the second params interceptor is called the
 * parameter <i>user.name</i> will be set, as desired, on the actual object
 * loaded from the database. See the example for more info.
 *
 * <p/>
 * <b>Note:</b> Since XWork 2.0.2, this interceptor extends {@link MethodFilterInterceptor}, therefore being
 * able to deal with excludeMethods / includeMethods parameters. See [Workflow Interceptor]
 * (class {@link DefaultWorkflowInterceptor}) for documentation and examples on how to use this feature.
 *
 * <p/><b>Update</b>: Added logic to execute a prepare{MethodName} and conditionally
 * the a general prepare() Method, depending on the 'alwaysInvokePrepare' parameter/property
 * which is by default true. This allows us to run some logic based on the method
 * name we specify in the {@link com.opensymphony.xwork2.ActionProxy}. For example, you can specify a
 * prepareInput() method that will be run before the invocation of the input method.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>alwaysInvokePrepare - Default to true. If true, prepare will always be invoked,
 * otherwise it will not.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points to this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- Calls the params interceptor twice, allowing you to
 *       pre-load data for the second time parameters are set --&gt;
 *  &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *      &lt;interceptor-ref name="params"/&gt;
 *      &lt;interceptor-ref name="prepare"/&gt;
 *      &lt;interceptor-ref name="basicStack"/&gt;
 *      &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 *  &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 * @author Philip Luppens
 * @author tm_jee
 * @see com.opensymphony.xwork2.Preparable
 */
public class PrepareInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = -5216969014510719786L;

    private final static String PREPARE_PREFIX = "prepare";
    private final static String ALT_PREPARE_PREFIX = "prepareDo";

    private boolean alwaysInvokePrepare = true;
    private boolean firstCallPrepareDo = false;

    /**
     * Sets if the <code>preapare</code> method should always be executed.
     * <p/>
     * Default is <tt>true</tt>.
     *
     * @param alwaysInvokePrepare if <code>prepare</code> should always be executed or not.
     */
    public void setAlwaysInvokePrepare(String alwaysInvokePrepare) {
        this.alwaysInvokePrepare = Boolean.parseBoolean(alwaysInvokePrepare);
    }

    /**
     * Sets if the <code>prepareDoXXX</code> method should be called first
     * <p/>
     * Default is <tt>false</tt> for backward compatibility
     *
     * @param firstCallPrepareDo if <code>prepareDoXXX</code> should be called first
     */
    public void setFirstCallPrepareDo(String firstCallPrepareDo) {
        this.firstCallPrepareDo = Boolean.parseBoolean(firstCallPrepareDo);
    }

    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof Preparable) {
            try {
                String[] prefixes;
                if (firstCallPrepareDo) {
                    prefixes = new String[] {ALT_PREPARE_PREFIX, PREPARE_PREFIX};
                } else {
                    prefixes = new String[] {PREPARE_PREFIX, ALT_PREPARE_PREFIX};
                }
                PrefixMethodInvocationUtil.invokePrefixMethod(invocation, prefixes);
            }
            catch (InvocationTargetException e) {
                /*
                 * The invoked method threw an exception and reflection wrapped it
                 * in an InvocationTargetException.
                 * If possible re-throw the original exception so that normal
                 * exception handling will take place.
                 */
                Throwable cause = e.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                } else if(cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    /*
                     * The cause is not an Exception or Error (must be Throwable) so
                     * just re-throw the wrapped exception.
                     */
                    throw e;
                }
            }

            if (alwaysInvokePrepare) {
                ((Preparable) action).prepare();
            }
        }

        return invocation.invoke();
    }

}
