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
<#assign hasFieldErrors = fieldErrors?? && fieldErrors.get(attributes.name)??/>
<#if hasFieldErrors>
<#list fieldErrors.get(attributes.name) as error>
<tr<#rt/>
<#if attributes.id??>
 errorFor="${attributes.id}"<#rt/>
</#if>
>
    <td class="tdCheckboxErrorMessage" colspan="2"><#rt/>
        <span class="errorMessage">${error}</span><#t/>
    </td><#lt/>
</tr>
</#list>
</#if>
<#if !attributes.labelPosition?? && (attributes.form.labelPosition)??>
<#assign labelPos = attributes.form.labelPosition/>
<#elseif attributes.labelPosition??>
<#assign labelPos = attributes.labelPosition/>
</#if>
<#if (labelPos!"") == 'top'>
<tr>
    <td colspan="2">
<#if attributes.label??> <label<#t/>
<#if attributes.id??>
 for="${attributes.id}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>
<#if attributes.required!false && attributes.requiredPosition!"right" != 'right'>
        <span class="required">*</span><#t/>
</#if>
${attributes.label}<#t/>
<#if attributes.required!false && attributes.requiredPosition!"right" == 'right'>
 <span class="required">*</span><#t/>
</#if>
:<#t/>
<#if attributes.tooltip??>
    <#include "/${attributes.templateDir}/${attributes.expandTheme}/tooltip.ftl" />
</#if>
</label><#t/>
</#if>
    </td>
</tr>
<tr>
    <td colspan="2">
        <#include "/${attributes.templateDir}/simple/checkbox.ftl" />
<#else>
<tr>
	<td class="tdCheckboxLabel">
<#if (labelPos!"") == 'left'>
<#if attributes.label??> <label<#t/>
<#if attributes.id??>
 for="${attributes.id}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>
<#if attributes.required!false && attributes.requiredPosition!"right" != 'right'>
        <span class="required">*</span><#t/>
</#if>
${attributes.label}<#t/>
<#if attributes.required!false && attributes.requiredPosition!"right" == 'right'>
 <span class="required">*</span><#t/>
</#if>
:<#t/>
<#if attributes.tooltip??>
    <#include "/${attributes.templateDir}/${attributes.expandTheme}/tooltip.ftl" />
</#if>
</label><#t/>
</#if>
</#if>
<#if (labelPos!"") == 'right'>
    <#if attributes.required!false>
        <span class="required">*</span><#t/>
    </#if>
    <#if attributes.tooltip??>
        <#include "/${attributes.templateDir}/${attributes.expandTheme}/tooltip.ftl" />
    </#if>
</#if>
    </td>
    <td class="tdCheckboxInput">
<#if (labelPos!"") != 'top'>
 <#include "/${attributes.templateDir}/simple/checkbox.ftl" />
</#if>
<#if (labelPos!"") != 'top' && (labelPos!"") != 'left'>
<#if attributes.label??><label<#rt/>
<#if attributes.id??>
 for="${attributes.id}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>${attributes.label}</label><#rt/>
</#if>
</#if>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/controlfooter.ftl" /><#nt/>
