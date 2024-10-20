<#--
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
-->
<#if attributes.tooltip??><#t/>
      <img
      <#if attributes.tooltipIconPath??><#t/>
      	src='<@s.url value="${attributes.tooltipIconPath}" includeParams="none" encode="false" />'
      <#else><#t/>
      	src='<@s.url value="${attributes.staticContentPath}/tooltip.gif" includeParams="none" encode="false" />'
      </#if><#t/>
      <#if (attributes.jsTooltipEnabled!'false') == 'true'>
          onmouseover="domTT_activate(this, event, 'content', '<#outputformat 'JavaScript'>${attributes.tooltip}</#outputformat>'<#t/>
          <#if attributes.tooltipDelay??><#t/>
          	<#t/>,'delay', '${attributes.tooltipDelay}'<#t/>
          </#if><#t/>
          <#t/>,'styleClass', '${attributes.tooltipCssClass!"StrutsTTClassic"}'<#t/>
          <#t/>)" />
      <#else>
      	title="${attributes.tooltip}"
      	alt="${attributes.tooltip}" />
     </#if>
</#if><#t/>
