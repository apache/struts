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

package org.apache.struts2.dispatcher;


import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

/**
 * A simple action support class that sets properties to be able to serve
 */
public class DefaultActionSupport extends ActionSupport {

    private static final long serialVersionUID = -2426166391283746095L;

    private String successResultValue;


    /**
     * Constructor
     */
    public DefaultActionSupport() {
        super();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    public String execute() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String requestedUrl = request.getPathInfo();
        if (successResultValue == null) successResultValue = requestedUrl;
        return SUCCESS;
    }

    /**
     * @return Returns the successResultValue.
     */
    public String getSuccessResultValue() {
        return successResultValue;
    }

    /**
     * @param successResultValue The successResultValue to set.
     */
    public void setSuccessResultValue(String successResultValue) {
        this.successResultValue = successResultValue;
    }


}
