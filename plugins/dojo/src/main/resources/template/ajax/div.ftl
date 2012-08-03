<#--
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
-->
<div dojoType="struts:BindDiv"
  <#if parameters.delay?exists>
    delay="${parameters.delay?c}"<#rt/>
  </#if>
  <#if parameters.updateFreq?exists>
    updateFreq="${parameters.updateFreq?c}"<#rt/>
  </#if>
  <#if parameters.autoStart?exists>
    autoStart="${parameters.autoStart?string?html}"<#rt/>
  </#if>
  <#if parameters.closable?exists>
    closable="${parameters.closable?string?html}"<#rt/>
  </#if>
  <#if parameters.startTimerListenTopics?if_exists != "">
    startTimerListenTopics="${parameters.startTimerListenTopics?html}"<#rt/>
  </#if>
  <#if parameters.stopTimerListenTopics?if_exists != "">
    stopTimerListenTopics="${parameters.stopTimerListenTopics?html}"<#rt/>
  </#if>
  <#if parameters.refreshOnShow?exists>
    refreshOnShow="${parameters.refreshOnShow?string?html}"<#rt/>
  </#if>
  <#if parameters.preload?exists>
    preload="${parameters.preload?string?html}"<#rt/>
  </#if>
  <#if parameters.disabled?default(false)>
    disabled="disabled"<#rt/>
  </#if>
  <#include "/${parameters.templateDir}/ajax/ajax-common.ftl" />
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
  <#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
>
