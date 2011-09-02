<#--
/*
 * $Id: form.ftl 590812 2007-10-31 20:32:54Z apetrelli $
 *
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
<#if (parameters.validate?default(false) == false)><#rt/>
    <#if parameters.onsubmit??><#rt/>
        ${tag.addParameter('onsubmit', "${parameters.onsubmit}") }
    </#if>
</#if>
<form<#rt/>
<#if parameters.id??>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.name??>
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.onsubmit??>
 onsubmit="${parameters.onsubmit?html}"<#rt/>
</#if>
<#if parameters.onreset??>
 onreset="${parameters.onreset?html}"<#rt/>
</#if>
<#if parameters.action??>
 action="${parameters.action?html}"<#rt/>
</#if>
<#if parameters.target??>
 target="${parameters.target?html}"<#rt/>
</#if>
<#if parameters.method??>
 method="${parameters.method?html}"<#rt/>
<#else>
 method="post"<#rt/>
</#if>
<#if parameters.enctype??>
 enctype="${parameters.enctype?html}"<#rt/>
</#if>
<#if parameters.cssClass??>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle??>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.title??>
 title="${parameters.title?html}"<#rt/>
</#if>
<#if parameters.acceptcharset??>
 accept-charset="${parameters.acceptcharset?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/dynamic-attributes.ftl" />