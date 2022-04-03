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
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
<link <#include "/${parameters.templateDir}/simple/nonce.ftl" />
<#if parameters.href?has_content>
    href="${parameters.href}"<#rt/>
</#if>
<#if parameters.hreflang?has_content>
    hreflang="${parameters.hreflang}"<#rt/>
</#if>
<#if parameters.rel?has_content>
    rel="${parameters.rel}"<#rt/>
</#if>
<#if parameters.disabled?has_content>
    <#if parameters.disabled=="true">
        disabled<#rt/>
    </#if>
</#if>
<#if parameters.media?has_content>
    media="${parameters.media}"<#rt/>
</#if>
<#if parameters.type?has_content>
    type="${parameters.type}"<#rt/>
</#if>
<#if parameters.title?has_content>
    title="${parameters.title}"<#rt/>
</#if>
<#if parameters.as?has_content>
    as="${parameters.as}"<#rt/>
</#if>
<#if parameters.referrerpolicy?has_content>
    referrerpolicy="${parameters.referrerpolicy}"<#rt/>
</#if>
<#if parameters.sizes?has_content>
    sizes="${parameters.sizes}"<#rt/>
</#if>
<#if parameters.crossorigin?has_content>
    crossorigin="${parameters.crossorigin}"<#rt/>
</#if>
<#if parameters.integrity?has_content>
    integrity="${parameters.integrity}"<#rt/>
</#if>
<#if parameters.importance?has_content>
    importance="${parameters.importance}"<#rt/>
</#if>
>
