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
<link<#rt/>
<#if attributes.rel?has_content>
 rel="${attributes.rel}"<#rt/>
</#if>
<#if attributes.type?has_content>
 type="${attributes.type}"<#rt/>
</#if>
<#if attributes.href?has_content>
 href="${attributes.href}"<#rt/>
</#if>
<#if attributes.hreflang?has_content>
 hreflang="${attributes.hreflang}"<#rt/>
</#if>
<#if attributes.disabled?has_content && attributes.disabled == "true">
 disabled<#rt/>
</#if>
<#if attributes.media?has_content>
 media="${attributes.media}"<#rt/>
</#if>
<#if attributes.title?has_content>
 title="${attributes.title}"<#rt/>
</#if>
<#if attributes.as?has_content>
 as="${attributes.as}"<#rt/>
</#if>
<#if attributes.referrerpolicy?has_content>
 referrerpolicy="${attributes.referrerpolicy}"<#rt/>
</#if>
<#if attributes.sizes?has_content>
 sizes="${attributes.sizes}"<#rt/>
</#if>
<#if attributes.crossorigin?has_content>
 crossorigin="${attributes.crossorigin}"<#rt/>
</#if>
<#if attributes.integrity?has_content>
 integrity="${attributes.integrity}"<#rt/>
</#if>
<#if attributes.importance?has_content>
 importance="${attributes.importance}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" /><#rt/>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" /><#rt/>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/nonce.ftl" /><#rt/>
/>
