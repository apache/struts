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
<#if attributes.type?? && attributes.type=="button">
<button type="submit"<#rt/>
<#if attributes.id?has_content>
 id="${attributes.id}"<#rt/>
</#if>
<#if attributes.name?has_content>
 name="${attributes.name}"<#rt/>
</#if>
<#if attributes.nameValue??>
 value="<@s.property value="attributes.nameValue"/>"<#rt/>
</#if>
<#if attributes.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if attributes.cssClass?has_content>
 class="${attributes.cssClass}"<#rt/>
</#if>
<#if attributes.cssStyle?has_content>
 style="${attributes.cssStyle}"<#rt/>
</#if>
<#if attributes.title?has_content>
 title="${attributes.title}"<#rt/>
</#if>
<#if attributes.tabindex?has_content>
 tabindex="${attributes.tabindex}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/scripting-events.ftl"/>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
>
<#else>
<#if attributes.type?? && attributes.type=="image">
<input type="image"<#rt/>
<#if attributes.label?has_content>
 alt="${attributes.label}"<#rt/>
</#if>
<#if attributes.src?has_content>
 src="${attributes.src}"<#rt/>
</#if>
<#else>
<input type="submit"<#rt/>
<#if attributes.nameValue?has_content>
 value="<@s.property value="attributes.nameValue"/>"<#rt/>
</#if>
</#if>
<#if attributes.id?has_content>
 id="${attributes.id}"<#rt/>
</#if>
<#if attributes.name?has_content>
 name="${attributes.name}"<#rt/>
</#if>
<#if attributes.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if attributes.cssClass?has_content>
 class="${attributes.cssClass}"<#rt/>
</#if>
<#if attributes.cssStyle?has_content>
 style="${attributes.cssStyle}"<#rt/>
</#if>
<#if attributes.title?has_content>
 title="${attributes.title}"<#rt/>
</#if>
<#if attributes.tabindex?has_content>
 tabindex="${attributes.tabindex}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/scripting-events.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
/>
</#if>
