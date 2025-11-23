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
    <textarea
            name="${(attributes.name!"")}"
<#if attributes.cols?has_content>
    cols="${(attributes.cols!"")}"
</#if>
            <#if attributes.rows?has_content>
                rows="${(attributes.rows!"")}"
            </#if>
            <#if attributes.wrap?has_content>
                wrap="${attributes.wrap}"
            </#if>
            <#if attributes.disabled!false>
                disabled="disabled"
            </#if>
            <#if attributes.readonly!false>
                readonly="readonly"
            </#if>
            <#if attributes.tabindex?has_content>
                tabindex="${attributes.tabindex}"
            </#if>
            <#if attributes.id?has_content>
                id="${attributes.id}"
            </#if>
            <#include "/${attributes.templateDir}/${attributes.expandTheme}/css.ftl" />
            <#if attributes.title?has_content>
                title="${attributes.title}"
            </#if>
            <#if attributes.maxlength?has_content>
                maxlength="${attributes.maxlength}"
            </#if>
            <#if attributes.minlength?has_content>
                minlength="${attributes.minlength}"
            </#if>
            <#include "/${attributes.templateDir}/${attributes.expandTheme}/scripting-events.ftl" />
            <#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
            <#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
>
<#if attributes.nameValue??>
    <@s.property value="attributes.nameValue"/>
</#if>
</textarea>
</#compress>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/controlfooter.ftl" />
