<#--
/*
 * $Id: Action.java 502296 2007-02-01 17:33:39Z niallp $
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
<script type="text/javascript">
	function autoPopulate_${parameters.escapedId?html}(targetElement) {
		<#if parameters.headerKey?exists && parameters.headerValue?exists>
		if (targetElement.options[targetElement.selectedIndex].value == '${parameters.headerKey?html}') {
			return;
		}			
		</#if>
		<#if parameters.emptyOption?default(false)>
		if (targetElement.options[targetElement.selectedIndex].value == '') {
		    return;
		}
		</#if>
		targetElement.form.elements['${parameters.name?html}'].value=targetElement.options[targetElement.selectedIndex].value;
	}
</script>
<#include "/${parameters.templateDir}/simple/text.ftl" />
<br />
<#if parameters.list?exists>
<select onChange="autoPopulate_${parameters.escapedId?html}(this);"<#rt/>
    <#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
    </#if>
>
	<#if (parameters.headerKey?exists && parameters.headerValue?exists)>
		<option value="${parameters.headerKey?html}">${parameters.headerValue?html}</option>
	</#if>
	<#if parameters.emptyOption?default(false)>
	    <option value=""></option>
	</#if>
    <@s.iterator value="parameters.list">
    <#if parameters.listKey?exists>
    	<#assign tmpListKey = stack.findString(parameters.listKey) />
    <#else>
    	<#assign tmpListKey = stack.findString('top') />
    </#if>
    <#if parameters.listValue?exists>
    	<#assign tmpListValue = stack.findString(parameters.listValue) />
    <#else>
    	<#assign tmpListValue = stack.findString('top') />
    </#if>
    <option value="${tmpListKey?html}"<#rt/>
        <#if (parameters.nameValue == tmpListKey)>
 selected="selected"<#rt/>
        </#if>
    ><#t/>
            ${tmpListValue?html}<#t/>
    </option><#lt/>
    </@s.iterator>
</select>
</#if>