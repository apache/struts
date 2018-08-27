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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.util.PrefixTrie;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * Default action mapper implementation, using the standard *.[ext] (where ext
 * usually "action") pattern. The extension is looked up from the Struts
 * configuration key <b>struts.action.extension</b>.
 * <p/>
 * <p/> To help with dealing with buttons and other related requirements, this
 * mapper (and other {@link ActionMapper}s, we hope) has the ability to name a
 * button with some predefined prefix and have that button name alter the
 * execution behaviour. The four prefixes are:
 * <p/>
 * <ul>
 * <li>Method prefix - <i>method:default</i></li>
 * <li>Action prefix - <i>action:dashboard</i></li>
 * </ul>
 * <p/>
 * In addition to these four prefixes, this mapper also understands the
 * action naming pattern of <i>foo!bar</i> in either the extension form (eg:
 * foo!bar.action) or in the prefix form (eg: action:foo!bar). This syntax tells
 * this mapper to map to the action named <i>foo</i> and the method <i>bar</i>.
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 * <b>Method Prefix</b>
 * <!-- START SNIPPET: method -->
 * <p/>
 * With method-prefix, instead of calling baz action's execute() method (by
 * default if it isn't overriden in struts.xml to be something else), the baz
 * action's anotherMethod() will be called. A very elegant way determine which
 * button is clicked. Alternatively, one would have submit button set a
 * particular value on the action when clicked, and the execute() method decides
 * on what to do with the setted value depending on which button is clicked.
 * <p/>
 * <!-- END SNIPPET: method -->
 * <p/>
 * <pre>
 *  &lt;!-- START SNIPPET: method-example --&gt;
 *  &lt;s:form action=&quot;baz&quot;&gt;
 *      &lt;s:textfield label=&quot;Enter your name&quot; name=&quot;person.name&quot;/&gt;
 *      &lt;s:submit value=&quot;Create person&quot;/&gt;
 *      &lt;s:submit method=&quot;anotherMethod&quot; value=&quot;Cancel&quot;/&gt;
 *  &lt;/s:form&gt;
 *  &lt;!-- END SNIPPET: method-example --&gt;
 * </pre>
 * <b>Action prefix</b>
 * <!-- START SNIPPET: action -->
 * <p/>
 * With action-prefix, instead of executing baz action's execute() method (by
 * default if it isn't overriden in struts.xml to be something else), the
 * anotherAction action's execute() method (assuming again if it isn't overriden
 * with something else in struts.xml) will be executed.
 * <p/>
 * <!-- END SNIPPET: action -->
 * <p/>
 * <pre>
 *  &lt;!-- START SNIPPET: action-example --&gt;
 *  &lt;s:form action=&quot;baz&quot;&gt;
 *      &lt;s:textfield label=&quot;Enter your name&quot; name=&quot;person.name&quot;/&gt;
 *      &lt;s:submit value=&quot;Create person&quot;/&gt;
 *      &lt;s:submit action=&quot;anotherAction&quot; value=&quot;Cancel&quot;/&gt;
 *  &lt;/s:form&gt;
 *  &lt;!-- END SNIPPET: action-example --&gt;
 * </pre>
 */
public class DefaultActionMapper implements ActionMapper {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultActionMapper.class);

    protected static final String METHOD_PREFIX = "method:";
    protected static final String ACTION_PREFIX = "action:";

    protected boolean allowDynamicMethodCalls = false;
    protected boolean allowSlashesInActionNames = false;
    protected boolean alwaysSelectFullNamespace = false;
    protected PrefixTrie prefixTrie = null;

    protected Pattern allowedNamespaceNames = Pattern.compile("[a-zA-Z0-9._/\\-]*");
    protected String defaultNamespaceName = "/";

    protected Pattern allowedActionNames = Pattern.compile("[a-zA-Z0-9._!/\\-]*");
    protected String defaultActionName = "index";

    protected Pattern allowedMethodNames = Pattern.compile("[a-zA-Z_]*[0-9]*");
    protected String defaultMethodName = "execute";

    private boolean allowActionPrefix = false;
    private boolean allowActionCrossNamespaceAccess = false;

    protected List<String> extensions = new ArrayList<String>() {{
        add("action");
        add("");
    }};

    protected Container container;

    public DefaultActionMapper() {
        prefixTrie = new PrefixTrie() {
            {
                put(METHOD_PREFIX, new ParameterAction() {
                    public void execute(String key, ActionMapping mapping) {
                        if (allowDynamicMethodCalls) {
                            mapping.setMethod(cleanupMethodName(key.substring(METHOD_PREFIX.length())));
                        }
                    }
                });

                put(ACTION_PREFIX, new ParameterAction() {
                    public void execute(final String key, ActionMapping mapping) {
                        if (allowActionPrefix) {
                            String name = key.substring(ACTION_PREFIX.length());
                            if (allowDynamicMethodCalls) {
                                int bang = name.indexOf('!');
                                if (bang != -1) {
                                    String method = cleanupMethodName(name.substring(bang + 1));
                                    mapping.setMethod(method);
                                    name = name.substring(0, bang);
                                }
                            }
                            String actionName = cleanupActionName(name);
                            if (allowSlashesInActionNames && !allowActionCrossNamespaceAccess) {
                                if (actionName.startsWith("/")) {
                                    actionName = actionName.substring(1);
                                }
                            }
                            if (!allowSlashesInActionNames && !allowActionCrossNamespaceAccess) {
                                if (actionName.lastIndexOf('/') != -1) {
                                    actionName = actionName.substring(actionName.lastIndexOf('/') + 1);
                                }
                            }
                            mapping.setName(actionName);
                        }
                    }
                });

            }
        };
    }

    /**
     * Adds a parameter action.  Should only be called during initialization
     *
     * @param prefix          The string prefix to trigger the action
     * @param parameterAction The parameter action to execute
     * @since 2.1.0
     */
    protected void addParameterAction(String prefix, ParameterAction parameterAction) {
        prefixTrie.put(prefix, parameterAction);
    }

    @Inject(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION)
    public void setAllowDynamicMethodCalls(String allow) {
        allowDynamicMethodCalls = "true".equalsIgnoreCase(allow);
    }

    @Inject(StrutsConstants.STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES)
    public void setSlashesInActionNames(String allow) {
        allowSlashesInActionNames = "true".equals(allow);
    }

    @Inject(StrutsConstants.STRUTS_ALWAYS_SELECT_FULL_NAMESPACE)
    public void setAlwaysSelectFullNamespace(String val) {
        this.alwaysSelectFullNamespace = "true".equals(val);
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOWED_NAMESPACE_NAMES, required = false)
    public void setAllowedNamespaceNames(String allowedNamespaceNames) {
        this.allowedNamespaceNames = Pattern.compile(allowedNamespaceNames);
    }

    @Inject(value = StrutsConstants.STRUTS_DEFAULT_NAMESPACE_NAME, required = false)
    public void setDefaultNamespaceName(String defaultNamespaceName) {
        this.defaultNamespaceName = defaultNamespaceName;
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOWED_ACTION_NAMES, required = false)
    public void setAllowedActionNames(String allowedActionNames) {
        this.allowedActionNames = Pattern.compile(allowedActionNames);
    }

    @Inject(value = StrutsConstants.STRUTS_DEFAULT_ACTION_NAME, required = false)
    public void setDefaultActionName(String defaultActionName) {
        this.defaultActionName = defaultActionName;
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOWED_METHOD_NAMES, required = false)
    public void setAllowedMethodNames(String allowedMethodNames) {
        this.allowedMethodNames = Pattern.compile(allowedMethodNames);
    }

    @Inject(value = StrutsConstants.STRUTS_DEFAULT_METHOD_NAME, required = false)
    public void setDefaultMethodName(String defaultMethodName) {
        this.defaultMethodName = defaultMethodName;
    }

    @Inject(value = StrutsConstants.STRUTS_MAPPER_ACTION_PREFIX_ENABLED)
    public void setAllowActionPrefix(String allowActionPrefix) {
        this.allowActionPrefix = "true".equalsIgnoreCase(allowActionPrefix);
    }

    @Inject(value = StrutsConstants.STRUTS_MAPPER_ACTION_PREFIX_CROSSNAMESPACES)
    public void setAllowActionCrossNamespaceAccess(String allowActionCrossNamespaceAccess) {
        this.allowActionCrossNamespaceAccess = "true".equalsIgnoreCase(allowActionCrossNamespaceAccess);
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(StrutsConstants.STRUTS_ACTION_EXTENSION)
    public void setExtensions(String extensions) {
        if (extensions != null && !"".equals(extensions)) {
            List<String> list = new ArrayList<String>();
            String[] tokens = extensions.split(",");
            Collections.addAll(list, tokens);
            if (extensions.endsWith(",")) {
                list.add("");
            }
            this.extensions = Collections.unmodifiableList(list);
        } else {
            this.extensions = null;
        }
    }

    public ActionMapping getMappingFromActionName(String actionName) {
        ActionMapping mapping = new ActionMapping();
        mapping.setName(actionName);
        return parseActionName(mapping);
    }

    public boolean isSlashesInActionNames() {
        return allowSlashesInActionNames;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.struts2.dispatcher.mapper.ActionMapper#getMapping(javax.servlet.http.HttpServletRequest)
     */
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        ActionMapping mapping = new ActionMapping();
        String uri = RequestUtils.getUri(request);

        int indexOfSemicolon = uri.indexOf(';');
        uri = (indexOfSemicolon > -1) ? uri.substring(0, indexOfSemicolon) : uri;

        uri = dropExtension(uri, mapping);
        if (uri == null) {
            return null;
        }

        parseNameAndNamespace(uri, mapping, configManager);
        handleSpecialParameters(request, mapping);
        return parseActionName(mapping);
    }

    protected ActionMapping parseActionName(ActionMapping mapping) {
        if (mapping.getName() == null) {
            return null;
        }
        if (allowDynamicMethodCalls) {
            // handle "name!method" convention.
            String name = mapping.getName();
            int exclamation = name.lastIndexOf('!');
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
     * @param request The request
     * @param mapping The action mapping
     */
    public void handleSpecialParameters(HttpServletRequest request, ActionMapping mapping) {
        // handle special parameter prefixes.
        Set<String> uniqueParameters = new HashSet<String>();
        Map parameterMap = request.getParameterMap();
        for (Object o : parameterMap.keySet()) {
            String key = (String) o;

            // Strip off the image button location info, if found
            if (key.endsWith(".x") || key.endsWith(".y")) {
                key = key.substring(0, key.length() - 2);
            }

            // Ensure a parameter doesn't get processed twice
            if (!uniqueParameters.contains(key)) {
                ParameterAction parameterAction = (ParameterAction) prefixTrie.get(key);
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
     * @param uri     The uri
     * @param mapping The action mapping to populate
     */
    protected void parseNameAndNamespace(String uri, ActionMapping mapping, ConfigurationManager configManager) {
        String namespace, name;
        int lastSlash = uri.lastIndexOf('/');
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
            boolean rootAvailable = false;
            // Find the longest matching namespace, defaulting to the default
            for (PackageConfig cfg : config.getPackageConfigs().values()) {
                String ns = cfg.getNamespace();
                if (ns != null && prefix.startsWith(ns) && (prefix.length() == ns.length() || prefix.charAt(ns.length()) == '/')) {
                    if (ns.length() > namespace.length()) {
                        namespace = ns;
                    }
                }
                if ("/".equals(ns)) {
                    rootAvailable = true;
                }
            }

            name = uri.substring(namespace.length() + 1);

            // Still none found, use root namespace if found
            if (rootAvailable && "".equals(namespace)) {
                namespace = "/";
            }
        }

        if (!allowSlashesInActionNames) {
            int pos = name.lastIndexOf('/');
            if (pos > -1 && pos < name.length() - 1) {
                name = name.substring(pos + 1);
            }
        }

        mapping.setNamespace(cleanupNamespaceName(namespace));
        mapping.setName(cleanupActionName(name));
    }

    /**
     * Checks namespace name against allowed pattern if not matched returns default namespace
     *
     * @param rawNamespace name extracted from URI
     * @return safe namespace name
     */
    protected String cleanupNamespaceName(final String rawNamespace) {
        if (allowedNamespaceNames.matcher(rawNamespace).matches()) {
            return rawNamespace;
        } else {
            LOG.warn(
                "{} did not match allowed namespace names {} - default namespace {} will be used!",
                rawNamespace, allowedNamespaceNames, defaultNamespaceName
            );
            return defaultNamespaceName;
        }
    }

    /**
     * Checks action name against allowed pattern if not matched returns default action name
     *
     * @param rawActionName action name extracted from URI
     * @return safe action name
     */
    protected String cleanupActionName(final String rawActionName) {
        if (allowedActionNames.matcher(rawActionName).matches()) {
            return rawActionName;
        } else {
            LOG.warn("{} did not match allowed action names {} - default action {} will be used!", rawActionName, allowedActionNames, defaultActionName);
            return defaultActionName;
        }
    }

    /**
     * Checks method name (when DMI is enabled) against allowed pattern if not matched returns default action name
     *
     * @param rawMethodName method name extracted from URI
     * @return safe method name
     */
    protected String cleanupMethodName(final String rawMethodName) {
        if (allowedMethodNames.matcher(rawMethodName).matches()) {
            return rawMethodName;
        } else {
            LOG.warn("{} did not match allowed method names {} - default method {} will be used!", rawMethodName, allowedMethodNames, defaultMethodName);
            return defaultMethodName;
        }
    }

    /**
     * Drops the extension from the action name
     *
     * @param name The action name
     * @return The action name without its extension
     * @deprecated Since 2.1, use {@link #dropExtension(java.lang.String, org.apache.struts2.dispatcher.mapper.ActionMapping)} instead
     */
    protected String dropExtension(String name) {
        return dropExtension(name, new ActionMapping());
    }

    /**
     * Drops the extension from the action name, storing it in the mapping for later use
     *
     * @param name    The action name
     * @param mapping The action mapping to store the extension in
     * @return The action name without its extension
     */
    protected String dropExtension(String name, ActionMapping mapping) {
        if (extensions == null) {
            return name;
        }
        for (String ext : extensions) {
            if ("".equals(ext)) {
                // This should also handle cases such as /foo/bar-1.0/description. It is tricky to
                // distinquish /foo/bar-1.0 but perhaps adding a numeric check in the future could
                // work
                int index = name.lastIndexOf('.');
                if (index == -1 || name.indexOf('/', index) >= 0) {
                    return name;
                }
            } else {
                String extension = "." + ext;
                if (name.endsWith(extension)) {
                    name = name.substring(0, name.length() - extension.length());
                    mapping.setExtension(ext);
                    return name;
                }
            }
        }
        return null;
    }

    /**
     * Returns null if no extension is specified.
     */
    protected String getDefaultExtension() {
        if (extensions == null) {
            return null;
        } else {
            return extensions.get(0);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.struts2.dispatcher.mapper.ActionMapper#getUriFromActionMapping(org.apache.struts2.dispatcher.mapper.ActionMapping)
     */
    public String getUriFromActionMapping(ActionMapping mapping) {
        StringBuilder uri = new StringBuilder();

        handleNamespace(mapping, uri);
        handleName(mapping, uri);
        handleDynamicMethod(mapping, uri);
        handleExtension(mapping, uri);
        handleParams(mapping, uri);

        return uri.toString();
    }

    protected void handleNamespace(ActionMapping mapping, StringBuilder uri) {
        if (mapping.getNamespace() != null) {
            uri.append(mapping.getNamespace());
            if (!"/".equals(mapping.getNamespace())) {
                uri.append("/");
            }
        }
    }

    protected void handleName(ActionMapping mapping, StringBuilder uri) {
        String name = mapping.getName();
        if (name.indexOf('?') != -1) {
            name = name.substring(0, name.indexOf('?'));
        }
        uri.append(name);
    }

    protected void handleDynamicMethod(ActionMapping mapping, StringBuilder uri) {
        // See WW-3965
        if (StringUtils.isNotEmpty(mapping.getMethod())) {
            if (allowDynamicMethodCalls) {
                // handle "name!method" convention.
                String name = mapping.getName();
                if (!name.contains("!")) {
                    // Append the method as no bang found
                    uri.append("!").append(mapping.getMethod());
                }
            } else {
                uri.append("!").append(mapping.getMethod());
            }
        }
    }

    protected void handleExtension(ActionMapping mapping, StringBuilder uri) {
        String extension = lookupExtension(mapping.getExtension());

        if (extension != null) {
            if (extension.length() == 0 || (extension.length() > 0 && uri.indexOf('.' + extension) == -1)) {
                if (extension.length() > 0) {
                    uri.append(".").append(extension);
                }
            }
        }
    }

    protected String lookupExtension(String extension) {
        if (extension == null) {
            // Look for the current extension, if available
            ActionContext context = ActionContext.getContext();
            if (context != null) {
                ActionMapping orig = (ActionMapping) context.get(ServletActionContext.ACTION_MAPPING);
                if (orig != null) {
                    extension = orig.getExtension();
                }
            }
            if (extension == null) {
                extension = getDefaultExtension();
            }
        }
        return extension;
    }

    protected void handleParams(ActionMapping mapping, StringBuilder uri) {
        String name = mapping.getName();
        String params = "";
        if (name.indexOf('?') != -1) {
            params = name.substring(name.indexOf('?'));
        }
        if (params.length() > 0) {
            uri.append(params);
        }
    }

}
