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
<#--
	Always include elements to show errors. They may be filled later via AJAX.
-->
<#assign hasFieldErrors = parameters.name?? && fieldErrors?? && fieldErrors[parameters.name]??/>
<#if (parameters.errorposition!"top") == 'top'>
<tr errorFor="${parameters.id}">
    <td class="tdErrorMessage" colspan="2" data-error-for-fieldname="${parameters.name}"><#rt/>
        <#if hasFieldErrors>
            <#list fieldErrors[parameters.name] as error>
                <div class="errorMessage">${error?html}</div><#t/>
            </#list>
        </#if>
    </td><#lt/>
</tr>
</#if>
<#if !parameters.labelposition?? && (parameters.form.labelposition)??>
<#assign labelpos = parameters.form.labelposition/>
<#elseif parameters.labelposition??>
<#assign labelpos = parameters.labelposition/>
</#if>
<#--
	if the label position is top,
	then give the label it's own row in the table
-->
<tr>
<#if (labelpos!"") == 'top'>
    <td class="tdLabelTop" colspan="2"><#rt/>
<#else>
    <td class="tdLabel"><#rt/>
</#if>
<#if parameters.label??>
    <label <#t/>
<#if parameters.id??>
        for="${parameters.id?html}" <#t/>
</#if>
<#if hasFieldErrors>
        class="errorLabel"<#t/>
<#else>
        class="label"<#t/>
</#if>
    ><#t/>
<#if parameters.required!false && parameters.requiredPosition!"right" != 'right'>
        <span class="required">*</span><#t/>
</#if>
${parameters.label?html}<#t/>
<#if parameters.required!false && parameters.requiredPosition!"right" == 'right'>
 <span class="required">*</span><#t/>
</#if>
${parameters.labelseparator!":"?html}<#t/>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/tooltip.ftl" />
</label><#t/>
</#if>
    </td><#lt/>
<#-- add the extra row -->
<#if (labelpos!"") == 'top'>
</tr>
<tr>
</#if>
