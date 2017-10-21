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
package org.apache.struts2.portlet;

/**
 * Defines phases of portlet processing per the portlet specification.
 */
public enum PortletPhase {

    /**
     * Constant used for the render phase (
     * {@link javax.portlet.Portlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)})
     */
    RENDER_PHASE,

    /**
     * Constant used for the action phase (
     * {@link javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)})
     */
    ACTION_PHASE,

    /**
     * Constant used for the event phase
     */
    EVENT_PHASE,

    /**
     * Constant used for the serve resource phase that was added with the 2.0 portlet specification.
     */
    SERVE_RESOURCE_PHASE;

    public boolean isRender() {
        return this.equals(RENDER_PHASE);
    }

    public boolean isAction() {
        return this.equals(ACTION_PHASE);
    }

    public boolean isEvent() {
        return this.equals(EVENT_PHASE);
    }

    public boolean isResource() {
        return this.equals(SERVE_RESOURCE_PHASE);
    }

}
