/*
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
package org.apache.struts2.portlet.example.eventing;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.portlet.interceptor.PortletResponseAware;

import javax.portlet.ActionResponse;
import javax.portlet.PortletResponse;
import javax.xml.namespace.QName;

public class PublishAction extends ActionSupport implements PortletResponseAware {

    private PortletResponse response;
    private String name;

    public String execute() throws Exception {

        if (name != null) {
            ((ActionResponse) response).setEvent(new QName("http://org.apache.struts2.portlets/events", "name"), name);

            addActionMessage("Publishing Event with Parameter name : " + name);
        }

        return SUCCESS;
    }

    public void setPortletResponse(PortletResponse response) {
        this.response = response;

    }

    public void setName(String name) {
        this.name = name;
    }
}
