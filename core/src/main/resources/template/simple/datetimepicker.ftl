<#--
/*
 * $Id: pom.xml 560558 2007-07-28 15:47:10Z apetrelli $
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
<script type="text/javascript">
    dojo.require("dojo.widget.DatePicker");
</script>
<div
   <#if parameters.type?if_exists == "date">
     dojoType="dropdowndatepicker"<#rt/>
   <#else>
     dojoType="struts:StrutsTimePicker"<#rt/>
  </#if>
  <#if parameters.id?if_exists != "">
    id="${parameters.id?html}"<#rt/>
  </#if>
  <#if parameters.nameValue?if_exists != "">
    value="${parameters.nameValue?html}"<#rt/>
  </#if>
  <#if parameters.language?if_exists != "">
    lang="${parameters.language?html}"<#rt/>
  </#if>
  <#if parameters.name?if_exists != "">
    name="dojo.${parameters.name?html}"<#rt/>
    inputName="${parameters.name?html}"<#rt/>
  </#if>
  <#if parameters.displayWeeks?if_exists != "">
    displayWeeks="${parameters.displayWeeks?html}"<#rt/>
  </#if>
  <#if parameters.adjustWeeks?exists>
    adjustWeeks="${parameters.adjustWeeks?string?html}"<#rt/>
  </#if>
  <#if parameters.startDate?if_exists != "">
    startDate="${parameters.startDate?html}"<#rt/>
  </#if>
  <#if parameters.endDate?if_exists != "">
    endDate="${parameters.endDate?html}"<#rt/>
  </#if>
  <#if parameters.weekStartsOn?if_exists != "">
    weekStartsOn="${parameters.weekStartsOn?html}"<#rt/>
  </#if>
  <#if parameters.staticDisplay?exists>
    staticDisplay="${parameters.staticDisplay?string?html}"<#rt/>
  </#if>
  <#if parameters.dayWidth?if_exists != "">
    dayWidth="${parameters.dayWidth?html}"<#rt/>
  </#if>
  <#if parameters.tabindex?if_exists != "">
    tabindex="${parameters.tabindex?html}"<#rt/>
  </#if>
  <#if parameters.cssClass?if_exists != "">
    class="${parameters.cssClass?html}"<#rt/>
  </#if>
  <#if parameters.cssStyle?if_exists != "">
    style="${parameters.cssStyle?html}"<#rt/>
  </#if>
  <#if parameters.iconPath?if_exists != "">
    iconURL="<@s.url value='${parameters.iconPath}' encode="false" includeParams='none'/>"<#rt/>
  </#if>
  <#if parameters.formatLength?if_exists != "">
    formatLength="${parameters.formatLength?html}"<#rt/>
  </#if>
  <#if parameters.displayFormat?if_exists != "">
    displayFormat="${parameters.displayFormat?html}"<#rt/>
  </#if>
  <#if parameters.toggleType?if_exists != "">
    containerToggle="${parameters.toggleType?html}"<#rt/>
  </#if>
  <#if parameters.toggleDuration?exists>
    containerToggleDuration="${parameters.toggleDuration?string?html}"<#rt/>
  </#if>
  <#if parameters.templateCssPath?exists>
	templateCssPath="<@s.url value='${parameters.templateCssPath}' encode="false" includeParams='none'/>"
  </#if>
  saveFormat="rfc"<#rt/>
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
></div>