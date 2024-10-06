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
<@s.script>
	function autoPopulate_${attributes.escapedId}(targetElement) {
	<#if attributes.headerKey?? && attributes.headerValue??>
		if (targetElement.options[targetElement.selectedIndex].value == '${attributes.headerKey?js_string}') {
			return;
		}
	</#if>
	<#if attributes.emptyOption!false>
		if (targetElement.options[targetElement.selectedIndex].value == '') {
			return;
		}
	</#if>
		targetElement.form.elements['${attributes.name?js_string}'].value=targetElement.options[targetElement.selectedIndex].value;
	}
</@s.script>
<#include "/${attributes.templateDir}/simple/text.ftl" />
<br />
<#if attributes.list??>
<select onChange="autoPopulate_${attributes.escapedId}(this);"<#rt/>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/css.ftl" />
    <#if attributes.disabled!false>
 disabled="disabled"<#rt/>
    </#if>
>
	<#if (attributes.headerKey?? && attributes.headerValue??)>
		<option value="${attributes.headerKey}">${attributes.headerValue}</option>
	</#if>
	<#if attributes.emptyOption!false>
	    <option value=""></option>
	</#if>
    <@s.iterator value="attributes.list">
    <#if attributes.listKey??>
    	<#assign tmpListKey = stack.findString(attributes.listKey) />
    <#else>
    	<#assign tmpListKey = stack.findString('top') />
    </#if>
    <#if attributes.listValue??>
    	<#assign tmpListValue = stack.findString(attributes.listValue) />
    <#else>
    	<#assign tmpListValue = stack.findString('top') />
    </#if>
    <#if attributes.listCssClass??>
        <#if stack.findString(attributes.listCssClass)??>
          <#assign itemCssClass= stack.findString(attributes.listCssClass)/>
        <#else>
          <#assign itemCssClass = ''/>
        </#if>
    </#if>
    <#if attributes.listCssStyle??>
        <#if stack.findString(attributes.listCssStyle)??>
          <#assign itemCssStyle= stack.findString(attributes.listCssStyle)/>
        <#else>
          <#assign itemCssStyle = ''/>
        </#if>
    </#if>
    <#if attributes.listTitle??>
        <#if stack.findString(attributes.listTitle)??>
          <#assign itemTitle= stack.findString(attributes.listTitle)/>
        <#else>
          <#assign itemTitle = ''/>
        </#if>
    </#if>
    <option value="${tmpListKey}"<#rt/>
        <#if (attributes.nameValue == tmpListKey)>
 selected="selected"<#rt/>
        </#if>
        <#if itemCssClass??>
 class="${itemCssClass}"<#rt/>
        </#if>
        <#if itemCssStyle??>
 style="${itemCssStyle}"<#rt/>
        </#if>
        <#if itemTitle??>
 title="${itemTitle}"<#rt/>
        </#if>
    ><#t/>
            ${tmpListValue}<#t/>
    </option><#lt/>
    </@s.iterator>
</select>
</#if>
