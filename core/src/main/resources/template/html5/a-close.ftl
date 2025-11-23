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
<#include "/${parameters.templateDir}/${parameters.expandTheme}/controlheader.ftl" />
<@compress single_line=true>
<a
<#if parameters.id??>
 id="${parameters.id}"
</#if>
<#if parameters.href??>
 href="${parameters.href?no_esc}"
</#if>
<#if parameters.disabled!false>
 disabled="disabled"
</#if>
<#if parameters.tabindex??>
 tabindex="${parameters.tabindex}"
</#if>
<#if parameters.cssClass??>
 class="${parameters.cssClass}"
</#if>
<#if parameters.cssStyle??>
 style="${parameters.cssStyle}"
</#if>
<#if parameters.title??>
 title="${parameters.title}"
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
>${tag.escapeHtmlBody()?then(parameters.body, parameters.body?no_esc)}</a>
</@compress>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/controlfooter.ftl" />
