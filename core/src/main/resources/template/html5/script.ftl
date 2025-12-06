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
<@s.compress>
<script
        <#if attributes.async?has_content && attributes.async == "true">
            async
        </#if>
        <#if attributes.charset?has_content>
            charset="${attributes.charset}"
        </#if>
        <#if attributes.defer?has_content && attributes.defer=="true">
            defer
        </#if>
        <#if attributes.type?has_content>
            type="${attributes.type}"
        </#if>
        <#if attributes.src?has_content>
            src="${attributes.src}"
        </#if>
        <#if attributes.referrerpolicy?has_content>
            referrerpolicy="${attributes.referrerpolicy}"
        </#if>
        <#if attributes.nomodule?has_content && attributes.nomodule=="true">
            nomodule
        </#if>
        <#if attributes.integrity?has_content>
            integrity="${attributes.integrity}"
        </#if>
        <#if attributes.crossorigin?has_content>
            crossorigin="${attributes.crossorigin}"
        </#if>
        <#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
        <#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
        <#include "/${attributes.templateDir}/${attributes.expandTheme}/nonce.ftl" />>
</@s.compress>
