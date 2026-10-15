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
	<@s.script src="${base}${attributes.staticContentPath}/optiontransferselect.js" />
	<#assign temporaryVariable = stack.setValue("#optiontransferselect_js_included", "true") /><#t/>
</#if><#t/>
<#--
  Buttons carry their action, both select ids and the header keys in data-* attributes; optiontransferselect.js binds
  them, so the tag needs no framework-generated inline JavaScript under a Content-Security-Policy. A developer-supplied
  *Onclick attribute is still rendered as onclick and runs after the framework action. The attribute line uses <#rt/>,
  not <#t/>, to keep the space that separates it from the preceding attribute.
-->
<#macro transferButton action onclick><#t/>
 data-struts-optiontransferselect="${action}" data-struts-left="${attributes.id}" data-struts-right="${attributes.doubleId!}"<#rt/>
<#if attributes.headerKey??> data-struts-header-key="${attributes.headerKey}"</#if><#t/>
<#if attributes.doubleHeaderKey??> data-struts-double-header-key="${attributes.doubleHeaderKey}"</#if><#t/>
<#if onclick?has_content> onclick="<#outputformat 'JavaScript'>${onclick}</#outputformat>;"</#if><#t/>
</#macro>
<table>
<tr>
<td>
<#if attributes.leftTitle??><#t/>
	<label for="leftTitle">${attributes.leftTitle}</label><br />
</#if><#t/>
<#global dynamic_attributes_ignore = "right-"/>
<#include "/${attributes.templateDir}/simple/select.ftl" />
<#if attributes.allowUpDownOnLeft!true>
<input type="button"
	<@transferButton action="leftDown" onclick=attributes.upDownOnLeftOnclick!""/>
<#if attributes.leftDownLabel??>
	value="${attributes.leftDownLabel}"
</#if>
/>
<input type="button"
	<@transferButton action="leftUp" onclick=attributes.upDownOnLeftOnclick!""/>
<#if attributes.leftUpLabel??>
	value="${attributes.leftUpLabel}"
</#if>
/>
</#if>

</td>
<td class="tdTransferSelect">
	<#if attributes.allowAddToLeft!true><#t/>
		<#assign addToLeftLabel=attributes.addToLeftLabel!"<-" /><#t/>
		<input type="button"
		<#if attributes.buttonCssClass??><#t/>
		 class="${attributes.buttonCssClass}"
		</#if><#t/>
		<#if attributes.buttonCssStyle??><#t/>
		 style="${attributes.buttonCssStyle}"
		</#if><#t/>
		 value="${addToLeftLabel}"<@transferButton action="addToLeft" onclick=attributes.addToLeftOnclick!""/> /><br /><br />
	</#if><#t/>
	<#if attributes.allowAddToRight!true><#t/>
		<#assign addToRightLabel=attributes.addToRightLabel!"->" /><#t/>
		<input type="button"
		<#if attributes.buttonCssClass??><#t/>
		 class="${attributes.buttonCssClass}"
		</#if><#t/>
		<#if attributes.buttonCssStyle??><#t/>
		 style="${attributes.buttonCssStyle}"
		</#if><#t/>
		 value="${addToRightLabel}"<@transferButton action="addToRight" onclick=attributes.addToRightOnclick!""/> /><br /><br />
	</#if><#t/>
	<#if attributes.allowAddAllToLeft!true><#t/>
		<#assign addAllToLeftLabel=attributes.addAllToLeftLabel!"<<--" /><#t/>
		<input type="button"
		<#if attributes.buttonCssClass??><#t/>
		 class="${attributes.buttonCssClass}"
		</#if><#t/>
		<#if attributes.buttonCssStyle??><#t/>
		 style="${attributes.buttonCssStyle}"
		</#if><#t/>
		 value="${addAllToLeftLabel}"<@transferButton action="addAllToLeft" onclick=attributes.addAllToLeftOnclick!""/> /><br /><br />
	</#if><#t/>
	<#if attributes.allowAddAllToRight!true><#t/>
		<#assign addAllToRightLabel=attributes.addAllToRightLabel!"-->>" /><#t/>
		<input type="button"
		<#if attributes.buttonCssClass??><#t/>
		 class="${attributes.buttonCssClass}"
		</#if><#t/>
		<#if attributes.buttonCssStyle??><#t/>
		 style="${attributes.buttonCssStyle}"
		</#if><#t/>
		 value="${addAllToRightLabel}"<@transferButton action="addAllToRight" onclick=attributes.addAllToRightOnclick!""/> /><br /><br />
	</#if><#t/>
	<#if attributes.allowSelectAll!true><#t/>
		<#assign selectAllLabel=attributes.selectAllLabel!"<*>" /><#t/>
		<input type="button"
		<#if attributes.buttonCssClass??><#t/>
		 class="${attributes.buttonCssClass}"
		</#if><#t/>
		<#if attributes.buttonCssStyle??><#t/>
		 style="${attributes.buttonCssStyle}"
		</#if><#t/>
		 value="${selectAllLabel}"<@transferButton action="selectAll" onclick=attributes.selectAllOnclick!""/> /><br /><br />
	</#if><#t/>
</td>
<td>
<#if attributes.rightTitle??><#t/>
	<label for="rightTitle">${attributes.rightTitle}</label><br />
</#if><#t/>
<select
	name="${attributes.doubleName!""}"
	<#if attributes.get("doubleSize")??><#t/>
	size="${attributes.get("doubleSize")}"
	</#if><#t/>
	<#if attributes.doubleDisabled!false><#t/>
	disabled="disabled"
	</#if><#t/>
	<#if attributes.doubleMultiple!false><#t/>
	multiple="multiple"
	</#if><#t/>
	<#if attributes.doubleTabindex??><#t/>
	tabindex="${attributes.tabindex}"
	</#if><#t/>
	<#if attributes.doubleId??><#t/>
	id="${attributes.doubleId}"
	</#if><#t/>
	<#if attributes.doubleCss??><#t/>
	class="${attributes.doubleCss}"
	</#if><#t/>
	<#if attributes.doubleStyle??><#t/>
	style="${attributes.doubleStyle}"
	</#if><#t/>
    <#if attributes.doubleOnclick??><#t/>
    onclick="<#outputformat 'JavaScript'>${attributes.doubleOnclick}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOndblclick??><#t/>
    ondblclick="<#outputformat 'JavaScript'>${attributes.doubleOndblclick}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnmousedown??><#t/>
    onmousedown="<#outputformat 'JavaScript'>${attributes.doubleOnmousedown}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnmouseup??><#t/>
    onmouseup="<#outputformat 'JavaScript'>${attributes.doubleOnmouseup}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnmousemove??><#t/>
    onmousemove="<#outputformat 'JavaScript'>${attributes.doubleOnmousemove}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnmouseover??><#t/>
    onmouseover="<#outputformat 'JavaScript'>${attributes.doubleOnmouseover}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnmouseout??><#t/>
    onmouseout="<#outputformat 'JavaScript'>${attributes.doubleOnmouseout}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnfocus??><#t/>
    onfocus="<#outputformat 'JavaScript'>${attributes.doubleOnfocus}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnblur??><#t/>
    onblur="<#outputformat 'JavaScript'>${attributes.doubleOnblur}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnkeypress??><#t/>
    onkeypress="<#outputformat 'JavaScript'>${attributes.doubleOnkeypress}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnKeydown??><#t/>
    onkeydown="<#outputformat 'JavaScript'>${attributes.doubleOnkeydown}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnkeyup??><#t/>
    onkeyup="<#outputformat 'JavaScript'>${attributes.doubleOnkeyup}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnselect??><#t/>
    onselect="<#outputformat 'JavaScript'>${attributes.doubleOnselect}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleOnchange??><#t/>
    onchange="<#outputformat 'JavaScript'>${attributes.doubleOnchange}</#outputformat>"
    </#if><#t/>
    <#if attributes.doubleAccesskey??><#t/>
    accesskey="<#outputformat 'JavaScript'>${attributes.doubleAccesskey}</#outputformat>"
    </#if>
	<#include "/${attributes.templateDir}/${attributes.expandTheme}/prefixed-dynamic-attributes.ftl" />
	<@prefixedDynamicAttributes prefix="right-"/>
>
	<#if attributes.doubleHeaderKey?? && attributes.doubleHeaderValue??><#t/>
    <option value="${attributes.doubleHeaderKey}">${attributes.doubleHeaderValue}</option>
	</#if><#t/>
	<#if attributes.doubleEmptyOption!false><#t/>
    <option value=""></option>
	</#if><#t/>
	<@s.iterator value="attributes.doubleList"><#t/>
        <#if attributes.doubleListKey??><#t/>
            <#assign doubleItemKey = stack.findValue(attributes.doubleListKey) /><#t/>
        <#else><#t/>
            <#assign doubleItemKey = stack.findValue('top') /><#t/>
        </#if><#t/>
        <#assign doubleItemKeyStr = doubleItemKey.toString() /><#t/>
        <#if attributes.doubleListValue??><#t/>
            <#assign doubleItemValue = stack.findString(attributes.doubleListValue)!"" /><#t/>
        <#else><#t/>
            <#assign doubleItemValue = stack.findString('top') /><#t/>
        </#if><#t/>
    	<option value="${doubleItemKeyStr}"<#rt/>
        <#if tag.contains(attributes.doubleNameValue, doubleItemKey)><#t/>
 		selected="selected"<#rt/>
        </#if><#t/>
    	>${doubleItemValue}</option><#lt/>
	</@s.iterator><#t/>
</select>
<#if attributes.doubleMultiple!false>
<input type="hidden" id="__multiselect_${attributes.doubleId}" name="__multiselect_${attributes.doubleName!""}" value=""<#rt/>
<#if attributes.doubleDisabled!false>
 disabled="disabled"<#rt/>
</#if>
 />
</#if>
<#if attributes.allowUpDownOnRight!true>
<input type="button"
	<@transferButton action="rightDown" onclick=attributes.upDownOnRightOnclick!""/>
<#if attributes.rightDownLabel??>
	value="${attributes.rightDownLabel}"
</#if>
/>
<input type="button"
	<@transferButton action="rightUp" onclick=attributes.upDownOnRightOnclick!""/>
<#if attributes.rightUpLabel??>
	value="${attributes.rightUpLabel}"
</#if>
/>
</#if>
</td>
</tr>
</table>
