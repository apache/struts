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

package org.apache.struts2.dojo.components;


public interface RemoteBean {

    void setListenTopics(String topics);

    void setNotifyTopics(String topics);

    void setHref(String href);

    void setErrorText(String errorText);

    void setAfterNotifyTopics(String afterNotifyTopics);

    void setBeforeNotifyTopics(String beforeNotifyTopics);
    
    void setErrorNotifyTopics(String errorNotifyTopics);

    void setExecuteScripts(String executeScripts);

    void setLoadingText(String loadingText);

    void setHandler(String handler);

    void setFormFilter(String formFilter);

    void setFormId(String formId);

    void setShowErrorTransportText(String showError);

    void setShowLoadingText(String showLoadingText);

    void setIndicator(String indicator);
    
    void setName(String name);
    
    void setCssStyle(String style);
    
    void setCssClass(String cssClass);
    
    void setHighlightColor(String color);
    
    void setHighlightDuration(String color);
    
    void setSeparateScripts(String separateScripts);
    
    void setTransport(String transport);
    
    void setParseContent(String parseContent);
}
