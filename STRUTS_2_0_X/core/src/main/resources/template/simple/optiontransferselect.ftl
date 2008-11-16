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
<#if !stack.findValue("#optiontransferselect_js_included")?exists><#t/>
	<script type="text/javascript" src="<@s.url value="/struts/optiontransferselect.js" encode='false' includeParams='none'/>"></script>
	<#assign temporaryVariable = stack.setValue("#optiontransferselect_js_included", "true") /><#t/>
</#if><#t/>
<table border="0">
<tr>
<td>
<#if parameters.leftTitle?exists><#t/>
	<label for="leftTitle">${parameters.leftTitle}</label><br />
</#if><#t/>
<#include "/${parameters.templateDir}/simple/select.ftl" /> 
<#if parameters.allowUpDownOnLeft?default(true)>
<input type="button" 
	onclick="moveOptionDown(document.getElementById('${parameters.id}'), 'key', <#if parameters.headerKey?exists>'${parameters.headerKey}'<#else>''</#if>);<#if parameters.upDownOnLeftOnclick?has_content>${parameters.upDownOnLeftOnclick};</#if>"
<#if parameters.leftDownLabel?exists>
	value="${parameters.leftDownLabel?html}"
</#if>
/>
<input type="button"
	onclick="moveOptionUp(document.getElementById('${parameters.id}'), 'key', <#if parameters.headerKey?exists>'${parameters.headerKey}'<#else>''</#if>);<#if parameters.upDownOnLeftOnclick?has_content>${parameters.upDownOnLeftOnclick};</#if>"
<#if parameters.leftUpLabel?exists>
	value="${parameters.leftUpLabel?html}"
</#if>
/>
</#if>

</td>
<td valign="middle" align="center">
	<#if parameters.allowAddToLeft?default(true)><#t/>
		<#assign addToLeftLabel = parameters.addToLeftLabel?default("<-")?html/><#t/>
		<#if parameters.doubleHeaderKey?exists><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${addToLeftLabel}" onclick="moveSelectedOptions(document.getElementById('${parameters.doubleId?html}'), document.getElementById('${parameters.id?html}'), false, '${parameters.doubleHeaderKey}', '');<#if parameters.addToLeftOnclick?has_content>${parameters.addToLeftOnclick};</#if>" /><br /><br />
		<#else><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${addToLeftLabel}" onclick="moveSelectedOptions(document.getElementById('${parameters.doubleId?html}'), document.getElementById('${parameters.id?html}'), false, '');<#if parameters.addToLeftOnclick?has_content>${parameters.addToLeftOnclick};</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
	<#if parameters.allowAddToRight?default(true)><#t/>
		<#assign addToRightLabel=parameters.addToRightLabel?default("->")?html /><#t/>
		<#if parameters.headerKey?exists><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${addToRightLabel}" onclick="moveSelectedOptions(document.getElementById('${parameters.id?html}'), document.getElementById('${parameters.doubleId?html}'), false, '${parameters.headerKey}', '');<#if parameters.addToRightOnclick?has_content>${parameters.addToRightOnclick};</#if>" /><br /><br />
		<#else><#t/>
			<input type="button"
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${addToRightLabel}" onclick="moveSelectedOptions(document.getElementById('${parameters.id?html}'), document.getElementById('${parameters.doubleId?html}'), false, '');<#if parameters.addToRightOnclick?has_content>${parameters.addToRightOnclick};</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
	<#if parameters.allowAddAllToLeft?default(true)><#t/>
		<#assign addAllToLeftLabel=parameters.addAllToLeftLabel?default("<<--")?html /><#t/>
		<#if parameters.doubleHeaderKey?exists><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addAllToLeftLabel}" onclick="moveAllOptions(document.getElementById('${parameters.doubleId?html}'), document.getElementById('${parameters.id?html}'), false, '${parameters.doubleHeaderKey}', '');<#if parameters.addAllToLeftOnclick?has_content>${parameters.addAllToLeftOnclick};</#if>" /><br /><br />
		<#else><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${addAllToLeftLabel}" onclick="moveAllOptions(document.getElementById('${parameters.doubleId?html}'), document.getElementById('${parameters.id?html}'), false, '');<#if parameters.addAllToLeftOnclick?has_content>${parameters.addAllToLeftOnclick};</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
	<#if parameters.allowAddAllToRight?default(true)><#t/>
		<#assign addAllToRightLabel=parameters.addAllToRightLabel?default("-->>")?html /><#t/>
		<#if parameters.headerKey?exists><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${addAllToRightLabel}" onclick="moveAllOptions(document.getElementById('${parameters.id?html}'), document.getElementById('${parameters.doubleId?html}'), false, '${parameters.headerKey}', '');<#if parameters.addAllToRightOnclick?has_content>${parameters.addAllToRightOnclick};</#if>" /><br /><br />	
		<#else><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${addAllToRightLabel}" onclick="moveAllOptions(document.getElementById('${parameters.id?html}'), document.getElementById('${parameters.doubleId?html}'), false, '');<#if parameters.addAllToRightOnclick?has_content>${parameters.addAllToRightOnclick};</#if>" /><br /><br />	
		</#if><#t/>
	</#if><#t/>
	<#if parameters.allowSelectAll?default(true)><#t/>
		<#assign selectAllLabel=parameters.selectAllLabel?default("<*>")?html /><#t/>
		<#if parameters.headerKey?exists && parameters.doubleHeaderKey?exists><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${selectAllLabel}" onclick="selectAllOptionsExceptSome(document.getElementById('${parameters.id?html}'), 'key', '${parameters.headerKey}');selectAllOptionsExceptSome(document.getElementById('${parameters.doubleId?html}'), 'key', '${parameters.doubleHeaderKey}');<#if parameters.selectAllOnclick?has_content>${parameters.selectAllOnclick};</#if>" /><br /><br />
		<#elseif parameters.headerKey?exists><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${selectAllLabel}" onclick="selectAllOptionsExceptSome(document.getElementById('${parameters.id?html}'), 'key', '${parameters.headerKey}');selectAllOptions(document.getElementById('${parameters.doubleId?html}'));<#if parameters.selectAllOnclick?has_content>${parameters.selectAllOnclick};</#if>" /><br /><br />
		<#elseif parameters.doubleHeaderKey?exists><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${selectAllLabel}" onclick="selectAllOptions(document.getElementById('${parameters.id?html}'));selectAllOptionsExceptSome(document.getElementById('${parameters.doubleId?html}'), 'key', '${parameters.doubleHeaderKey}');<#if parameters.selectAllOnclick?has_content>${parameters.selectAllOnclick};</#if>" /><br /><br />
		<#else><#t/>
			<input type="button" 
			<#if parameters.buttonCssClass?exists><#t/>
			 class="${parameters.buttonCssClass?html}" 
			</#if><#t/>
			<#if parameters.buttonCssStyle?exists><#t/>
			 style="${parameters.buttonCssStyle?html}"
			</#if><#t/>
			 value="${selectAllLabel}" onclick="selectAllOptions(document.getElementById('${parameters.id?html}'));selectAllOptions(document.getElementById('${parameters.doubleId?html}'));<#if parameters.selectAllOnclick?has_content>${parameters.selectAllOnclick};</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
</td>
<td>
<#if parameters.rightTitle?exists><#t/>
	<label for="rightTitle">${parameters.rightTitle}</label><br />
</#if><#t/>
<select 
	name="${parameters.doubleName?default("")?html}"
	<#if parameters.get("doubleSize")?exists><#t/>
	size="${parameters.get("doubleSize")?html}"		
	</#if><#t/>
	<#if parameters.doubleDisabled?default(false)><#t/>
	disabled="disabled"
	</#if><#t/>
	<#if parameters.doubleMultiple?default(false)><#t/>
	multiple="multiple"
	</#if><#t/>
	<#if parameters.doubleTabindex?exists><#t/>
	tabindex="${parameters.tabindex?html}"
	</#if><#t/>
	<#if parameters.doubleId?exists><#t/>
	id="${parameters.doubleId?html}"
	</#if><#t/>
	<#if parameters.doubleCss?exists><#t/>
	class="${parameters.doubleCss?html}"
	</#if><#t/>
	<#if parameters.doubleStyle?exists><#t/>
	style="${parameters.doubleStyle?html}"
	</#if><#t/>
    <#if parameters.doubleOnclick?exists><#t/>
    onclick="${parameters.doubleOnclick?html}"
    </#if><#t/>
    <#if parameters.doubleOndblclick?exists><#t/>
    ondblclick="${parameters.doubleOndblclick?html}"
    </#if><#t/>
    <#if parameters.doubleOnmousedown?exists><#t/>
    onmousedown="${parameters.doubleOnmousedown?html}"
    </#if><#t/>
    <#if parameters.doubleOnmouseup?exists><#t/>
    onmouseup="${parameters.doubleMnmouseup?html}"
    </#if><#t/>
    <#if parameters.doubleOnmousemove?exists><#t/>
    onmousemove="${parameters.doubleOnmousemove?html}"
    </#if><#t/>
    <#if parameters.doubleOnmouseout?exists><#t/>
    onmouseout="${parameters.doubleOnmouseout?html}"
    </#if><#t/>
    <#if parameters.doubleOnfocus?exists><#t/>
    onfocus="${parameters.doubleOnfocus?html}"
    </#if><#t/>
    <#if parameters.doubleOnblur?exists><#t/>
    onblur="${parameters.doubleOnblur?html}"
    </#if><#t/>
    <#if parameters.doubleOnkeypress?exists><#t/>
    onkeypress="${parameters.doubleOnkeypress?html}"
    </#if><#t/>
    <#if parameters.doubleOnKeydown?exists><#t/>
    onkeydown="${parameters.doubleOnkeydown?html}"
    </#if><#t/>
    <#if parameters.doubleOnkeyup?exists><#t/>
    onkeyup="${parameters.doubleOnkeyup?html}"
    </#if><#t/>
    <#if parameters.doubleOnselect?exists><#t/>
    onselect="${parameters.doubleOnselect?html}"
    </#if><#t/>
    <#if parameters.doubleOnchange?exists><#t/>
    onchange="${parameters.doubleOnchange?html}"
    </#if><#t/>
    <#if parameters.doubleAccesskey?exists><#t/>
    accesskey="${parameters.doubleAccesskey?html}"
    </#if>
>
	<#if parameters.doubleHeaderKey?exists && parameters.doubleHeaderValue?exists><#t/>
    <option value="${parameters.doubleHeaderKey?html}">${parameters.doubleHeaderValue?html}</option>
	</#if><#t/>
	<#if parameters.doubleEmptyOption?default(false)><#t/>
    <option value=""></option>
	</#if><#t/>
	<@s.iterator value="parameters.doubleList"><#t/>
        <#if parameters.doubleListKey?exists><#t/>
            <#assign doubleItemKey = stack.findValue(parameters.doubleListKey) /><#t/>
        <#else><#t/>
            <#assign doubleItemKey = stack.findValue('top') /><#t/>
        </#if><#t/>
        <#assign doubleItemKeyStr = doubleItemKey.toString() /><#t/>
        <#if parameters.doubleListValue?exists><#t/>
            <#assign doubleItemValue = stack.findString(parameters.doubleListValue) /><#t/>
        <#else><#t/>
            <#assign doubleItemValue = stack.findString('top') /><#t/>
        </#if><#t/>
    	<option value="${doubleItemKeyStr?html}"<#rt/>
        <#if tag.contains(parameters.doubleNameValue, doubleItemKey)><#t/>
 		selected="selected"<#rt/>
        </#if><#t/>
    	>${doubleItemValue?html}</option><#lt/>
	</@s.iterator><#t/>
</select>
<#if parameters.allowUpDownOnRight?default(true)>
<input type="button" 
	onclick="moveOptionDown(document.getElementById('${parameters.doubleId}'), 'key', <#if parameters.doubleHeaderKey?exists>'${parameters.doubleHeaderKey}'<#else>''</#if>);<#if parameters.upDownOnRightOnclick?has_content>${parameters.upDownOnRightOnclick};</#if>"
<#if parameters.rightDownLabel?exists>
	value="${parameters.rightDownLabel?html}"
</#if>
/>
<input type="button" 
	onclick="moveOptionUp(document.getElementById('${parameters.doubleId}'), 'key', <#if parameters.doubleHeaderKey?exists>'${parameters.doubleHeaderKey}'<#else>''</#if>);<#if parameters.upDownOnRightOnclick?has_content>${parameters.upDownOnRightOnclick};</#if>"
<#if parameters.rightUpLabel?exists>
	value="${parameters.rightUpLabel?html}"
</#if>
/>
</#if>
</td>
</tr>
</table>