<#--
/*
 * $Id$
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
<#assign hasFieldErrors = fieldErrors?? && fieldErrors[parameters.name]??/>
<#if hasFieldErrors>
<#list fieldErrors[parameters.name] as error>
<tr<#rt/>
<#if parameters.id??>
 errorFor="${parameters.id}"<#rt/>
</#if>
>
    <td align="left" valign="top" colspan="2"><#rt/>
        <span class="errorMessage">${error?html}</span><#t/>
    </td><#lt/>
</tr>
</#list>
</#if>
<#if !parameters.labelposition?? && (parameters.form.labelposition)??>
<#assign labelpos = parameters.form.labelposition/>
<#elseif parameters.labelposition??>
<#assign labelpos = parameters.labelposition/>
</#if>
<#if labelpos?default("") == 'top'>
<tr>
    <td colspan="2">
<#if parameters.label??> <label<#t/>
<#if parameters.id??>
 for="${parameters.id?html}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>
<#if parameters.required?default(false) && parameters.requiredPosition?default("right") != 'right'>
        <span class="required">*</span><#t/>
</#if>
${parameters.label?html}<#t/>
<#if parameters.required?default(false) && parameters.requiredPosition?default("right") == 'right'>
 <span class="required">*</span><#t/>
</#if>
:<#t/>
<#if parameters.tooltip??>
    <#include "/${parameters.templateDir}/${parameters.expandTheme}/tooltip.ftl" />
</#if>
</label><#t/>
</#if>
    </td>
</tr>
<tr>
    <td colspan="2">
        <#include "/${parameters.templateDir}/simple/checkbox.ftl" />
<#else>
<tr>
	<td valign="top" align="right">
<#if labelpos?default("") == 'left'>
<#if parameters.label??> <label<#t/>
<#if parameters.id??>
 for="${parameters.id?html}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>
<#if parameters.required?default(false) && parameters.requiredPosition?default("right") != 'right'>
        <span class="required">*</span><#t/>
</#if>
${parameters.label?html}<#t/>
<#if parameters.required?default(false) && parameters.requiredPosition?default("right") == 'right'>
 <span class="required">*</span><#t/>
</#if>
:<#t/>
<#if parameters.tooltip??>
    <#include "/${parameters.templateDir}/${parameters.expandTheme}/tooltip.ftl" />
</#if>
</label><#t/>
</#if>
</#if>
<#if labelpos?default("") == 'right'>
    <#if parameters.required?default(false)>
        <span class="required">*</span><#t/>
    </#if>
    <#if parameters.tooltip??>
        <#include "/${parameters.templateDir}/${parameters.expandTheme}/tooltip.ftl" />
    </#if>
</#if>
    </td>
    <td valign="top" align="left">

<#if labelpos?default("") != 'top'>
                	<#include "/${parameters.templateDir}/simple/checkbox.ftl" />
</#if>                    
<#if labelpos?default("") != 'top' && labelpos?default("") != 'left'>
<#if parameters.label??> <label<#t/>
<#if parameters.id??>
 for="${parameters.id?html}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>${parameters.label?html}</label><#rt/>
</#if>
</#if>
</#if>
 <#include "/${parameters.templateDir}/${parameters.expandTheme}/controlfooter.ftl" /><#nt/>
