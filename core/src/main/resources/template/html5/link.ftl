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
<@s.compress>
    <link
            <#if attributes.rel?has_content>
                rel="${attributes.rel}"
            <#else>
                rel="stylesheet"
            </#if>
            <#if attributes.type?has_content>
                type="${attributes.type}"
            <#else>
                type="text/css"
            </#if>
            <#if attributes.href?has_content>
                href="${attributes.href}"
            </#if>
            <#if attributes.hreflang?has_content>
                hreflang="${attributes.hreflang}"
            </#if>
            <#if attributes.disabled?has_content && attributes.disabled == "true">
                disabled
            </#if>
            <#if attributes.media?has_content>
                media="${attributes.media}"
            <#else>
                media="all"
            </#if>
            <#if attributes.title?has_content>
                title="${attributes.title}"
            </#if>
            <#if attributes.as?has_content>
                as="${attributes.as}"
            </#if>
            <#if attributes.referrerpolicy?has_content>
                referrerpolicy="${attributes.referrerpolicy}"
            </#if>
            <#if attributes.sizes?has_content>
                sizes="${attributes.sizes}"
            </#if>
            <#if attributes.crossorigin?has_content>
                crossorigin="${attributes.crossorigin}"
            </#if>
            <#if attributes.integrity?has_content>
                integrity="${attributes.integrity}"
            </#if>
            <#if attributes.importance?has_content>
                importance="${attributes.importance}"
            </#if>
            <#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
            <#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
            <#include "/${attributes.templateDir}/${attributes.expandTheme}/nonce.ftl" />
    />
</@s.compress>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/controlfooter.ftl" />
