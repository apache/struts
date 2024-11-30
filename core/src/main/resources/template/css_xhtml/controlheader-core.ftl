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
<#--
	Only show message if errors are available.
	This will be done if ActionSupport is used.
-->
<#assign hasFieldErrors = attributes.name?? && fieldErrors?? && fieldErrors.get(attributes.name)??/>
<div <#rt/><#if attributes.id??>id="wwgrp_${attributes.id}"<#rt/></#if> class="wwgrp">

<#if (attributes.errorposition!"top") == 'top'>
<#if hasFieldErrors>
<div <#rt/><#if attributes.id??>id="wwerr_${attributes.id}"<#rt/></#if> class="wwerr">
<#list fieldErrors.get(attributes.name) as error>
    <div<#rt/>
    <#if attributes.id??>
     errorFor="${attributes.id}"<#rt/>
    </#if>
    class="errorMessage">
             ${error}
    </div><#t/>
</#list>
</div><#t/>
</#if>
</#if>

<#if !attributes.labelPosition?? && (attributes.form.labelPosition)??>
<#assign labelPos = attributes.form.labelPosition/>
<#elseif attributes.labelPosition??>
<#assign labelPos = attributes.labelPosition/>
</#if>
<#if attributes.label??>
<#if (labelPos!"top") == 'top'>
<div <#rt/>
<#else>
<span <#rt/>
</#if>
<#if attributes.id??>id="wwlbl_${attributes.id}"<#rt/></#if> class="wwlbl">
    <label <#t/>
<#if attributes.id??>
        for="${attributes.id}" <#t/>
</#if>
<#if hasFieldErrors>
        class="errorLabel"<#t/>
<#else>
        class="label"<#t/>
</#if>
    ><#t/>
<#if attributes.required!false>
        <span class="required">*</span><#t/>
</#if>
        ${attributes.label}${attributes.labelseparator!":"}
<#include "/${attributes.templateDir}/xhtml/tooltip.ftl" />
	</label><#t/>
<#if (labelPos!"top") == 'top'>
</div> <br /><#rt/>
<#else>
</span> <#rt/>
</#if>
</#if>
