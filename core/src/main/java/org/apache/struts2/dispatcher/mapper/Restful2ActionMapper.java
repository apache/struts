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

import com.opensymphony.xwork2.config.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <!-- START SNIPPET: description -->
 *
 * Improved restful action mapper that adds several ReST-style improvements to
 * action mapping, but supports fully-customized URL's via XML.  The two primary
 * ReST enhancements are:
 * <ul>
 *  <li>If the method is not specified (via '!' or 'method:' prefix), the method is
 *      "guessed" at using ReST-style conventions that examine the URL and the HTTP
 *      method.</li>
 *  <li>Parameters are extracted from the action name, if parameter name/value pairs
 *      are specified using PARAM_NAME/PARAM_VALUE syntax.
 * </ul>
 * <p>
 * These two improvements allow a GET request for 'category/action/movie/Thrillers' to
 * be mapped to the action name 'movie' with an id of 'Thrillers' with an extra parameter
 * named 'category' with a value of 'action'.  A single action mapping can then handle
 * all CRUD operations using wildcards, e.g.
 * </p>
 * <pre>
 *   &lt;action name="movie/*" className="app.MovieAction"&gt;
 *     &lt;param name="id"&gt;{0}&lt;/param&gt;
 *     ...
 *   &lt;/action&gt;
 * </pre>
 * <p>
 * The following URL's will invoke its methods:
 * </p>
 * <ul>
 *  <li><code>GET:    /movie               => method="index"</code></li>
 *  <li><code>GET:    /movie/Thrillers      => method="view", id="Thrillers"</code></li>
 *  <li><code>GET:    /movie/Thrillers!edit => method="edit", id="Thrillers"</code></li>
 *  <li><code>GET:    /movie/new           => method="editNew"</code></li>
 *  <li><code>POST:   /movie/Thrillers      => method="create"</code></li>
 *  <li><code>PUT:    /movie/              => method="update"</code></li>
 *  <li><code>DELETE: /movie/Thrillers      => method="remove"</code></li>
 * </ul>
 * <p>
 * To simulate the HTTP methods PUT and DELETE, since they aren't supported by HTML,
 * the HTTP parameter "__http_method" will be used.
 * </p>
 * <p>
 * The syntax and design for this feature was inspired by the ReST support in Ruby on Rails.
 * See <a href="http://ryandaigle.com/articles/2006/08/01/whats-new-in-edge-rails-simply-restful-support-and-how-to-use-it">
 * http://ryandaigle.com/articles/2006/08/01/whats-new-in-edge-rails-simply-restful-support-and-how-to-use-it
 * </a>
 * </p>
 *
 * <!-- END SNIPPET: description -->
 */
public class Restful2ActionMapper extends DefaultActionMapper {

    protected static final Log LOG = LogFactory.getLog(Restful2ActionMapper.class);
    private static final String HTTP_METHOD_PARAM = "__http_method";

    /*
    * (non-Javadoc)
    *
    * @see org.apache.struts2.dispatcher.mapper.ActionMapper#getMapping(javax.servlet.http.HttpServletRequest)
    */
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {

        ActionMapping mapping = super.getMapping(request, configManager);
        
        if (mapping == null) {
            return null;
        }

        String actionName = mapping.getName();

        // Only try something if the action name is specified
        if (actionName != null && actionName.length() > 0) {
            int lastSlashPos = actionName.lastIndexOf('/');

            // If a method hasn't been explicitly named, try to guess using ReST-style patterns
            if (mapping.getMethod() == null) {

                if (lastSlashPos == actionName.length() -1) {

                    // Index e.g. foo/
                    if (isGet(request)) {
                        mapping.setMethod("index");
                        
                    // Creating a new entry on POST e.g. foo/
                    } else if (isPost(request)) {
                        mapping.setMethod("create");
                    }

                } else if (lastSlashPos > -1) {
                    String id = actionName.substring(lastSlashPos+1);

                    // Viewing the form to create a new item e.g. foo/new
                    if (isGet(request) && "new".equals(id)) {
                        mapping.setMethod("editNew");

                    // Viewing an item e.g. foo/1
                    } else if (isGet(request)) {
                        mapping.setMethod("view");

                    // Updating an item e.g. foo/1
                    } else if (isPut(request)) {
                        mapping.setMethod("update");

                    // Removing an item e.g. foo/1
                    } else if (isDelete(request)) {
                        mapping.setMethod("remove");
                    }
                }
            }

            // Try to determine parameters from the url before the action name
            int actionSlashPos = actionName.lastIndexOf('/', lastSlashPos - 1);
            if (actionSlashPos > 0 && actionSlashPos < lastSlashPos) {
                String params = actionName.substring(0, actionSlashPos);
                HashMap<String,String> parameters = new HashMap<String,String>();
                try {
                    StringTokenizer st = new StringTokenizer(params, "/");
                    boolean isNameTok = true;
                    String paramName = null;
                    String paramValue;

                    while (st.hasMoreTokens()) {
                        if (isNameTok) {
                            paramName = URLDecoder.decode(st.nextToken(), "UTF-8");
                            isNameTok = false;
                        } else {
                            paramValue = URLDecoder.decode(st.nextToken(), "UTF-8");

                            if ((paramName != null) && (paramName.length() > 0)) {
                                parameters.put(paramName, paramValue);
                            }

                            isNameTok = true;
                        }
                    }
                    if (parameters.size() > 0) {
                        if (mapping.getParams() == null) {
                            mapping.setParams(new HashMap());
                        }
                        mapping.getParams().putAll(parameters);
                    }
                } catch (Exception e) {
                    LOG.warn(e);
                }
                mapping.setName(actionName.substring(actionSlashPos+1));
            }
        }

        return mapping;
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
            return isPost(request) && "delete".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
        }
    }

}
