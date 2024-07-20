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
<#compress>
<input type="file"
 name="${(parameters.name!"")}"
<#if parameters.get("size")?has_content>
 size="${parameters.get("size")}"
</#if>
<#if parameters.disabled!false>
 disabled="disabled"
</#if>
<#if parameters.accept?has_content>
 accept="${parameters.accept}"
</#if>
<#if parameters.tabindex?has_content>
 tabindex="${parameters.tabindex}"
</#if>
<#if parameters.id?has_content>
 id="${parameters.id}"
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/css.ftl" />
<#if parameters.title?has_content>
 title="${parameters.title}"
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
/>
</#compress>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/controlfooter.ftl" />
