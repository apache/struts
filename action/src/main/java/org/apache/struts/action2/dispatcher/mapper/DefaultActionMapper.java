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
package org.apache.struts.action2.dispatcher.mapper;

import org.apache.struts.action2.RequestUtils;
import org.apache.struts.action2.StrutsConstants;
import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.dispatcher.ServletRedirectResult;
import org.apache.struts.action2.util.PrefixTrie;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Default action mapper implementation, using the standard *.[ext] (where ext usually "action") pattern. The extension
 * is looked up from the Struts configuration key <b>struts.action.exection</b>.
 *
 * <p/> To help with dealing with buttons and other related requirements, this mapper (and other {@link ActionMapper}s,
 * we hope) has the ability to name a button with some predefined prefix and have that button name alter the execution
 * behaviour. The four prefixes are:
 *
 * <ul>
 *
 * <li>Method prefix - <i>method:default</i></li>
 *
 * <li>Action prefix - <i>action:dashboard</i></li>
 *
 * <li>Redirect prefix - <i>redirect:cancel.jsp</i></li>
 *
 * <li>Redirect-action prefix - <i>redirect-action:cancel</i></li>
 *
 * </ul>
 *
 * <p/> In addition to these four prefixes, this mapper also understands the action naming pattern of <i>foo!bar</i> in
 * either the extension form (eg: foo!bar.action) or in the prefix form (eg: action:foo!bar). This syntax tells this mapper
 * to map to the action named <i>foo</i> and the method <i>bar</i>.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Method Prefix</b> <p/>
 *
 * <!-- START SNIPPET: method -->
 *
 * With method-prefix, instead of calling baz action's execute() method (by default if it isn't overriden in xwork.xml
 * to be something else), the baz action's anotherMethod() will be called. A very elegant way determine which button is
 * clicked. Alternatively, one would have submit button set a particular value on the action when clicked, and the
 * execute() method decides on what to do with the setted value depending on which button is clicked.
 *
 * <!-- END SNIPPET: method -->
 *
 * <pre>
 * <!-- START SNIPPET: method-example -->
 * &lt;a:form name="baz"&gt;
 *     &lt;a:textfield label="Enter your name" name="person.name"/&gt;
 *     &lt;a:submit value="Create person"/&gt;
 *     &lt;a:submit name="method:anotherMethod" value="Cancel"/&gt;
 * &lt;/a:form&gt;
 * <!-- END SNIPPET: method-example -->
 * </pre>
 *
 * <p/> <b>Action prefix</b> <p/>
 *
 * <!-- START SNIPPET: action -->
 *
 * With action-prefix, instead of executing baz action's execute() method (by default if it isn't overriden in xwork.xml
 * to be something else), the anotherAction action's execute() method (assuming again if it isn't overriden with
 * something else in xwork.xml) will be executed.
 *
 * <!-- END SNIPPET: action -->
 *
 * <pre>
 * <!-- START SNIPPET: action-example -->
 * &lt;a:form name="baz"&gt;
 *     &lt;a:textfield label="Enter your name" name="person.name"/&gt;
 *     &lt;a:submit value="Create person"/&gt;
 *     &lt;a:submit name="action:anotherAction" value="Cancel"/&gt;
 * &lt;/a:form&gt;
 * <!-- END SNIPPET: action-example -->
 * </pre>
 *
 * <p/> <b>Redirect prefix</b> <p/>
 *
 * <!-- START SNIPPET: redirect -->
 *
 * With redirect-prefix, instead of executing baz action's execute() method (by default it isn't overriden in xwork.xml
 * to be something else), it will get redirected to, in this case to www.google.com. Internally it uses
 * ServletRedirectResult to do the task.
 *
 * <!-- END SNIPPET: redirect -->
 *
 * <pre>
 * <!-- START SNIPPET: redirect-example -->
 * &lt;a:form name="baz"&gt;
 *     &lt;a:textfield label="Enter your name" name="person.name"/&gt;
 *     &lt;a:submit value="Create person"/&gt;
 *     &lt;a:submit name="redirect:www.google.com" value="Cancel"/&gt;
 * &lt;/a:form&gt;
 * <!-- END SNIPPET: redirect-example -->
 * </pre>
 *
 * <p/> <b>Redirect-action prefix</b> <p/>
 *
 * <!-- START SNIPPET: redirect-action -->
 *
 * With redirect-action-prefix, instead of executing baz action's execute() method (by default it isn't overriden in
 * xwork.xml to be something else), it will get redirected to, in this case 'dashboard.action'. Internally it uses
 * ServletRedirectResult to do the task and read off the extension from the struts.properties.
 *
 * <!-- END SNIPPET: redirect-action -->
 *
 * <pre>
 * <!-- START SNIPPET: redirect-action-example -->
 * &lt;a:form name="baz"&gt;
 *     &lt;a:textfield label="Enter your name" name="person.name"/&gt;
 *     &lt;a:submit value="Create person"/&gt;
 *     &lt;a:submit name="redirect-action:dashboard" value="Cancel"/&gt;
 * &lt;/a:form&gt;
 * <!-- END SNIPPET: redirect-action-example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author tm_jee
 */
public class DefaultActionMapper implements ActionMapper {

    static final String METHOD_PREFIX = "method:";
    static final String ACTION_PREFIX = "action:";
    static final String REDIRECT_PREFIX = "redirect:";
    static final String REDIRECT_ACTION_PREFIX = "redirect-action:";

    static PrefixTrie prefixTrie = new PrefixTrie() {
        {
            put(METHOD_PREFIX, new ParameterAction() {
                public void execute(String key, ActionMapping mapping) {
                    mapping.setMethod(key.substring(METHOD_PREFIX.length()));
                }
            });

            put(ACTION_PREFIX, new ParameterAction() {
                public void execute(String key, ActionMapping mapping) {
                    String name = key.substring(ACTION_PREFIX.length());
                    int bang = name.indexOf('!');
                    if (bang != -1) {
                        String method = name.substring(bang + 1);
                        mapping.setMethod(method);
                        name = name.substring(0, bang);
                    }
                    
                    mapping.setName(name);
                }
            });

            put(REDIRECT_PREFIX, new ParameterAction() {
                public void execute(String key, ActionMapping mapping) {
                    ServletRedirectResult redirect = new ServletRedirectResult();
                    redirect.setLocation(key.substring(REDIRECT_PREFIX.length()));
                    mapping.setResult(redirect);
                }
            });

            put(REDIRECT_ACTION_PREFIX, new ParameterAction() {
                public void execute(String key, ActionMapping mapping) {
                    String location = key.substring(REDIRECT_ACTION_PREFIX.length());
                    ServletRedirectResult redirect = new ServletRedirectResult();
                    String extension = getDefaultExtension();
                    if (extension != null) {
                        location += "." + extension;
                    }
                    redirect.setLocation(location);
                    mapping.setResult(redirect);
                }
            });
        }
    };

    public ActionMapping getMapping(HttpServletRequest request) {
        ActionMapping mapping = new ActionMapping();
        String uri = getUri(request);

        parseNameAndNamespace(uri, mapping);

        handleSpecialParameters(request, mapping);

        if (mapping.getName() == null) {
            return null;
        }

        // handle "name!method" convention.
        String name = mapping.getName();
        int exclamation = name.lastIndexOf("!");
        if (exclamation != -1) {
            mapping.setName(name.substring(0, exclamation));
            mapping.setMethod(name.substring(exclamation + 1));
        }
        return mapping;
    }

    public static void handleSpecialParameters(HttpServletRequest request, ActionMapping mapping) {
        // handle special parameter prefixes.
        Map parameterMap = request.getParameterMap();
        for (Iterator iterator = parameterMap.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            ParameterAction parameterAction = (ParameterAction) prefixTrie.get(key);
            if (parameterAction != null) {
                parameterAction.execute(key, mapping);
                break;
            }
        }
    }

    void parseNameAndNamespace(String uri, ActionMapping mapping) {
        String namespace, name;
        int lastSlash = uri.lastIndexOf("/");
        if (lastSlash == -1) {
            namespace = "";
            name = uri;
        } else if (lastSlash == 0) {
            // ww-1046, assume it is the root namespace, it will fallback to default
            // namespace anyway if not found in root namespace.
            namespace = "/";
            name = uri.substring(lastSlash + 1);
        } else {
            namespace = uri.substring(0, lastSlash);
            name = uri.substring(lastSlash + 1);
        }
        mapping.setNamespace(namespace);
        mapping.setName(dropExtension(name));
    }

    String dropExtension(String name) {
    		List extensions = getExtensions();
		if (extensions == null) {
		    return name;
		}
        	Iterator it = extensions.iterator();
        	while (it.hasNext()) {
        		String extension = "." + (String) it.next();
        		if ( name.endsWith(extension)) {
        			name = name.substring(0, name.length() - extension.length());
        			return name;
        		}
        	}
        	return null;
    }

    /**
     * Returns null if no extension is specified.
     */
    static String getDefaultExtension() {
        List extensions = getExtensions();
        if (extensions == null) {
        	return null;
        } else {
        	return (String) extensions.get(0);
        }
    }
    
    /**
     * Returns null if no extension is specified.
     */
    static List getExtensions() {
        String extensions = (String) Configuration.get(StrutsConstants.STRUTS_ACTION_EXTENSION);

        if (extensions == null) {
            System.out.println(Configuration.getConfiguration().getClass());
        }

        if ("".equals(extensions)) {
        	return null;
        } else {
        	return Arrays.asList(extensions.split(","));        	
        } 
    }

    String getUri(HttpServletRequest request) {
        // handle http dispatcher includes.
        String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");
        if (uri != null) {
            return uri;
        }

        uri = RequestUtils.getServletPath(request);
        if (uri != null && !"".equals(uri)) {
            return uri;
        }

        uri = request.getRequestURI();
        return uri.substring(request.getContextPath().length());
    }

    public String getUriFromActionMapping(ActionMapping mapping) {
        StringBuffer uri = new StringBuffer();

        uri.append(mapping.getNamespace());
        if(!"/".equals(mapping.getNamespace())) {
            uri.append("/");
        }
        String name = mapping.getName();
        String params = "";
        if ( name.indexOf('?') != -1) {
            params = name.substring(name.indexOf('?'));
            name = name.substring(0, name.indexOf('?'));
        }
        uri.append(name);

        if (null != mapping.getMethod() && !"".equals(mapping.getMethod())) {
            uri.append("!").append(mapping.getMethod());
        }

        String extension = getDefaultExtension();
        if ( extension != null && uri.indexOf( '.' + extension) == -1  ) {
            uri.append(".").append(extension);
            if ( params.length() > 0) {
                uri.append(params);
            }
        }

        return uri.toString();
    }

    interface ParameterAction {
        void execute(String key, ActionMapping mapping);
    }
}
