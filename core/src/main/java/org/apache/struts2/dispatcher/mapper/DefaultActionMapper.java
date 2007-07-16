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
package org.apache.struts2.dispatcher.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.apache.struts2.util.PrefixTrie;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Container;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Default action mapper implementation, using the standard *.[ext] (where ext
 * usually "action") pattern. The extension is looked up from the Struts
 * configuration key <b>struts.action.extension</b>.
 *
 * <p/> To help with dealing with buttons and other related requirements, this
 * mapper (and other {@link ActionMapper}s, we hope) has the ability to name a
 * button with some predefined prefix and have that button name alter the
 * execution behaviour. The four prefixes are:
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
 * <p/> In addition to these four prefixes, this mapper also understands the
 * action naming pattern of <i>foo!bar</i> in either the extension form (eg:
 * foo!bar.action) or in the prefix form (eg: action:foo!bar). This syntax tells
 * this mapper to map to the action named <i>foo</i> and the method <i>bar</i>.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Method Prefix</b> <p/>
 *
 * <!-- START SNIPPET: method -->
 *
 * With method-prefix, instead of calling baz action's execute() method (by
 * default if it isn't overriden in struts.xml to be something else), the baz
 * action's anotherMethod() will be called. A very elegant way determine which
 * button is clicked. Alternatively, one would have submit button set a
 * particular value on the action when clicked, and the execute() method decides
 * on what to do with the setted value depending on which button is clicked.
 *
 * <!-- END SNIPPET: method -->
 *
 * <pre>
 *  &lt;!-- START SNIPPET: method-example --&gt;
 *  &lt;s:form action=&quot;baz&quot;&gt;
 *      &lt;s:textfield label=&quot;Enter your name&quot; name=&quot;person.name&quot;/&gt;
 *      &lt;s:submit value=&quot;Create person&quot;/&gt;
 *      &lt;s:submit name=&quot;method:anotherMethod&quot; value=&quot;Cancel&quot;/&gt;
 *  &lt;/s:form&gt;
 *  &lt;!-- END SNIPPET: method-example --&gt;
 * </pre>
 *
 * <p/> <b>Action prefix</b> <p/>
 *
 * <!-- START SNIPPET: action -->
 *
 * With action-prefix, instead of executing baz action's execute() method (by
 * default if it isn't overriden in struts.xml to be something else), the
 * anotherAction action's execute() method (assuming again if it isn't overriden
 * with something else in struts.xml) will be executed.
 *
 * <!-- END SNIPPET: action -->
 *
 * <pre>
 *  &lt;!-- START SNIPPET: action-example --&gt;
 *  &lt;s:form action=&quot;baz&quot;&gt;
 *      &lt;s:textfield label=&quot;Enter your name&quot; name=&quot;person.name&quot;/&gt;
 *      &lt;s:submit value=&quot;Create person&quot;/&gt;
 *      &lt;s:submit name=&quot;action:anotherAction&quot; value=&quot;Cancel&quot;/&gt;
 *  &lt;/s:form&gt;
 *  &lt;!-- END SNIPPET: action-example --&gt;
 * </pre>
 *
 * <p/> <b>Redirect prefix</b> <p/>
 *
 * <!-- START SNIPPET: redirect -->
 *
 * With redirect-prefix, instead of executing baz action's execute() method (by
 * default it isn't overriden in struts.xml to be something else), it will get
 * redirected to, in this case to www.google.com. Internally it uses
 * ServletRedirectResult to do the task.
 *
 * <!-- END SNIPPET: redirect -->
 *
 * <pre>
 *  &lt;!-- START SNIPPET: redirect-example --&gt;
 *  &lt;s:form action=&quot;baz&quot;&gt;
 *      &lt;s:textfield label=&quot;Enter your name&quot; name=&quot;person.name&quot;/&gt;
 *      &lt;s:submit value=&quot;Create person&quot;/&gt;
 *      &lt;s:submit name=&quot;redirect:www.google.com&quot; value=&quot;Cancel&quot;/&gt;
 *  &lt;/s:form&gt;
 *  &lt;!-- END SNIPPET: redirect-example --&gt;
 * </pre>
 *
 * <p/> <b>Redirect-action prefix</b> <p/>
 *
 * <!-- START SNIPPET: redirect-action -->
 *
 * With redirect-action-prefix, instead of executing baz action's execute()
 * method (by default it isn't overriden in struts.xml to be something else), it
 * will get redirected to, in this case 'dashboard.action'. Internally it uses
 * ServletRedirectResult to do the task and read off the extension from the
 * struts.properties.
 *
 * <!-- END SNIPPET: redirect-action -->
 *
 * <pre>
 *  &lt;!-- START SNIPPET: redirect-action-example --&gt;
 *  &lt;s:form action=&quot;baz&quot;&gt;
 *      &lt;s:textfield label=&quot;Enter your name&quot; name=&quot;person.name&quot;/&gt;
 *      &lt;s:submit value=&quot;Create person&quot;/&gt;
 *      &lt;s:submit name=&quot;redirect-action:dashboard&quot; value=&quot;Cancel&quot;/&gt;
 *  &lt;/s:form&gt;
 *  &lt;!-- END SNIPPET: redirect-action-example --&gt;
 * </pre>
 *
 */
public class DefaultActionMapper implements ActionMapper {

    static final String METHOD_PREFIX = "method:";

    static final String ACTION_PREFIX = "action:";

    static final String REDIRECT_PREFIX = "redirect:";

    static final String REDIRECT_ACTION_PREFIX = "redirect-action:";

    private boolean allowDynamicMethodCalls = true;

    private boolean allowSlashesInActionNames = false;
    
    private boolean alwaysSelectFullNamespace = false;

    private PrefixTrie prefixTrie = null;
    
    List extensions = new ArrayList() {{ add("action");}};

    private Container container;

    public DefaultActionMapper() {
        prefixTrie = new PrefixTrie() {
            {
                put(METHOD_PREFIX, new ParameterAction() {
                    public void execute(String key, ActionMapping mapping) {
                        mapping
                                .setMethod(key
                                        .substring(METHOD_PREFIX.length()));
                    }
                });

                put(ACTION_PREFIX, new ParameterAction() {
                    public void execute(String key, ActionMapping mapping) {
                        String name = key.substring(ACTION_PREFIX.length());
                        if (allowDynamicMethodCalls) {
                            int bang = name.indexOf('!');
                            if (bang != -1) {
                                String method = name.substring(bang + 1);
                                mapping.setMethod(method);
                                name = name.substring(0, bang);
                            }
                        }
                        mapping.setName(name);
                    }
                });

                put(REDIRECT_PREFIX, new ParameterAction() {
                    public void execute(String key, ActionMapping mapping) {
                        ServletRedirectResult redirect = new ServletRedirectResult();
                        container.inject(redirect);
                        redirect.setLocation(key.substring(REDIRECT_PREFIX
                                .length()));
                        mapping.setResult(redirect);
                    }
                });

                put(REDIRECT_ACTION_PREFIX, new ParameterAction() {
                    public void execute(String key, ActionMapping mapping) {
                        String location = key.substring(REDIRECT_ACTION_PREFIX
                                .length());
                        ServletRedirectResult redirect = new ServletRedirectResult();
                        container.inject(redirect);
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
    }

    /**
     * Adds a parameter action.  Should only be called during initialization
     *
     * @param prefix The string prefix to trigger the action
     * @param parameterAction The parameter action to execute
     * @since 2.1.0
    */
    protected void addParameterAction(String prefix, ParameterAction parameterAction) {
        prefixTrie.put(prefix, parameterAction);
    }
    
    @Inject(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION)
    public void setAllowDynamicMethodCalls(String allow) {
        allowDynamicMethodCalls = "true".equals(allow);
    }
    
    @Inject(StrutsConstants.STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES)
    public void setSlashesInActionNames(String allow) {
        allowSlashesInActionNames = "true".equals(allow);
    }
    
    @Inject(StrutsConstants.STRUTS_ALWAYS_SELECT_FULL_NAMESPACE)
    public void setAlwaysSelectFullNamespace(String val) {
        this.alwaysSelectFullNamespace = "true".equals(val);
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.struts2.dispatcher.mapper.ActionMapper#getMapping(javax.servlet.http.HttpServletRequest)
     */
    public ActionMapping getMapping(HttpServletRequest request,
            ConfigurationManager configManager) {
        ActionMapping mapping = new ActionMapping();
        String uri = getUri(request);

        uri = dropExtension(uri);
        if (uri == null) {
            return null;
        }

        parseNameAndNamespace(uri, mapping, configManager);

        handleSpecialParameters(request, mapping);

        if (mapping.getName() == null) {
            return null;
        }

        if (allowDynamicMethodCalls) {
            // handle "name!method" convention.
            String name = mapping.getName();
            int exclamation = name.lastIndexOf("!");
            if (exclamation != -1) {
                mapping.setName(name.substring(0, exclamation));
                mapping.setMethod(name.substring(exclamation + 1));
            }
        }

        return mapping;
    }

    /**
     * Special parameters, as described in the class-level comment, are searched
     * for and handled.
     *
     * @param request
     *            The request
     * @param mapping
     *            The action mapping
     */
    public void handleSpecialParameters(HttpServletRequest request,
            ActionMapping mapping) {
        // handle special parameter prefixes.
        Set<String> uniqueParameters = new HashSet<String>();
        Map parameterMap = request.getParameterMap();
        for (Iterator iterator = parameterMap.keySet().iterator(); iterator
                .hasNext();) {
            String key = (String) iterator.next();
            
            // Strip off the image button location info, if found
            if (key.endsWith(".x") || key.endsWith(".y")) {
                key = key.substring(0, key.length() - 2);
            }
            
            // Ensure a parameter doesn't get processed twice
            if (!uniqueParameters.contains(key)) {
                ParameterAction parameterAction = (ParameterAction) prefixTrie
                        .get(key);
                if (parameterAction != null) {
                    parameterAction.execute(key, mapping);
                    uniqueParameters.add(key);
                    break;
                }
            }
        }
    }

    /**
     * Parses the name and namespace from the uri
     *
     * @param uri
     *            The uri
     * @param mapping
     *            The action mapping to populate
     */
    void parseNameAndNamespace(String uri, ActionMapping mapping,
            ConfigurationManager configManager) {
        String namespace, name;
        int lastSlash = uri.lastIndexOf("/");
        if (lastSlash == -1) {
            namespace = "";
            name = uri;
        } else if (lastSlash == 0) {
            // ww-1046, assume it is the root namespace, it will fallback to
            // default
            // namespace anyway if not found in root namespace.
            namespace = "/";
            name = uri.substring(lastSlash + 1);
        } else if (alwaysSelectFullNamespace) {
            // Simply select the namespace as everything before the last slash
            namespace = uri.substring(0, lastSlash);
            name = uri.substring(lastSlash + 1);
        } else {
            // Try to find the namespace in those defined, defaulting to ""
            Configuration config = configManager.getConfiguration();
            String prefix = uri.substring(0, lastSlash);
            namespace = "";
            // Find the longest matching namespace, defaulting to the default
            for (Iterator i = config.getPackageConfigs().values().iterator(); i
                    .hasNext();) {
                String ns = ((PackageConfig) i.next()).getNamespace();
                if (ns != null && prefix.startsWith(ns) && (prefix.length() == ns.length() || prefix.charAt(ns.length()) == '/')) {
                    if (ns.length() > namespace.length()) {
                        namespace = ns;
                    }
                }
            }

            name = uri.substring(namespace.length() + 1);
        }

        if (!allowSlashesInActionNames && name != null) {
            int pos = name.lastIndexOf('/');
            if (pos > -1 && pos < name.length() - 1) {
                name = name.substring(pos + 1);
            }
        }

        mapping.setNamespace(namespace);
        mapping.setName(name);
    }

    /**
     * Drops the extension from the action name
     *
     * @param name
     *            The action name
     * @return The action name without its extension
     */
    String dropExtension(String name) {
        if (extensions == null) {
            return name;
        }
        Iterator it = extensions.iterator();
        while (it.hasNext()) {
            String extension = "." + (String) it.next();
            if (name.endsWith(extension)) {
                name = name.substring(0, name.length() - extension.length());
                return name;
            }
        }
        return null;
    }

    /**
     * Returns null if no extension is specified.
     */
    String getDefaultExtension() {
        if (extensions == null) {
            return null;
        } else {
            return (String) extensions.get(0);
        }
    }

    @Inject(StrutsConstants.STRUTS_ACTION_EXTENSION)
    public void setExtensions(String extensions) {
        if (!"".equals(extensions)) {
            this.extensions = Arrays.asList(extensions.split(","));
        } else {
            this.extensions = null;
        }
    }
    
    /**
     * Gets the uri from the request
     *
     * @param request
     *            The request
     * @return The uri
     */
    String getUri(HttpServletRequest request) {
        // handle http dispatcher includes.
        String uri = (String) request
                .getAttribute("javax.servlet.include.servlet_path");
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

    /*
     * (non-Javadoc)
     *
     * @see org.apache.struts2.dispatcher.mapper.ActionMapper#getUriFromActionMapping(org.apache.struts2.dispatcher.mapper.ActionMapping)
     */
    public String getUriFromActionMapping(ActionMapping mapping) {
        StringBuffer uri = new StringBuffer();

        uri.append(mapping.getNamespace());
        if (!"/".equals(mapping.getNamespace())) {
            uri.append("/");
        }
        String name = mapping.getName();
        String params = "";
        if (name.indexOf('?') != -1) {
            params = name.substring(name.indexOf('?'));
            name = name.substring(0, name.indexOf('?'));
        }
        uri.append(name);

        if (null != mapping.getMethod() && !"".equals(mapping.getMethod())) {
            uri.append("!").append(mapping.getMethod());
        }

        String extension = getDefaultExtension();
        if (extension != null) {
            if (uri.indexOf('.' + extension) == -1) {
                uri.append(".").append(extension);
                if (params.length() > 0) {
                    uri.append(params);
                }
            }
        }

        return uri.toString();
    }
    

	public boolean isSlashesInActionNames() {
		return allowSlashesInActionNames;
	}
	
}
