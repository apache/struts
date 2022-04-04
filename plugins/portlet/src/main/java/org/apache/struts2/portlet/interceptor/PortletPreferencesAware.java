/*
 * $Id: PortletPreferencesAware.java 557544 2007-07-19 10:03:06Z nilsga $
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
package org.apache.struts2.portlet.interceptor;

import javax.portlet.PortletPreferences;


/**
 * All Actions that want to have access to the portlet preferences should
 * implement this interface.  If running in a servlet environment, an
 * appropriate testing implementation will be provided.
 */
public interface PortletPreferencesAware {

    /**
     * Sets the HTTP request object in implementing classes.
     *
     * @param prefs the portlet preferences.
     */
    public void setPortletPreferences(PortletPreferences prefs);
}
