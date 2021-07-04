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
NOTE: The 'header' stuff that follows is in this one file for checkbox due to the fact
that for checkboxes we do not want the label field to show up as checkboxes handle their own
lables
-->
<#assign hasFieldErrors = fieldErrors?? && fieldErrors.get(parameters.name)??/>
<div <#rt/><#if parameters.id??>id="wwgrp_${parameters.id}"<#rt/></#if> class="wwgrp">

<#if hasFieldErrors>
<div <#rt/><#if parameters.id??>id="wwerr_${parameters.id}"<#rt/></#if> class="wwerr">
<#list fieldErrors.get(parameters.name) as error>
    <div<#rt/>
    <#if parameters.id??>
     errorFor="${parameters.id}"<#rt/>
    </#if>
    class="errorMessage">
             ${error}
    </div><#t/>
</#list>
</div><#t/>
</#if>
<#if !parameters.labelPosition?? && (parameters.form.labelPosition)??>
<#assign labelPos = parameters.form.labelPosition/>
<#elseif parameters.labelPosition??>
<#assign labelPos = parameters.labelPosition/>
</#if>
<#if (labelPos!"") == 'left'>
<span <#rt/>
<#if parameters.id??>id="wwlbl_${parameters.id}"<#rt/></#if> class="wwlbl">
<label<#t/>
<#if parameters.id??>
 for="${parameters.id}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="label"<#rt/>
</#if>
>${parameters.label}</label><#rt/>
</span>
</#if>

<#if (labelPos!"top") == 'top'>
<div <#rt/>
<#else>
<span <#rt/>
</#if>
<#if parameters.id??>id="wwctrl_${parameters.id}"<#rt/></#if> class="wwctrl">

<#if parameters.required!false>
        <span class="required">*</span><#t/>
</#if>

<#include "/${parameters.templateDir}/simple/checkbox.ftl" />
<#if (labelPos!"") != 'left'>
<#if (labelPos!"top") == 'top'>
</div> <#rt/>
<#else>
</span>  <#rt/>
</#if>
<#if parameters.label??>
<#if (labelPos!"top") == 'top'>
<div <#rt/>
<#else>
<span <#rt/>
</#if>
<#if parameters.id??>id="wwlbl_${parameters.id}"<#rt/></#if> class="wwlbl">
<label<#t/>
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
<#if parameters.label??>
<#if (labelPos!"top") == 'top'>
</div> <#rt/>
<#else>
</span> <#rt/>
</#if>
</#if>
</div>
