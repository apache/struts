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

import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.config.ConfigurationManager;


/**
 * ActionMapper for testing FilterDispatcher (used in FilterDispaatcherTest)
 */
public class NullActionMapper implements ActionMapper {

    private static ActionMapping _actionMapping;

    public NullActionMapper() {
    }

    public static void setActionMapping(ActionMapping actionMappingToBeRetrned) {
        _actionMapping = actionMappingToBeRetrned;
    }

    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager config) {
        return _actionMapping;
    }

    public ActionMapping getMappingFromActionName(String actionName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getUriFromActionMapping(ActionMapping mapping) {
        throw new UnsupportedOperationException("operation not supported");
    }
}
