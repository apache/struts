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
<input type="password"<#rt/>
 name="${(attributes.name!"")}"<#rt/>
<#if attributes.get("size")?has_content>
 size="${attributes.get("size")}"<#rt/>
</#if>
<#if attributes.maxlength?has_content>
 maxlength="${attributes.maxlength}"<#rt/>
</#if>
<#if attributes.nameValue?? && attributes.showPassword!false>
 value="<@s.property value="attributes.nameValue"/>"<#rt/>
</#if>
<#if attributes.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if attributes.readonly!false>
 readonly="readonly"<#rt/>
</#if>
<#if attributes.tabindex?has_content>
 tabindex="${attributes.tabindex}"<#rt/>
</#if>
<#if attributes.id?has_content>
 id="${attributes.id}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/css.ftl" />
<#if attributes.title?has_content>
 title="${attributes.title}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/scripting-events.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
/>