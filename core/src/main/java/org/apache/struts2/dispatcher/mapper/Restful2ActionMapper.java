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
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.URLDecoderUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Extended version of {@link RestfulActionMapper}, see documentation for more details
 * http://struts.apache.org/2.x/docs/restfulactionmapper.html
 */
public class Restful2ActionMapper extends DefaultActionMapper {

    protected static final Logger LOG = LoggerFactory.getLogger(Restful2ActionMapper.class);
    public static final String HTTP_METHOD_PARAM = "__http_method";
    private String idParameterName = null;
    
    public Restful2ActionMapper() {
    	setSlashesInActionNames("true");
    }

    /*
    * (non-Javadoc)
    *
    * @see org.apache.struts2.dispatcher.mapper.ActionMapper#getMapping(javax.servlet.http.HttpServletRequest)
    */
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
    	if (!isSlashesInActionNames()) {
    		throw new IllegalStateException("This action mapper requires the setting 'slashesInActionNames' to be set to 'true'");
    	}
        ActionMapping mapping = super.getMapping(request, configManager);
        
        if (mapping == null) {
            return null;
        }

        String actionName = mapping.getName();

        String id = null;

        // Only try something if the action name is specified
        if (actionName != null && actionName.length() > 0) {

            int lastSlashPos = actionName.lastIndexOf('/');
            if (lastSlashPos > -1) {
                id = actionName.substring(lastSlashPos+1);
            }


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
                    // Viewing the form to create a new item e.g. foo/new
                    if (isGet(request) && "new".equals(id)) {
                        mapping.setMethod("editNew");

                    // Viewing an item e.g. foo/1
                    } else if (isGet(request)) {
                        mapping.setMethod("view");

                    // Removing an item e.g. foo/1
                    } else if (isDelete(request)) {
                        mapping.setMethod("remove");
                    
                    // Updating an item e.g. foo/1    
                    }  else if (isPut(request)) {
                        mapping.setMethod("update");
                    }
                    
                }
                
                if (idParameterName != null && lastSlashPos > -1) {
                	actionName = actionName.substring(0, lastSlashPos);
                }
            }

            if (idParameterName != null && id != null) {
                if (mapping.getParams() == null) {
                    mapping.setParams(new HashMap<String, Object>());
                }
                mapping.getParams().put(idParameterName, id);
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
                            paramName = URLDecoderUtil.decode(st.nextToken(), "UTF-8");
                            isNameTok = false;
                        } else {
                            paramValue = URLDecoderUtil.decode(st.nextToken(), "UTF-8");

                            if ((paramName != null) && (paramName.length() > 0)) {
                                parameters.put(paramName, paramValue);
                            }

                            isNameTok = true;
                        }
                    }
                    if (parameters.size() > 0) {
                        if (mapping.getParams() == null) {
                            mapping.setParams(new HashMap<String, Object>());
                        }
                        mapping.getParams().putAll(parameters);
                    }
                } catch (Exception e) {
                    if (LOG.isWarnEnabled()) {
                	LOG.warn("Unable to determine parameters from the url", e);
                    }
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

	public String getIdParameterName() {
		return idParameterName;
	}

	@Inject(required=false,value=StrutsConstants.STRUTS_ID_PARAMETER_NAME)
	public void setIdParameterName(String idParameterName) {
		this.idParameterName = idParameterName;
	}
}
