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
<script type="text/javascript">
	function autoPopulate_${parameters.escapedId?html}(targetElement) {
		<#if parameters.headerKey?? && parameters.headerValue??>
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
<#if parameters.list??>
<select onChange="autoPopulate_${parameters.escapedId?html}(this);"<#rt/>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/css.ftl" />
    <#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
    </#if>
>
	<#if (parameters.headerKey?? && parameters.headerValue??)>
		<option value="${parameters.headerKey?html}">${parameters.headerValue?html}</option>
	</#if>
	<#if parameters.emptyOption?default(false)>
	    <option value=""></option>
	</#if>
    <@s.iterator value="parameters.list">
    <#if parameters.listKey??>
    	<#assign tmpListKey = stack.findString(parameters.listKey) />
    <#else>
    	<#assign tmpListKey = stack.findString('top') />
    </#if>
    <#if parameters.listValue??>
    	<#assign tmpListValue = stack.findString(parameters.listValue) />
    <#else>
    	<#assign tmpListValue = stack.findString('top') />
    </#if>
    <#if parameters.listCssClass??>
        <#if stack.findString(parameters.listCssClass)??>
          <#assign itemCssClass= stack.findString(parameters.listCssClass)/>
        <#else>
          <#assign itemCssClass = ''/>
        </#if>
    </#if>
    <#if parameters.listCssStyle??>
        <#if stack.findString(parameters.listCssStyle)??>
          <#assign itemCssStyle= stack.findString(parameters.listCssStyle)/>
        <#else>
          <#assign itemCssStyle = ''/>
        </#if>
    </#if>
    <#if parameters.listTitle??>
        <#if stack.findString(parameters.listTitle)??>
          <#assign itemTitle= stack.findString(parameters.listTitle)/>
        <#else>
          <#assign itemTitle = ''/>
        </#if>
    </#if>
    <option value="${tmpListKey?html}"<#rt/>
        <#if (parameters.nameValue == tmpListKey)>
 selected="selected"<#rt/>
        </#if>
        <#if itemCssClass?if_exists != "">
 class="${itemCssClass?html}"<#rt/>
        </#if>
        <#if itemCssStyle?if_exists != "">
 style="${itemCssStyle?html}"<#rt/>
        </#if>
        <#if itemTitle?if_exists != "">
 title="${itemTitle?html}"<#rt/>
        </#if>
    ><#t/>
            ${tmpListValue?html}<#t/>
    </option><#lt/>
    </@s.iterator>
</select>
</#if>
