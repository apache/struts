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
    <#if parameters.type?? && parameters.type=="button">
        <button type="reset"
                <#if parameters.name?has_content>
                    name="${parameters.name}"
                </#if>
                <#if parameters.nameValue??>
                    value="<@s.property value="parameters.nameValue"/>"
                </#if>
                <#if parameters.cssClass?has_content>
                    class="${parameters.cssClass}"
                </#if>
                <#if parameters.cssStyle?has_content>
                    style="${parameters.cssStyle}"
                </#if>
                <#if parameters.disabled!false>
                    disabled="disabled"
                </#if>
                <#if parameters.tabindex?has_content>
                    tabindex="${parameters.tabindex}"
                </#if>
                <#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl"/>
                <#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
                <#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
        ><#if parameters.src?has_content>
                <img
                <#if parameters.label?has_content>
                    alt="${parameters.label}"
                </#if>
                <#if parameters.src?has_content>
                    src="${parameters.src}"
                </#if>
                /><#else><#if parameters.label?has_content><@s.property value="parameters.label"/></#if></#if></button>
    <#else>
        <input type="reset"
                <#if parameters.name?has_content>
                    name="${parameters.name}"
                </#if>
                <#if parameters.nameValue??>
                    value="<@s.property value="parameters.nameValue"/>"
                </#if>
                <#if parameters.cssClass?has_content>
                    class="${parameters.cssClass}"
                </#if>
                <#if parameters.cssStyle?has_content>
                    style="${parameters.cssStyle}"
                </#if>
                <#if parameters.title?has_content>
                    title="${parameters.title}"
                </#if>
                <#if parameters.disabled!false>
                    disabled="disabled"
                </#if>
                <#if parameters.tabindex?has_content>
                    tabindex="${parameters.tabindex}"
                </#if>
                <#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
                <#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
                <#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
        />
    </#if>
</#compress>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/controlfooter.ftl" />
