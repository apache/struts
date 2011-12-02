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

package org.apache.struts2.interceptor.debugging;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.FilterDispatcher;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.FreemarkerResult;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

/**
 * <!-- START SNIPPET: description -->
 * Provides several different debugging screens to provide insight into the
 * data behind the page.
 * <!-- END SNIPPET: description -->
 * The value of the 'debug' request parameter determines
 * the screen:
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * <li> <code>xml</code> - Dumps the parameters, context, session, and value
 * stack as an XML document.</li>
 * <li> <code>console</code> - Shows a popup 'OGNL Console' that allows the
 * user to test OGNL expressions against the value stack. The XML data from
 * the 'xml' mode is inserted at the top of the page.</li>
 * <li> <code>command</code> - Tests an OGNL expression and returns the
 * string result. Only used by the OGNL console.</li>
 * <li><code>browser</code> Shows field values of an object specified in the 
 * <code>object<code> parameter (#context by default). When the <code>object<code>
 * parameters is set, the '#' character needs to be escaped to '%23'. Like
 * debug=browser&object=%23parameters</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 *  Example:
 * <!-- START SNIPPET: example -->
 *  http://localhost:8080/Welcome.action?debug=xml
 * <!-- END SNIPPET: example -->
 * <p/>
 * <!-- START SNIPPET: remarks -->
 * This interceptor only is activated when devMode is enabled in
 * struts.properties. The 'debug' parameter is removed from the parameter list
 * before the action is executed. All operations occur before the natural
 * Result has a chance to execute.
 * <!-- END SNIPPET: remarks -->
 */
public class DebuggingInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -3097324155953078783L;

    private final static Logger LOG = LoggerFactory.getLogger(DebuggingInterceptor.class);

    private String[] ignorePrefixes = new String[]{"org.apache.struts.",
            "com.opensymphony.xwork2.", "xwork."};
    private String[] _ignoreKeys = new String[]{"application", "session",
            "parameters", "request"};
    private HashSet<String> ignoreKeys = new HashSet<String>(Arrays.asList(_ignoreKeys));

    private final static String XML_MODE = "xml";
    private final static String CONSOLE_MODE = "console";
    private final static String COMMAND_MODE = "command";
    private final static String BROWSER_MODE = "browser";

    private final static String SESSION_KEY = "org.apache.struts2.interceptor.debugging.VALUE_STACK";

    private final static String DEBUG_PARAM = "debug";
    private final static String OBJECT_PARAM = "object";
    private final static String EXPRESSION_PARAM = "expression";
    private final static String DECORATE_PARAM = "decorate";

    private boolean enableXmlWithConsole = false;
    
    private boolean devMode;
    private FreemarkerManager freemarkerManager;
    
    private boolean consoleEnabled = false;
    private ReflectionProvider reflectionProvider;

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = "true".equals(mode);
    }
    
    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }
    
    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opensymphony.xwork2.interceptor.Interceptor#invoke(com.opensymphony.xwork2.ActionInvocation)
     */
    public String intercept(ActionInvocation inv) throws Exception {
        boolean actionOnly = false;
        boolean cont = true;
        Boolean devModeOverride = FilterDispatcher.getDevModeOverride();
        boolean devMode = devModeOverride != null ? devModeOverride.booleanValue() : this.devMode;
        if (devMode) {
            final ActionContext ctx = ActionContext.getContext();
            String type = getParameter(DEBUG_PARAM);
            ctx.getParameters().remove(DEBUG_PARAM);
            if (XML_MODE.equals(type)) {
                inv.addPreResultListener(
                        new PreResultListener() {
                            public void beforeResult(ActionInvocation inv, String result) {
                                printContext();
                            }
                        });
            } else if (CONSOLE_MODE.equals(type)) {
                consoleEnabled = true;
                inv.addPreResultListener(
                        new PreResultListener() {
                            public void beforeResult(ActionInvocation inv, String actionResult) {
                                String xml = "";
                                if (enableXmlWithConsole) {
                                    StringWriter writer = new StringWriter();
                                    printContext(new PrettyPrintWriter(writer));
                                    xml = writer.toString();
                                    xml = xml.replaceAll("&", "&amp;");
                                    xml = xml.replaceAll(">", "&gt;");
                                    xml = xml.replaceAll("<", "&lt;");
                                }
                                ActionContext.getContext().put("debugXML", xml);

                                FreemarkerResult result = new FreemarkerResult();
                                result.setFreemarkerManager(freemarkerManager);
                                result.setContentType("text/html");
                                result.setLocation("/org/apache/struts2/interceptor/debugging/console.ftl");
                                result.setParse(false);
                                try {
                                    result.execute(inv);
                                } catch (Exception ex) {
                                    LOG.error("Unable to create debugging console", ex);
                                }

                            }
                        });
            } else if (COMMAND_MODE.equals(type)) {
                ValueStack stack = (ValueStack) ctx.getSession().get(SESSION_KEY);
                if (stack == null) {
                    //allows it to be embedded on another page
                    stack = (ValueStack) ctx.get(ActionContext.VALUE_STACK);
                    ctx.getSession().put(SESSION_KEY, stack);
                }
                String cmd = getParameter(EXPRESSION_PARAM);

                ServletActionContext.getRequest().setAttribute("decorator", "none");
                HttpServletResponse res = ServletActionContext.getResponse();
                res.setContentType("text/plain");

                try {
                    PrintWriter writer =
                            ServletActionContext.getResponse().getWriter();
                    writer.print(stack.findValue(cmd));
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                cont = false;
            } else if (BROWSER_MODE.equals(type)) {
                actionOnly = true;
                inv.addPreResultListener(
                    new PreResultListener() {
                        public void beforeResult(ActionInvocation inv, String actionResult) {
                            String rootObjectExpression = getParameter(OBJECT_PARAM);
                            if (rootObjectExpression == null)
                                rootObjectExpression = "#context";
                            String decorate = getParameter(DECORATE_PARAM);
                            ValueStack stack = (ValueStack) ctx.get(ActionContext.VALUE_STACK);
                            Object rootObject = stack.findValue(rootObjectExpression);
                            
                            try {
                                StringWriter writer = new StringWriter();
                                ObjectToHTMLWriter htmlWriter = new ObjectToHTMLWriter(writer);
                                htmlWriter.write(reflectionProvider, rootObject, rootObjectExpression);
                                String html = writer.toString();
                                writer.close();
                                
                                stack.set("debugHtml", html);
                                
                                //on the first request, response can be decorated
                                //but we need plain text on the other ones
                                if ("false".equals(decorate))
                                    ServletActionContext.getRequest().setAttribute("decorator", "none");
                                
                                FreemarkerResult result = new FreemarkerResult();
                                result.setFreemarkerManager(freemarkerManager);
                                result.setContentType("text/html");
                                result.setLocation("/org/apache/struts2/interceptor/debugging/browser.ftl");
                                result.execute(inv);
                            } catch (Exception ex) {
                                LOG.error("Unable to create debugging console", ex);
                            }

                        }
                    });
            }
        } 
        if (cont) {
            try {
                if (actionOnly) {
                    inv.invokeActionOnly();
                    return null;
                } else {
                    return inv.invoke();
                }
            } finally {
                if (devMode && consoleEnabled) {
                    final ActionContext ctx = ActionContext.getContext();
                    ctx.getSession().put(SESSION_KEY, ctx.get(ActionContext.VALUE_STACK));
                }
            }
        } else {
            return null;
        }
    }


    /**
     * Gets a single string from the request parameters
     *
     * @param key The key
     * @return The parameter value
     */
    private String getParameter(String key) {
        String[] arr = (String[]) ActionContext.getContext().getParameters().get(key);
        if (arr != null && arr.length > 0) {
            return arr[0];
        }
        return null;
    }


    /**
     * Prints the current context to the response in XML format.
     */
    protected void printContext() {
        HttpServletResponse res = ServletActionContext.getResponse();
        res.setContentType("text/xml");

        try {
            PrettyPrintWriter writer = new PrettyPrintWriter(
                    ServletActionContext.getResponse().getWriter());
            printContext(writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Prints the current request to the existing writer.
     *
     * @param writer The XML writer
     */
    protected void printContext(PrettyPrintWriter writer) {
        ActionContext ctx = ActionContext.getContext();
        writer.startNode(DEBUG_PARAM);
        serializeIt(ctx.getParameters(), "parameters", writer,
                new ArrayList<Object>());
        writer.startNode("context");
        String key;
        Map ctxMap = ctx.getContextMap();
        for (Object o : ctxMap.keySet()) {
            key = o.toString();
            boolean print = !ignoreKeys.contains(key);

            for (String ignorePrefixe : ignorePrefixes) {
                if (key.startsWith(ignorePrefixe)) {
                    print = false;
                    break;
                }
            }
            if (print) {
                serializeIt(ctxMap.get(key), key, writer, new ArrayList<Object>());
            }
        }
        writer.endNode();
        Map requestMap = (Map) ctx.get("request");
        serializeIt(requestMap, "request", writer, filterValueStack(requestMap));
        serializeIt(ctx.getSession(), "session", writer, new ArrayList<Object>());

        ValueStack stack = (ValueStack) ctx.get(ActionContext.VALUE_STACK);
        serializeIt(stack.getRoot(), "valueStack", writer, new ArrayList<Object>());
        writer.endNode();
    }


    /**
     * Recursive function to serialize objects to XML. Currently it will
     * serialize Collections, maps, Arrays, and JavaBeans. It maintains a stack
     * of objects serialized already in the current functioncall. This is used
     * to avoid looping (stack overflow) of circular linked objects. Struts and
     * XWork objects are ignored.
     *
     * @param bean   The object you want serialized.
     * @param name   The name of the object, used for element &lt;name/&gt;
     * @param writer The XML writer
     * @param stack  List of objects we're serializing since the first calling
     *               of this function (to prevent looping on circular references).
     */
    protected void serializeIt(Object bean, String name,
                               PrettyPrintWriter writer, List<Object> stack) {
        writer.flush();
        // Check stack for this object
        if ((bean != null) && (stack.contains(bean))) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Circular reference detected, not serializing object: "
                        + name);
            }
            return;
        } else if (bean != null) {
            // Push object onto stack.
            // Don't push null objects ( handled below)
            stack.add(bean);
        }
        if (bean == null) {
            return;
        }
        String clsName = bean.getClass().getName();

        writer.startNode(name);

        // It depends on the object and it's value what todo next:
        if (bean instanceof Collection) {
            Collection col = (Collection) bean;

            // Iterate through components, and call ourselves to process
            // elements
            for (Object aCol : col) {
                serializeIt(aCol, "value", writer, stack);
            }
        } else if (bean instanceof Map) {

            Map<Object, Object> map = (Map) bean;

            // Loop through keys and call ourselves
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                Object objValue = entry.getValue();
                serializeIt(objValue, entry.getKey().toString(), writer, stack);
            }
        } else if (bean.getClass().isArray()) {
            // It's an array, loop through it and keep calling ourselves
            for (int i = 0; i < Array.getLength(bean); i++) {
                serializeIt(Array.get(bean, i), "arrayitem", writer, stack);
            }
        } else {
            if (clsName.startsWith("java.lang")) {
                writer.setValue(bean.toString());
            } else {
                // Not java.lang, so we can call ourselves with this object's
                // values
                try {
                    BeanInfo info = Introspector.getBeanInfo(bean.getClass());
                    PropertyDescriptor[] props = info.getPropertyDescriptors();

                    for (PropertyDescriptor prop : props) {
                        String n = prop.getName();
                        Method m = prop.getReadMethod();

                        // Call ourselves with the result of the method
                        // invocation
                        if (m != null) {
                            serializeIt(m.invoke(bean), n, writer, stack);
                        }
                    }
                } catch (Exception e) {
                    LOG.error(e.toString(), e);
                }
            }
        }

        writer.endNode();

        // Remove object from stack
        stack.remove(bean);
    }


    /**
     * @param enableXmlWithConsole the enableXmlWithConsole to set
     */
    public void setEnableXmlWithConsole(boolean enableXmlWithConsole) {
        this.enableXmlWithConsole = enableXmlWithConsole;
    }

    
    private List<Object> filterValueStack(Map requestMap) {
    	List<Object> filter = new ArrayList<Object>();
    	Object valueStack = requestMap.get("struts.valueStack");
    	if(valueStack != null) {
    		filter.add(valueStack);
    	}
    	return filter;
    }


}


