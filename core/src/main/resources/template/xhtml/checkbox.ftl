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
<#assign hasFieldErrors = fieldErrors?? && fieldErrors.get(parameters.name)??/>
<#if hasFieldErrors>
<#list fieldErrors.get(parameters.name) as error>
<tr<#rt/>
<#if parameters.id??>
 errorFor="${parameters.id}"<#rt/>
</#if>
>
    <td class="tdCheckboxErrorMessage" colspan="2"><#rt/>
        <span class="errorMessage">${error}</span><#t/>
    </td><#lt/>
</tr>
</#list>
</#if>
<#if !parameters.labelPosition?? && (parameters.form.labelPosition)??>
<#assign labelPos = parameters.form.labelPosition/>
<#elseif parameters.labelPosition??>
<#assign labelPos = parameters.labelPosition/>
</#if>
<#if (labelPos!"") == 'top'>
<tr>
    <td colspan="2">
<#if parameters.label??> <label<#t/>
<#if parameters.id??>
 for="${parameters.id}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>
<#if parameters.required!false && parameters.requiredPosition!"right" != 'right'>
        <span class="required">*</span><#t/>
</#if>
${parameters.label}<#t/>
<#if parameters.required!false && parameters.requiredPosition!"right" == 'right'>
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
	<td class="tdCheckboxLabel">
<#if (labelPos!"") == 'left'>
<#if parameters.label??> <label<#t/>
<#if parameters.id??>
 for="${parameters.id}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>
<#if parameters.required!false && parameters.requiredPosition!"right" != 'right'>
        <span class="required">*</span><#t/>
</#if>
${parameters.label}<#t/>
<#if parameters.required!false && parameters.requiredPosition!"right" == 'right'>
 <span class="required">*</span><#t/>
</#if>
:<#t/>
<#if parameters.tooltip??>
    <#include "/${parameters.templateDir}/${parameters.expandTheme}/tooltip.ftl" />
</#if>
</label><#t/>
</#if>
</#if>
<#if (labelPos!"") == 'right'>
    <#if parameters.required!false>
        <span class="required">*</span><#t/>
    </#if>
    <#if parameters.tooltip??>
        <#include "/${parameters.templateDir}/${parameters.expandTheme}/tooltip.ftl" />
    </#if>
</#if>
    </td>
    <td class="tdCheckboxInput">

<#if (labelPos!"") != 'top'>
 <#include "/${parameters.templateDir}/simple/checkbox.ftl" />
</#if>
<#if (labelPos!"") != 'top' && (labelPos!"") != 'left'>
<#if parameters.label??> <label<#t/>
<#if parameters.id??>
 for="${parameters.id}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>${parameters.label}</label><#rt/>
</#if>
</#if>
</#if>
 <#include "/${parameters.templateDir}/${parameters.expandTheme}/controlfooter.ftl" /><#nt/>
