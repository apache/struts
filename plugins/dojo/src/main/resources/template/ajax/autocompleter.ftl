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
<#include "/${parameters.templateDir}/ajax/controlheader.ftl" />
<#if parameters.href?exists>
  <input dojoType="struts:ComboBox"<#rt/>
  dataUrl="${parameters.href}"<#rt/>
<#else>
  <select dojoType="struts:ComboBox"<#rt/>
</#if>
<#if parameters.id?if_exists != "">
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?if_exists != "">
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?if_exists != "">
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.forceValidOption?exists>
 forceValidOption="${parameters.forceValidOption?string?html}"<#rt/>
</#if>
<#if parameters.searchType?if_exists != "">
 searchType="${parameters.searchType?html}"<#rt/>
</#if>
<#if parameters.autoComplete?exists>
 autoComplete="${parameters.autoComplete?string?html}"<#rt/>
</#if>
<#if parameters.delay?exists>
 searchDelay="${parameters.delay?c}"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.dropdownWidth?exists>
 dropdownWidth="${parameters.dropdownWidth?c}"<#rt/>
</#if>
<#if parameters.dropdownHeight?exists>
 dropdownHeight="${parameters.dropdownHeight?c}"<#rt/>
</#if>
<#if parameters.name?if_exists != "">
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.get("size")?exists>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.keyName?if_exists != "">
 keyName="${parameters.keyName?html}"<#rt/>
</#if>
<#if parameters.maxlength?exists>
 maxlength="${parameters.maxlength?string?html}"<#rt/>
</#if>
<#if parameters.nameValue?if_exists != "">
 initialValue="${parameters.nameValue}"<#rt/>
</#if>
<#if parameters.nameKeyValue?if_exists != "">
 initialKey="${parameters.nameKeyValue}"<#rt/>
</#if>
<#if parameters.readonly?default(false)>
 readonly="readonly"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.formId?if_exists != "">
 formId="${parameters.formId?html}"<#rt/>
</#if>
<#if parameters.formFilter?if_exists != "">
 formFilter="${parameters.formFilter?html}"<#rt/>
</#if>
<#if parameters.listenTopics?if_exists != "">
 listenTopics="${parameters.listenTopics?html}"<#rt/>
</#if>
<#if parameters.notifyTopics?if_exists != "">
 notifyTopics="${parameters.notifyTopics?html}"<#rt/>
</#if>
<#if parameters.beforeNotifyTopics?if_exists != "">
  beforeNotifyTopics="${parameters.beforeNotifyTopics?html}"<#rt/>
</#if>
<#if parameters.afterNotifyTopics?if_exists != "">
  afterNotifyTopics="${parameters.afterNotifyTopics?html}"<#rt/>
</#if>
<#if parameters.errorNotifyTopics?if_exists != "">
  errorNotifyTopics="${parameters.errorNotifyTopics?html}"<#rt/>
</#if>
<#if parameters.valueNotifyTopics?if_exists != "">
  valueNotifyTopics="${parameters.valueNotifyTopics?html}"<#rt/>
</#if>
<#if parameters.indicator?if_exists != "">
 indicator="${parameters.indicator?html}"<#rt/>
</#if>
<#if parameters.loadOnTextChange?default(false)>
 loadOnType="${parameters.loadOnTextChange?string?html}"<#rt/>
</#if>
<#if parameters.loadMinimumCount?exists>
 loadMinimum="${parameters.loadMinimumCount?c}"<#rt/>
</#if>
<#if parameters.showDownArrow?exists>
 visibleDownArrow="${parameters.showDownArrow?string?html}"<#rt/>
</#if>
<#if parameters.iconPath?if_exists != "">
 buttonSrc="<@s.url value='${parameters.iconPath}' encode="false" includeParams='none'/>"<#rt/>
</#if>
<#if parameters.templateCssPath?if_exists != "">
 templateCssPath="<@s.url value='${parameters.templateCssPath}' encode="false" includeParams='none'/>"
</#if>
<#if parameters.dataFieldName?if_exists != "">
 dataFieldName="${parameters.dataFieldName?html}"
</#if>
<#if parameters.searchLimit?if_exists != "">
 searchLimit="${parameters.searchLimit?html}"
</#if>
<#if parameters.transport?if_exists != "">
  transport="${parameters.transport?html}"<#rt/>
</#if>
<#if parameters.preload?exists>
  preload="${parameters.preload?string?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
<#if parameters.href?exists>
 />
<#else>
 >
</#if>
<#if parameters.list?exists>
	<#if (parameters.headerKey?exists && parameters.headerValue?exists)>
		<option value="${parameters.headerKey?html}">${parameters.headerValue?html}</option>
	</#if>
	<#if parameters.emptyOption?default(false)>
	    <option value=""></option>
	</#if>
    <@s.iterator value="parameters.list">
    <#if parameters.listKey?exists>
    	<#assign tmpListKey = stack.findString(parameters.listKey) />
    <#else>
    	<#assign tmpListKey = stack.findString('top') />
    </#if>
    <#if parameters.listValue?exists>
    	<#assign tmpListValue = stack.findString(parameters.listValue) />
    <#else>
    	<#assign tmpListValue = stack.findString('top') />
    </#if>
    <option value="${tmpListKey?html}"<#rt/>
        <#if (parameters.nameValue?exists && parameters.nameValue == tmpListKey)>
 selected="selected"<#rt/>
        </#if>
    ><#t/>
            ${tmpListValue?html}<#t/>
    </option><#lt/>
    </@s.iterator>
  </select>
</#if>
<#if parameters.label?if_exists != "">
	<#include "/${parameters.templateDir}/xhtml/controlfooter.ftl" />
</#if>
<#if parameters.pushId>
<script language="JavaScript" type="text/javascript">djConfig.searchIds.push("${parameters.id?html}");</script>
</#if>


