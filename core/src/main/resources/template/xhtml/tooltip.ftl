<#--
/*
 * $Id: Action.java 502296 2007-02-01 17:33:39Z niallp $
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
<#if parameters.tooltip?exists><#t/>
	  <#assign tooltipHashCode=parameters.tooltip.hashCode() />
      <img id="_tt${tooltipHashCode?string('#')}"
      <#if parameters.tooltipIcon?exists><#t/>
      	src='<@s.url value="${parameters.tooltipIcon}" includeParams="none" encode="false" />'
      <#else><#t/>
      	src='<@s.url value="/struts/dojo/struts/tooltip.gif" includeParams="none" encode="false" />'
      </#if><#t/>
      	alt="${parameters.tooltip?html}" 
      	title="${parameters.tooltip?html}" />
      <#if parameters.jsTooltipEnabled?default('false') == 'true'>
      <span dojoType="tooltip" connectId="_tt${tooltipHashCode?string('#')}"
          <#if parameters.tooltipToggle?exists><#t/>
        	<#t/>toggle="${parameters.tooltipToggle}"<#t/>
          </#if><#t/>
          <#if parameters.tooltipToggleDuration?exists><#t/>
        	<#t/>toggleDuration="${parameters.tooltipToggleDuration}"<#t/>
          </#if><#t/>
          <#if parameters.tooltipDelay?exists><#t/>
          	<#t/>delay="${parameters.tooltipDelay}"<#t/>
          </#if><#t/>
          caption="${parameters.tooltip}"></span>
     </#if>
</#if><#t/>
