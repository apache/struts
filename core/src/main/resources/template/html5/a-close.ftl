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
<#include "/${attributes.templateDir}/${attributes.expandTheme}/controlheader.ftl" />
<#compress>
<a
<#if attributes.id??>
 id="${attributes.id}"
</#if>
<#if attributes.href??>
 href="${attributes.href?no_esc}"
</#if>
<#if attributes.disabled!false>
 disabled="disabled"
</#if>
<#if attributes.tabindex??>
 tabindex="${attributes.tabindex}"
</#if>
<#if attributes.cssClass??>
 class="${attributes.cssClass}"
</#if>
<#if attributes.cssStyle??>
 style="${attributes.cssStyle}"
</#if>
<#if attributes.title??>
 title="${attributes.title}"
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/scripting-events.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
>${tag.escapeHtmlBody()?then(attributes.body, attributes.body?no_esc)}</a>
</#compress>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/controlfooter.ftl" />
