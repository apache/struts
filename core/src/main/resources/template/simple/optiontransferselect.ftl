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
<#if !stack.findValue("#optiontransferselect_js_included")??><#t/>
	<script type="text/javascript" src="<@s.url value="${parameters.staticContentPath}/optiontransferselect.js" encode='false' includeParams='none'/>" <#include "/${parameters.templateDir}/simple/nonce.ftl" /> ></script>
	<#assign temporaryVariable = stack.setValue("#optiontransferselect_js_included", "true") /><#t/>
</#if><#t/>
<table>
<tr>
<td>
<#if parameters.leftTitle??><#t/>
	<label for="leftTitle">${parameters.leftTitle}</label><br />
</#if><#t/>
<#global dynamic_attributes_ignore = "right-"/>
<#include "/${parameters.templateDir}/simple/select.ftl" />
<#if parameters.allowUpDownOnLeft!true>
<input type="button"
	onclick="moveOptionDown(document.getElementById('${parameters.id}'), 'key', <#if parameters.headerKey??>'${parameters.headerKey}'<#else>''</#if>);<#if parameters.upDownOnLeftOnclick?has_content><#outputformat 'JavaScript'>${parameters.upDownOnLeftOnclick}</#outputformat>;</#if>"
<#if parameters.leftDownLabel??>
	value="${parameters.leftDownLabel}"
</#if>
/>
<input type="button"
	onclick="moveOptionUp(document.getElementById('${parameters.id}'), 'key', <#if parameters.headerKey??>'${parameters.headerKey}'<#else>''</#if>);<#if parameters.upDownOnLeftOnclick?has_content><#outputformat 'JavaScript'>${parameters.upDownOnLeftOnclick}</#outputformat>;</#if>"
<#if parameters.leftUpLabel??>
	value="${parameters.leftUpLabel}"
</#if>
/>
</#if>

</td>
<td class="tdTransferSelect">
	<#if parameters.allowAddToLeft!true><#t/>
		<#assign addToLeftLabel = parameters.addToLeftLabel!"<-"/><#t/>
		<#if parameters.doubleHeaderKey??><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addToLeftLabel}" onclick="moveSelectedOptions(document.getElementById('${parameters.doubleId}'), document.getElementById('${parameters.id}'), false, '${parameters.doubleHeaderKey}', '');<#if parameters.addToLeftOnclick?has_content><#outputformat 'JavaScript'>${parameters.addToLeftOnclick}</#outputformat>;</#if>" /><br /><br />
		<#else><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addToLeftLabel}" onclick="moveSelectedOptions(document.getElementById('${parameters.doubleId}'), document.getElementById('${parameters.id}'), false, '');<#if parameters.addToLeftOnclick?has_content><#outputformat 'JavaScript'>${parameters.addToLeftOnclick}</#outputformat>;</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
	<#if parameters.allowAddToRight!true><#t/>
		<#assign addToRightLabel=parameters.addToRightLabel!"->" /><#t/>
		<#if parameters.headerKey??><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addToRightLabel}" onclick="moveSelectedOptions(document.getElementById('${parameters.id}'), document.getElementById('${parameters.doubleId}'), false, '${parameters.headerKey}', '');<#if parameters.addToRightOnclick?has_content><#outputformat 'JavaScript'>${parameters.addToRightOnclick}</#outputformat>;</#if>" /><br /><br />
		<#else><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addToRightLabel}" onclick="moveSelectedOptions(document.getElementById('${parameters.id}'), document.getElementById('${parameters.doubleId}'), false, '');<#if parameters.addToRightOnclick?has_content><#outputformat 'JavaScript'>${parameters.addToRightOnclick}</#outputformat>;</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
	<#if parameters.allowAddAllToLeft!true><#t/>
		<#assign addAllToLeftLabel=parameters.addAllToLeftLabel!"<<--" /><#t/>
		<#if parameters.doubleHeaderKey??><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addAllToLeftLabel}" onclick="moveAllOptions(document.getElementById('${parameters.doubleId}'), document.getElementById('${parameters.id}'), false, '${parameters.doubleHeaderKey}', '');<#if parameters.addAllToLeftOnclick?has_content><#outputformat 'JavaScript'>${parameters.addAllToLeftOnclick}</#outputformat>;</#if>" /><br /><br />
		<#else><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addAllToLeftLabel}" onclick="moveAllOptions(document.getElementById('${parameters.doubleId}'), document.getElementById('${parameters.id}'), false, '');<#if parameters.addAllToLeftOnclick?has_content><#outputformat 'JavaScript'>${parameters.addAllToLeftOnclick}</#outputformat>;</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
	<#if parameters.allowAddAllToRight!true><#t/>
		<#assign addAllToRightLabel=parameters.addAllToRightLabel!"-->>" /><#t/>
		<#if parameters.headerKey??><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addAllToRightLabel}" onclick="moveAllOptions(document.getElementById('${parameters.id}'), document.getElementById('${parameters.doubleId}'), false, '${parameters.headerKey}', '');<#if parameters.addAllToRightOnclick?has_content><#outputformat 'JavaScript'>${parameters.addAllToRightOnclick}</#outputformat>;</#if>" /><br /><br />
		<#else><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${addAllToRightLabel}" onclick="moveAllOptions(document.getElementById('${parameters.id}'), document.getElementById('${parameters.doubleId}'), false, '');<#if parameters.addAllToRightOnclick?has_content><#outputformat 'JavaScript'>${parameters.addAllToRightOnclick}</#outputformat>;</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
	<#if parameters.allowSelectAll!true><#t/>
		<#assign selectAllLabel=parameters.selectAllLabel!"<*>" /><#t/>
		<#if parameters.headerKey?? && parameters.doubleHeaderKey??><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${selectAllLabel}" onclick="selectAllOptionsExceptSome(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');selectAllOptionsExceptSome(document.getElementById('${parameters.doubleId}'), 'key', '${parameters.doubleHeaderKey}');<#if parameters.selectAllOnclick?has_content><#outputformat 'JavaScript'>${parameters.selectAllOnclick}</#outputformat>;</#if>" /><br /><br />
		<#elseif parameters.headerKey??><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${selectAllLabel}" onclick="selectAllOptionsExceptSome(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');selectAllOptions(document.getElementById('${parameters.doubleId}'));<#if parameters.selectAllOnclick?has_content><#outputformat 'JavaScript'>${parameters.selectAllOnclick}</#outputformat>;</#if>" /><br /><br />
		<#elseif parameters.doubleHeaderKey??><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${selectAllLabel}" onclick="selectAllOptions(document.getElementById('${parameters.id}'));selectAllOptionsExceptSome(document.getElementById('${parameters.doubleId}'), 'key', '${parameters.doubleHeaderKey}');<#if parameters.selectAllOnclick?has_content><#outputformat 'JavaScript'>${parameters.selectAllOnclick}</#outputformat>;</#if>" /><br /><br />
		<#else><#t/>
			<input type="button"
			<#if parameters.buttonCssClass??><#t/>
			 class="${parameters.buttonCssClass}"
			</#if><#t/>
			<#if parameters.buttonCssStyle??><#t/>
			 style="${parameters.buttonCssStyle}"
			</#if><#t/>
			 value="${selectAllLabel}" onclick="selectAllOptions(document.getElementById('${parameters.id}'));selectAllOptions(document.getElementById('${parameters.doubleId}'));<#if parameters.selectAllOnclick?has_content><#outputformat 'JavaScript'>${parameters.selectAllOnclick}</#outputformat>;</#if>" /><br /><br />
		</#if><#t/>
	</#if><#t/>
</td>
<td>
<#if parameters.rightTitle??><#t/>
	<label for="rightTitle">${parameters.rightTitle}</label><br />
</#if><#t/>
<select
	name="${parameters.doubleName!""}"
	<#if parameters.get("doubleSize")??><#t/>
	size="${parameters.get("doubleSize")}"
	</#if><#t/>
	<#if parameters.doubleDisabled!false><#t/>
	disabled="disabled"
	</#if><#t/>
	<#if parameters.doubleMultiple!false><#t/>
	multiple="multiple"
	</#if><#t/>
	<#if parameters.doubleTabindex??><#t/>
	tabindex="${parameters.tabindex}"
	</#if><#t/>
	<#if parameters.doubleId??><#t/>
	id="${parameters.doubleId}"
	</#if><#t/>
	<#if parameters.doubleCss??><#t/>
	class="${parameters.doubleCss}"
	</#if><#t/>
	<#if parameters.doubleStyle??><#t/>
	style="${parameters.doubleStyle}"
	</#if><#t/>
    <#if parameters.doubleOnclick??><#t/>
    onclick="<#outputformat 'JavaScript'>${parameters.doubleOnclick}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOndblclick??><#t/>
    ondblclick="<#outputformat 'JavaScript'>${parameters.doubleOndblclick}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnmousedown??><#t/>
    onmousedown="<#outputformat 'JavaScript'>${parameters.doubleOnmousedown}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnmouseup??><#t/>
    onmouseup="<#outputformat 'JavaScript'>${parameters.doubleOnmouseup}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnmousemove??><#t/>
    onmousemove="<#outputformat 'JavaScript'>${parameters.doubleOnmousemove}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnmouseover??><#t/>
    onmouseover="<#outputformat 'JavaScript'>${parameters.doubleOnmouseover}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnmouseout??><#t/>
    onmouseout="<#outputformat 'JavaScript'>${parameters.doubleOnmouseout}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnfocus??><#t/>
    onfocus="<#outputformat 'JavaScript'>${parameters.doubleOnfocus}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnblur??><#t/>
    onblur="<#outputformat 'JavaScript'>${parameters.doubleOnblur}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnkeypress??><#t/>
    onkeypress="<#outputformat 'JavaScript'>${parameters.doubleOnkeypress}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnKeydown??><#t/>
    onkeydown="<#outputformat 'JavaScript'>${parameters.doubleOnkeydown}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnkeyup??><#t/>
    onkeyup="<#outputformat 'JavaScript'>${parameters.doubleOnkeyup}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnselect??><#t/>
    onselect="<#outputformat 'JavaScript'>${parameters.doubleOnselect}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleOnchange??><#t/>
    onchange="<#outputformat 'JavaScript'>${parameters.doubleOnchange}</#outputformat>"
    </#if><#t/>
    <#if parameters.doubleAccesskey??><#t/>
    accesskey="<#outputformat 'JavaScript'>${parameters.doubleAccesskey}</#outputformat>"
    </#if>
	<#include "/${parameters.templateDir}/${parameters.expandTheme}/prefixed-dynamic-attributes.ftl" />
	<@prefixedDynamicAttributes prefix="right-"/>
>
	<#if parameters.doubleHeaderKey?? && parameters.doubleHeaderValue??><#t/>
    <option value="${parameters.doubleHeaderKey}">${parameters.doubleHeaderValue}</option>
	</#if><#t/>
	<#if parameters.doubleEmptyOption!false><#t/>
    <option value=""></option>
	</#if><#t/>
	<@s.iterator value="parameters.doubleList"><#t/>
        <#if parameters.doubleListKey??><#t/>
            <#assign doubleItemKey = stack.findValue(parameters.doubleListKey) /><#t/>
        <#else><#t/>
            <#assign doubleItemKey = stack.findValue('top') /><#t/>
        </#if><#t/>
        <#assign doubleItemKeyStr = doubleItemKey.toString() /><#t/>
        <#if parameters.doubleListValue??><#t/>
            <#assign doubleItemValue = stack.findString(parameters.doubleListValue)!"" /><#t/>
        <#else><#t/>
            <#assign doubleItemValue = stack.findString('top') /><#t/>
        </#if><#t/>
    	<option value="${doubleItemKeyStr}"<#rt/>
        <#if tag.contains(parameters.doubleNameValue, doubleItemKey)><#t/>
 		selected="selected"<#rt/>
        </#if><#t/>
    	>${doubleItemValue}</option><#lt/>
	</@s.iterator><#t/>
</select>
<#if parameters.doubleMultiple!false>
<input type="hidden" id="__multiselect_${parameters.doubleId}" name="__multiselect_${parameters.doubleName!""}" value=""<#rt/>
<#if parameters.doubleDisabled!false>
 disabled="disabled"<#rt/>
</#if>
 />
</#if>
<#if parameters.allowUpDownOnRight!true>
<input type="button"
	onclick="moveOptionDown(document.getElementById('${parameters.doubleId}'), 'key', <#if parameters.doubleHeaderKey??>'${parameters.doubleHeaderKey}'<#else>''</#if>);<#if parameters.upDownOnRightOnclick?has_content><#outputformat 'JavaScript'>${parameters.upDownOnRightOnclick}</#outputformat>;</#if>"
<#if parameters.rightDownLabel??>
	value="${parameters.rightDownLabel}"
</#if>
/>
<input type="button"
	onclick="moveOptionUp(document.getElementById('${parameters.doubleId}'), 'key', <#if parameters.doubleHeaderKey??>'${parameters.doubleHeaderKey}'<#else>''</#if>);<#if parameters.upDownOnRightOnclick?has_content><#outputformat 'JavaScript'>${parameters.upDownOnRightOnclick}</#outputformat>;</#if>"
<#if parameters.rightUpLabel??>
	value="${parameters.rightUpLabel}"
</#if>
/>
</#if>
</td>
</tr>
</table>
