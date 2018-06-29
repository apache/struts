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

package org.apache.struts2.rest;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * <!-- START SNIPPET: description -->
 *
 * This Restful action mapper enforces Ruby-On-Rails Rest-style mappings.  If the method 
 * is not specified (via '!' or 'method:' prefix), the method is "guessed" at using 
 * ReST-style conventions that examine the URL and the HTTP method.  Special care has
 * been given to ensure this mapper works correctly with the codebehind plugin so that
 * XML configuration is unnecessary.
 *  
 * <p>
 *   This mapper supports the following parameters:
 * </p>
 * <ul>
 *   <li><code>struts.mapper.idParameterName</code> - If set, this value will be the name
 *       of the parameter under which the id is stored.  The id will then be removed
 *       from the action name.  Whether or not the method is specified, the mapper will 
 *       try to truncate the identifier from the url and store it as a parameter.
 *   </li>
 *   <li><code>struts.mapper.indexMethodName</code> - The method name to call for a GET
 *       request with no id parameter. Defaults to 'index'.
 *   </li>
 *   <li><code>struts.mapper.getMethodName</code> - The method name to call for a GET
 *       request with an id parameter. Defaults to 'show'.
 *   </li>
 *   <li><code>struts.mapper.postMethodName</code> - The method name to call for a POST
 *       request with no id parameter. Defaults to 'create'.
 *   </li>
 *   <li><code>struts.mapper.putMethodName</code> - The method name to call for a PUT
 *       request with an id parameter. Defaults to 'update'.
 *   </li>
 *   <li><code>struts.mapper.deleteMethodName</code> - The method name to call for a DELETE
 *       request with an id parameter. Defaults to 'destroy'.
 *   </li>
 *   <li><code>struts.mapper.editMethodName</code> - The method name to call for a GET
 *       request with an id parameter and the 'edit' view specified. Defaults to 'edit'.
 *   </li>
 *   <li><code>struts.mapper.newMethodName</code> - The method name to call for a GET
 *       request with no id parameter and the 'new' view specified. Defaults to 'editNew'.
 *   </li>
 * </ul>
 * <p>
 * The following URL's will invoke its methods:
 * </p>
 * <ul> 
 *  <li><code>GET:    /movies                => method="index"</code></li>
 *  <li><code>GET:    /movies/Thrillers      => method="show", id="Thrillers"</code></li>
 *  <li><code>GET:    /movies/Thrillers;edit => method="edit", id="Thrillers"</code></li>
 *  <li><code>GET:    /movies/Thrillers/edit => method="edit", id="Thrillers"</code></li>
 *  <li><code>GET:    /movies/new            => method="editNew"</code></li>
 *  <li><code>POST:   /movies                => method="create"</code></li>
 *  <li><code>PUT:    /movies/Thrillers      => method="update", id="Thrillers"</code></li>
 *  <li><code>DELETE: /movies/Thrillers      => method="destroy", id="Thrillers"</code></li>
 * </ul>
 * <p>
 * To simulate the HTTP methods PUT and DELETE, since they aren't supported by HTML,
 * the HTTP parameter "_method" will be used.
 * </p>
 * <!-- END SNIPPET: description -->
 */
public class RestActionMapper extends DefaultActionMapper {

    protected static final Logger LOG = LoggerFactory.getLogger(RestActionMapper.class);
    public static final String HTTP_METHOD_PARAM = "_method";
    private String idParameterName = "id";
    private String indexMethodName = "index";
    private String getMethodName = "show";
    private String postMethodName = "create";
    private String editMethodName = "edit";
    private String newMethodName = "editNew";
    private String deleteMethodName = "destroy";
    private String putMethodName = "update";
    private String optionsMethodName = "options";
    private String postContinueMethodName = "createContinue";
    private String putContinueMethodName = "updateContinue";
    private boolean allowDynamicMethodCalls = false;
    
    public RestActionMapper() {
        this.defaultMethodName = indexMethodName;
    }
    
    public String getIdParameterName() {
        return idParameterName;
    }

    @Inject(required=false,value=StrutsConstants.STRUTS_ID_PARAMETER_NAME)
    public void setIdParameterName(String idParameterName) {
        this.idParameterName = idParameterName;
    }

    @Inject(required=false,value="struts.mapper.indexMethodName")
    public void setIndexMethodName(String indexMethodName) {
        this.indexMethodName = indexMethodName;
    }

    @Inject(required=false,value="struts.mapper.getMethodName")
    public void setGetMethodName(String getMethodName) {
        this.getMethodName = getMethodName;
    }

    @Inject(required=false,value="struts.mapper.postMethodName")
    public void setPostMethodName(String postMethodName) {
        this.postMethodName = postMethodName;
    }

    @Inject(required=false,value="struts.mapper.editMethodName")
    public void setEditMethodName(String editMethodName) {
        this.editMethodName = editMethodName;
    }

    @Inject(required=false,value="struts.mapper.newMethodName")
    public void setNewMethodName(String newMethodName) {
        this.newMethodName = newMethodName;
    }

    @Inject(required=false,value="struts.mapper.deleteMethodName")
    public void setDeleteMethodName(String deleteMethodName) {
        this.deleteMethodName = deleteMethodName;
    }

    @Inject(required=false,value="struts.mapper.putMethodName")
    public void setPutMethodName(String putMethodName) {
        this.putMethodName = putMethodName;
    }

    @Inject(required=false,value="struts.mapper.optionsMethodName")
    public void setOptionsMethodName(String optionsMethodName) {
        this.optionsMethodName = optionsMethodName;
    }

    @Inject(required=false,value="struts.mapper.postContinueMethodName")
    public void setPostContinueMethodName(String postContinueMethodName) {
        this.postContinueMethodName = postContinueMethodName;
    }

    @Inject(required=false,value="struts.mapper.putContinueMethodName")
    public void setPutContinueMethodName(String putContinueMethodName) {
        this.putContinueMethodName = putContinueMethodName;
    }

    @Inject(required = false, value = StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION)
    public void setAllowDynamicMethodCalls(String allowDynamicMethodCalls) {
        this.allowDynamicMethodCalls = "true".equalsIgnoreCase(allowDynamicMethodCalls);
    }
    
    public ActionMapping getMapping(HttpServletRequest request,
            ConfigurationManager configManager) {
        ActionMapping mapping = new ActionMapping();
        String uri = RequestUtils.getUri(request);

        uri = dropExtension(uri, mapping);
        if (uri == null) {
            return null;
        }

        parseNameAndNamespace(uri, mapping, configManager);

        handleSpecialParameters(request, mapping);

        if (mapping.getName() == null) {
            return null;
        }

        // handle "name!method" convention.
        handleDynamicMethodInvocation(mapping, mapping.getName());

        String fullName = mapping.getName();
        // Only try something if the action name is specified
        if (fullName != null && fullName.length() > 0) {

            // cut off any ;jsessionid= type appendix but allow the rails-like ;edit
            int scPos = fullName.indexOf(';');
            if (scPos > -1 && !"edit".equals(fullName.substring(scPos + 1))) {
                fullName = fullName.substring(0, scPos);
            }

            int lastSlashPos = fullName.lastIndexOf('/');
            String id = null;
            if (lastSlashPos > -1) {

                // fun trickery to parse 'actionName/id/methodName' in the case of 'animals/dog/edit'
                int prevSlashPos = fullName.lastIndexOf('/', lastSlashPos - 1);
                //WW-4589 do not overwrite explicit method name
                if (prevSlashPos > -1 && mapping.getMethod() == null) {
                    mapping.setMethod(fullName.substring(lastSlashPos + 1));
                    fullName = fullName.substring(0, lastSlashPos);
                    lastSlashPos = prevSlashPos;
                }
                id = fullName.substring(lastSlashPos + 1);
            }



            // If a method hasn't been explicitly named, try to guess using ReST-style patterns
            if (mapping.getMethod() == null) {

            	if (isOptions(request)) {
                	mapping.setMethod(optionsMethodName);
                
            	// Handle uris with no id, possibly ending in '/'
            	} else if (lastSlashPos == -1 || lastSlashPos == fullName.length() -1) {

                    // Index e.g. foo
                    if (isGet(request)) {
                        mapping.setMethod(indexMethodName);
                        
                    // Creating a new entry on POST e.g. foo
                    } else if (isPost(request)) {
                    	if (isExpectContinue(request)) {
                            mapping.setMethod(postContinueMethodName);
                    	} else {
                            mapping.setMethod(postMethodName);
                    	}
                    }

                // Handle uris with an id at the end
                } else if (id != null) {
                    
                    // Viewing the form to edit an item e.g. foo/1;edit
                    if (isGet(request) && id.endsWith(";edit")) {
                        id = id.substring(0, id.length() - ";edit".length());
                        mapping.setMethod(editMethodName);
                        
                    // Viewing the form to create a new item e.g. foo/new
                    } else if (isGet(request) && "new".equals(id)) {
                        mapping.setMethod(newMethodName);

                    // Removing an item e.g. foo/1
                    } else if (isDelete(request)) {
                        mapping.setMethod(deleteMethodName);
                        
                    // Viewing an item e.g. foo/1
                    } else if (isGet(request)) {
                        mapping.setMethod(getMethodName);
                    
                    // Updating an item e.g. foo/1    
                    }  else if (isPut(request)) {
                    	if (isExpectContinue(request)) {
                            mapping.setMethod(putContinueMethodName);
                    	} else {
                            mapping.setMethod(putMethodName);
                    	}
                    }
                }
            }

            // cut off the id parameter, even if a method is specified
            if (id != null) {
                if (!"new".equals(id)) {
                    if (mapping.getParams() == null) {
                        mapping.setParams(new HashMap<String, Object>());
                    }
                    mapping.getParams().put(idParameterName, new String[]{id});
                }
                fullName = fullName.substring(0, lastSlashPos);
            }

            mapping.setName(cleanupActionName(fullName));
        }
        return mapping;
    }

    private void handleDynamicMethodInvocation(ActionMapping mapping, String name) {
        int exclamation = name.lastIndexOf("!");
        if (exclamation != -1) {
            String actionName = name.substring(0, exclamation);
            String actionMethod = name.substring(exclamation + 1);

            // WW-4585
            // add any ; appendix to name, it will be handled later in getMapping method
            int scPos = actionMethod.indexOf(';');
            if (scPos != -1) {
                actionName = actionName + actionMethod.substring(scPos);
                actionMethod = actionMethod.substring(0, scPos);
            }

            mapping.setName(actionName);
            if (allowDynamicMethodCalls) {
                mapping.setMethod(cleanupMethodName(actionMethod));
            } else {
                mapping.setMethod(null);
            }
        }
    }

    /**
     * Parses the name and namespace from the uri.  Uses the configured package 
     * namespaces to determine the name and id parameter, to be parsed later.
     *
     * @param uri
     *            The uri
     * @param mapping
     *            The action mapping to populate
     */
    protected void parseNameAndNamespace(String uri, ActionMapping mapping,
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
        } else {
            // Try to find the namespace in those defined, defaulting to ""
            Configuration config = configManager.getConfiguration();
            String prefix = uri.substring(0, lastSlash);
            namespace = "";
            // Find the longest matching namespace, defaulting to the default
            for (Object o : config.getPackageConfigs().values()) {
                String ns = ((PackageConfig) o).getNamespace();
                if (ns != null && prefix.startsWith(ns) && (prefix.length() == ns.length() || prefix.charAt(ns.length()) == '/')) {
                    if (ns.length() > namespace.length()) {
                        namespace = ns;
                    }
                }
            }

            name = uri.substring(namespace.length() + 1);
        }

        mapping.setNamespace(cleanupNamespaceName(namespace));
        mapping.setName(name);
    }

    protected boolean isGet(HttpServletRequest request) {
        return "get".equalsIgnoreCase(request.getMethod());
    }

    protected boolean isPost(HttpServletRequest request) {
        return "post".equalsIgnoreCase(request.getMethod());
    }

    protected boolean isPut(HttpServletRequest request) {
        if ("put".equalsIgnoreCase(request.getMethod())) {
            return true;
        } else {
            return isPost(request) && "put".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
        }
    }

    protected boolean isDelete(HttpServletRequest request) {
        if ("delete".equalsIgnoreCase(request.getMethod())) {
            return true;
        } else {
            return "delete".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
        }
    }

    protected boolean isOptions(HttpServletRequest request) {
        return "options".equalsIgnoreCase(request.getMethod());
    }
    
    protected boolean isExpectContinue(HttpServletRequest request) {
    	String expect = request.getHeader("Expect");
    	return (expect != null && expect.toLowerCase().contains("100-continue")); 
    }

}
