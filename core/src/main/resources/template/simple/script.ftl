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
<script <#rt/>
<#if parameters.nonce?has_content>
 nonce="${parameters.nonce}"<#rt/>
</#if>
<#if parameters.async?has_content>
 <#if parameters.async=="true">
  async<#rt/>
 </#if>
</#if>
<#if parameters.charset?has_content>
 charset="${parameters.charset}"<#rt/>
</#if>
<#if parameters.defer?has_content>
 <#if parameters.defer=="true">
  defer<#rt/>
 </#if>
</#if>
<#if parameters.src?has_content>
 src="${parameters.src}"<#rt/>
</#if>
<#if parameters.type?has_content>
 type="${parameters.type}"<#rt/>
</#if>
<#if parameters.name?has_content>
 name="${parameters.name}"<#rt/>
</#if>
<#if parameters.referrerpolicy?has_content>
 referrerpolicy="${parameters.referrerpolicy}"<#rt/>
</#if>
<#if parameters.nomodule?has_content>
 <#if parameters.nomodule=="true">
  nomodule<#rt/>
 </#if>
</#if>
<#if parameters.integrity?has_content>
 integrity="${parameters.integrity}"<#rt/>
</#if>
<#if parameters.crossorigin?has_content>
 crossorigin="${parameters.crossorigin}"<#rt/>
</#if>
>
