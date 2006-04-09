/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.components;

import org.apache.struts.action2.util.FastByteArrayOutputStream;
import org.apache.struts.action2.views.jsp.TagUtils;
import org.apache.struts.action2.views.util.ContextUtil;
import org.apache.struts.action2.views.util.UrlHelper;
import org.apache.struts.action2.dispatcher.mapper.ActionMapping;
import org.apache.struts.action2.dispatcher.mapper.ActionMapper;
import org.apache.struts.action2.dispatcher.mapper.ActionMapperFactory;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * Base class to extend for UI components.
 * <p/>
 * This class is a good extension point when building reuseable UI components.
 *
 * @author plightbo
 */
public class Component {
    private static final Log LOG = LogFactory.getLog(Component.class);

    public static final String COMPONENT_STACK = "__component_stack";

    protected OgnlValueStack stack;
    protected Map parameters;
    protected String id;

    /**
     * Constructor.
     *
     * @param stack  OGNL value stack.
     */
    public Component(OgnlValueStack stack) {
        this.stack = stack;
        this.parameters = new HashMap();
        getComponentStack().push(this);
    }

    /**
     * Get's the name of this component.
     * @return the name of this component.
     */
    private String getComponentName() {
        Class c = getClass();
        String name = c.getName();
        int dot = name.lastIndexOf('.');

        return name.substring(dot + 1).toLowerCase();
    }

    /**
     * Get's the OGNL value stack assoicated with this component.
     * @return the OGNL value stack assoicated with this component.
     */
    public OgnlValueStack getStack() {
        return stack;
    }

    /**
     * Get's the component stack of this component.
     * @return the component stack of this component, never <tt>null</tt>.
     */
    public Stack getComponentStack() {
        Stack componentStack = (Stack) stack.getContext().get(COMPONENT_STACK);
        if (componentStack == null) {
            componentStack = new Stack();
            stack.getContext().put(COMPONENT_STACK, componentStack);
        }
        return componentStack;
    }

    /**
     * Callback for the start tag of this component.
     * Should the body be evaluated?
     *
     * @param writer  the output writer.
     * @return true if the body should be evaluated
     */
    public boolean start(Writer writer) {
        return true;
    }

    /**
     * Callback for the end tag of this component.
     * Should the body be evaluated again?
     * <p/>
     * <b>NOTE:</b> will pop component stack.
     * @param writer  the output writer.
     * @param body    the rendered body.
     * @return true if the body should be evaluated again
     */
    public boolean end(Writer writer, String body) {
        return end(writer, body, true);
    }
    
    /**
     * Callback for the start tag of this component.
     * Should the body be evaluated again?
     * <p/>
     * <b>NOTE:</b> has a parameter to determine to pop the component stack.
     * @param writer  the output writer.
     * @param body    the rendered body.
     * @param popComponentStack  should the component stack be popped?
     * @return true if the body should be evaluated again
     */
    protected boolean end(Writer writer, String body, boolean popComponentStack) {
    	assert(body != null);

        try {
            writer.write(body);
        } catch (IOException e) {
            throw new RuntimeException("IOError while writing the body: " + e.getMessage(), e);
        }
        if (popComponentStack) {
        	popComponentStack();
        }
        return false;
    }

    /**
     * Pops the component stack.
     */
    protected void popComponentStack() {
    	getComponentStack().pop();
    }

    /**
     * Finds the nearest ancestor of this component stack.
     * @param clazz the class to look for, or if assignable from.
     * @return  the component if found, <tt>null</tt> if not.
     */
    protected Component findAncestor(Class clazz) {
        Stack componentStack = getComponentStack();
        int currPosition = componentStack.search(this);
        if (currPosition >= 0) {
        	int start = componentStack.size() - currPosition - 1;
        	
        	//for (int i = componentStack.size() - 2; i >= 0; i--) {
        	for (int i = start; i >=0; i--) {
            	Component component = (Component) componentStack.get(i);
            	if (clazz.isAssignableFrom(component.getClass()) && component != this) {
            		return component;
            	}
        	}
        }

        return null;
    }

    /**
     * Evaluates the OGNL stack to find a String value.
     * @param expr  OGNL expression.
     * @return  the String value found.
     */
    protected String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    /**
     * Evaluates the OGNL stack to find a String value.
     * <p/>
     * If the given expression is <tt>null</tt/> a error is logged and a <code>RuntimeException</code> is thrown
     * constructed with a messaged based on the given field and errorMsg paramter.
     *
     * @param expr  OGNL expression.
     * @param field   field name used when throwing <code>RuntimeException</code>.
     * @param errorMsg  error message used when throwing <code>RuntimeException</code>.
     * @return  the String value found.
     * @throws RuntimeException is thrown in case of expression is <tt>null</tt>.
     */
    protected String findString(String expr, String field, String errorMsg) {
        if (expr == null) {
            throw fieldError(field, errorMsg, null);
        } else {
            return findString(expr);
        }
    }

    /**
     * Constructs?a <code>RuntimeException</code> based on the given information.
     * <p/>
     * A message is constructed and logged at ERROR level before being returned
     * as a <code>RuntimeException</code>.
     * @param field   field name used when throwing <code>RuntimeException</code>.
     * @param errorMsg  error message used when throwing <code>RuntimeException</code>.
     * @param e  the caused exception, can be <tt>null</tt>.
     * @return  the constructed <code>RuntimeException</code>.
     */
    protected RuntimeException fieldError(String field, String errorMsg, Exception e) {
        String msg = "tag " + getComponentName() + ", field " + field + ": " + errorMsg;
        if (e == null) {
            LOG.error(msg);
            return new RuntimeException(msg);
        } else {
            LOG.error(msg, e);
            return new RuntimeException(msg, e);
        }
    }

    /**
     * Finds a value from the OGNL stack based on the given expression.
     * @param expr  the expression. Returns <tt>null</tt> if expr is null.
     * @return the value, <tt>null</tt> if not found.
     */
    protected Object findValue(String expr) {
        if (expr == null) {
            return null;
        }

        if (altSyntax()) {
            // does the expression start with %{ and end with }? if so, just cut it off!
            if (expr.startsWith("%{") && expr.endsWith("}")) {
                expr = expr.substring(2, expr.length() - 1);
            }
        }

        return getStack().findValue(expr);
    }

    /**
     * Is the altSyntax enabled?
     * <p/>
     * See <code>struts.properties</code> where the altSyntax flag is defined.
     * <b>Note:</b> Since WebWork 2.17 the altSyntax is default <tt>true</tt>.
     * @return true if the altSyntax is enabled.
     */
    public boolean altSyntax() {
        return ContextUtil.isUseAltSyntax(stack.getContext());
    }

    /**
     * Evaluates the OGNL stack to find an Object value.
     * <p/>
     * If the given expression is <tt>null</tt/> a error is logged and a <code>RuntimeException</code> is thrown
     * constructed with a messaged based on the given field and errorMsg paramter.
     *
     * @param expr  OGNL expression.
     * @param field   field name used when throwing <code>RuntimeException</code>.
     * @param errorMsg  error message used when throwing <code>RuntimeException</code>.
     * @return  the Object found, is never <tt>null</tt>.
     * @throws RuntimeException is thrown in case of not found in the OGNL stack, or expression is <tt>null</tt>.
     */
    protected Object findValue(String expr, String field, String errorMsg) {
        if (expr == null) {
            throw fieldError(field, errorMsg, null);
        } else {
            Object value = null;
            Exception problem = null;
            try {
                value = findValue(expr);
            } catch (Exception e) {
                problem = e;
            }

            if (value == null) {
                throw fieldError(field, errorMsg, problem);
            }

            return value;
        }
    }

    /**
     * Evaluates the OGNL stack to find an Object of the given type.
     * <p/>
     * This method only supports the altSyntax. So this should be set to true.
     * @param expr  OGNL expression.
     * @param toType  the type expected to find.
     * @return  the Object found, or <tt>null</tt> if not found.
     */
    protected Object findValue(String expr, Class toType) {
        if (altSyntax() && toType == String.class) {
            return TextParseUtil.translateVariables('%', expr, stack);
        } else {
            if (altSyntax()) {
                // does the expression start with %{ and end with }? if so, just cut it off!
                if (expr.startsWith("%{") && expr.endsWith("}")) {
                    expr = expr.substring(2, expr.length() - 1);
                }
            }

            return getStack().findValue(expr, toType);
        }
    }

    /**
     * Renders an action URL by consulting the {@link org.apache.struts.action2.dispatcher.mapper.ActionMapper}.
     * @param action      the action
     * @param namespace   the namespace
     * @param method      the method
     * @param req         HTTP request
     * @param res         HTTP response
     * @param parameters  parameters
     * @param scheme      http or https
     * @param includeContext  should the context path be included or not
     * @param encodeResult    should the url be encoded
     * @return the action url.
     */
    protected String determineActionURL(String action, String namespace, String method,
                                        HttpServletRequest req, HttpServletResponse res, Map parameters, String scheme,
                                        boolean includeContext, boolean encodeResult) {
        String finalAction = findString(action);
        String finalNamespace = determineNamespace(namespace, getStack(), req);
        ActionMapping mapping = new ActionMapping(finalAction, finalNamespace, method, parameters);
        ActionMapper mapper = ActionMapperFactory.getMapper();
        String uri = mapper.getUriFromActionMapping(mapping);
        return UrlHelper.buildUrl(uri, req, res, parameters, scheme, includeContext, encodeResult);
    }

    /**
     * Determines the namespace of the current page being renderdd. Useful for Form, URL, and href generations.
     * @param namespace  the namespace
     * @param stack      OGNL value stack
     * @param req        HTTP request
     * @return  the namepsace of the current page being rendered, is never <tt>null</tt>.
     */
    protected String determineNamespace(String namespace, OgnlValueStack stack, HttpServletRequest req) {
        String result;

        if (namespace == null) {
            result = TagUtils.buildNamespace(stack, req);
        } else {
            result = findString(namespace);
        }

        if (result == null) {
            result = "";
        }

        return result;
    }

    /**
     * Pushes this component's parameter Map as well as the component itself on to the stack
     * and then copies the supplied parameters over. Because the component's parameter Map is
     * pushed before the component itself, any key-value pair that can't be assigned to componet
     * will be set in the parameters Map.
     *
     * @param params  the parameters to copy.
     */
    public void copyParams(Map params) {
        stack.push(parameters);
        stack.push(this);
        try {
            for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                stack.setValue(key, entry.getValue());
            }
        } finally {
            stack.pop();
            stack.pop();
        }
    }

    /**
     * Constructs a string representation of the given exception.
     * @param t  the exception
     * @return the exception as a string.
     */
    protected String toString(Throwable t) {
        FastByteArrayOutputStream bout = new FastByteArrayOutputStream();
        PrintWriter wrt = new PrintWriter(bout);
        t.printStackTrace(wrt);
        wrt.close();

        return bout.toString();
    }

    /**
     * Get's the parameters.
     * @return the parameters. Is never <tt>null</tt>.
     */
    public Map getParameters() {
        return parameters;
    }

    /**
     * Add's all the given parameters to this componenets own parameters.
     * @param params the parameters to add.
     */
    public void addAllParameters(Map params) {
        parameters.putAll(params);
    }

    /**
     * Add's the given key and value to this components own parameter.
     * <p/>
     * If the provided key is <tt>null</tt> nothing happends.
     * If the provided value is <tt>null</tt> any existing parameter with
     * the given key name is removed.
     * @param key  the key of the new parameter to add.
     * @param value the value assoicated with the key.
     */
    public void addParameter(String key, Object value) {
        if (key != null) {
            Map params = getParameters();

            if (value == null) {
                params.remove(key);
            } else {
                params.put(key, value);
            }
        }
    }

    /**
     * Get's the id for referencing element.
     * @return the id for referencing element.
     */
    public String getId() {
        return id;
    }

    /**
     * id for referencing element. For UI and form tags it will be used as HTML id attribute
     * @a2.tagattribute required="false"
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Overwrite to set if body shold be used.
     * @return always false for this component.
     */
    public boolean usesBody() {
        return false;
    }

}
