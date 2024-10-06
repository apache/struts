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
<#global dynamic_attributes_ignore = "second-"/>
<#include "/${attributes.templateDir}/simple/select.ftl" />
<#assign startCount = 0/><#rt/>
<#if attributes.headerKey?? && attributes.headerValue??>
    <#assign startCount = startCount + 1/><#rt/>
</#if>
<#if attributes.emptyOption??>
    <#assign startCount = startCount + 1/><#rt/>
</#if>
<br/>
<select<#rt/>
 name="${(attributes.doubleName!"")}"<#rt/>
<#if attributes.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if attributes.doubleTabindex?has_content>
 tabindex="${attributes.doubleTabindex}"<#rt/>
</#if>
<#if attributes.doubleId?has_content>
 id="${attributes.doubleId}"<#rt/>
</#if>
<#if attributes.doubleCss?has_content>
 class="${attributes.doubleCss}"<#rt/>
</#if>
<#if attributes.doubleStyle?has_content>
 style="${attributes.doubleStyle}"<#rt/>
</#if>
<#if attributes.title?has_content>
 title="${attributes.title}"<#rt/>
</#if>
<#if attributes.multiple!false>
 multiple="multiple"<#rt/>
</#if>
<#if attributes.get("doubleSize")?has_content>
 size="${attributes.get("doubleSize")}"<#rt/>
</#if>
<#if attributes.doubleMultiple!false>
 multiple="multiple"<#rt/>
</#if>
<#if attributes.doubleDisabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if attributes.doubleOnclick??>
 onclick="<#outputformat 'JavaScript'>${attributes.doubleOnclick}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOndblclick??>
 ondblclick="<#outputformat 'JavaScript'>${attributes.doubleOndblclick}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnmousedown??>
 onmousedown="<#outputformat 'JavaScript'>${attributes.doubleOnmousedown}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnmouseup??>
 onmouseup="<#outputformat 'JavaScript'>${attributes.doubleOnmouseup}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnmouseover??>
 onmouseover="<#outputformat 'JavaScript'>${attributes.doubleOnmouseover}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnmousemove??>
 onmousemove="<#outputformat 'JavaScript'>${attributes.doubleOnmousemove}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnmouseout??>
 onmouseout="<#outputformat 'JavaScript'>${attributes.doubleOnmouseout}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnfocus??>
 onfocus="<#outputformat 'JavaScript'>${attributes.doubleOnfocus}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnblur??>
 onblur="<#outputformat 'JavaScript'>${attributes.doubleOnblur}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnkeypress??>
 onkeypress="<#outputformat 'JavaScript'>${attributes.doubleOnkeypress}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnkeydown??>
 onkeydown="<#outputformat 'JavaScript'>${attributes.doubleOnkeydown}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnkeyup??>
 onkeyup="<#outputformat 'JavaScript'>${attributes.doubleOnkeyup}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnselect??>
 onselect="<#outputformat 'JavaScript'>${attributes.doubleOnselect}</#outputformat>"<#rt/>
</#if>
<#if attributes.doubleOnchange??>
 onchange="<#outputformat 'JavaScript'>${attributes.doubleOnchange}</#outputformat>"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/prefixed-dynamic-attributes.ftl" />
<@prefixedDynamicAttributes prefix="second-"/>
>
</select>
<#if attributes.doubleMultiple!false>
<input type="hidden" id="__multiselect_${attributes.doubleId}"<#rt/>
 name="__multiselect_${(attributes.doubleName!"")}" value=""<#rt/>
<#if attributes.doubleDisabled!false>
 disabled="disabled"<#rt/>
</#if>
/><#rt/>
</#if>
<@s.script>
    <#assign itemCount = startCount/>
    var ${attributes.escapedId}Group = new Array(${attributes.listSize?number?c} + ${startCount});
    for (var i = 0; i < (${attributes.listSize?number?c} + ${startCount}); i++) {
        ${attributes.escapedId}Group[i] = [];
    }

    <@s.iterator value="attributes.list">
        <#if attributes.listKey??>
            <#assign itemKey = stack.findValue(attributes.listKey)/>
            <#else>
                <#assign itemKey = stack.findValue('top')/>
        </#if>
        <#if attributes.listValue??>
            <#assign itemValue = stack.findString(attributes.listValue)/>
            <#else>
                <#assign itemValue = stack.findString('top')/>
        </#if>
        <#assign doubleItemCount = 0/>
        <#if attributes.doubleHeaderKey?? && attributes.doubleHeaderValue??>
        ${attributes.escapedId}Group[${itemCount}][${doubleItemCount}] = new Option("${attributes.doubleHeaderValue?js_string}", "${attributes.doubleHeaderKey?js_string}");
            <#assign doubleItemCount = doubleItemCount + 1/>
        </#if>
        <#if attributes.doubleEmptyOption??>
        ${attributes.escapedId}Group[${itemCount}][${doubleItemCount}] = new Option("", "");
            <#assign doubleItemCount = doubleItemCount + 1/>
        </#if>
    <@s.iterator value="${attributes.doubleList}">
        <#if attributes.doubleListKey??>
            <#assign doubleItemKey = stack.findValue(attributes.doubleListKey)/>
            <#else>
                <#assign doubleItemKey = stack.findValue('top')/>
        </#if>
        <#assign doubleItemKeyStr = doubleItemKey.toString() />
        <#if attributes.doubleListValue??>
            <#assign doubleItemValue = stack.findString(attributes.doubleListValue)/>
            <#else>
                <#assign doubleItemValue = stack.findString('top')/>
        </#if>
        <#if attributes.doubleListCssClass??>
            <#if stack.findString(attributes.doubleListCssClass)??>
              <#assign itemDoubleCssClass= stack.findString(attributes.doubleListCssClass)/>
            <#else>
              <#assign itemDoubleCssClass = ''/>
            </#if>
        </#if>
        <#if attributes.doubleListCssStyle??>
            <#if stack.findString(attributes.doubleListCssStyle)??>
              <#assign itemDoubleCssStyle= stack.findString(attributes.doubleListCssStyle)/>
            <#else>
              <#assign itemDoubleCssStyle = ''/>
            </#if>
        </#if>
        <#if attributes.doubleListTitle??>
            <#if stack.findString(attributes.doubleListTitle)??>
              <#assign itemDoubleTitle= stack.findString(attributes.doubleListTitle)/>
            <#else>
              <#assign itemDoubleTitle = ''/>
            </#if>
        </#if>
    ${attributes.escapedId}Group[${itemCount}][${doubleItemCount}] = new Option("${doubleItemValue?js_string}", "${doubleItemKeyStr?js_string}");
        <#if itemDoubleCssClass??>
    ${attributes.escapedId}Group[${itemCount}][${doubleItemCount}].setAttribute("class","${itemDoubleCssClass}");
        </#if>
        <#if itemDoubleCssStyle??>
        ${attributes.escapedId}Group[${itemCount}][${doubleItemCount}].setAttribute("style","${itemDoubleCssStyle}");
        </#if>
        <#if itemDoubleTitle??>
        ${attributes.escapedId}Group[${itemCount}][${doubleItemCount}].setAttribute("title","${itemDoubleTitle}");
        </#if>

        <#assign doubleItemCount = doubleItemCount + 1/>
    </@s.iterator>
        <#assign itemCount = itemCount + 1/>
    </@s.iterator>

    var ${attributes.escapedId}Temp = document.${attributes.formName}.${attributes.doubleId};
    <#assign itemCount = startCount/>
    <#assign redirectTo = 0/>
    <@s.iterator value="attributes.list">
        <#if attributes.listKey??>
            <#assign itemKey = stack.findValue(attributes.listKey)/>
            <#else>
                <#assign itemKey = stack.findValue('top')/>
        </#if>
        <#if tag.contains(attributes.nameValue, itemKey)>
            <#assign redirectTo = itemCount/>
        </#if>
        <#assign itemCount = itemCount + 1/>
    </@s.iterator>
    ${attributes.escapedId}Redirect(${redirectTo});
    function ${attributes.escapedId}Redirect(x) {
        var selected = false;
        for (var m = ${attributes.escapedId}Temp.options.length - 1; m >= 0; m--) {
            ${attributes.escapedId}Temp.remove(m);
        }

        for (var i = 0; i < ${attributes.escapedId}Group[x].length; i++) {
            ${attributes.escapedId}Temp.options[i] = new Option(${attributes.escapedId}Group[x][i].text, ${attributes.escapedId}Group[x][i].value);
        <#if attributes.doubleNameValue??>
            <#if attributes.doubleMultiple??>
                for (var j = 0; j < ${attributes.doubleNameValue}.length; j++) {
                    if (${attributes.escapedId}Temp.options[i].value == ${attributes.doubleNameValue?js_string}[j]) {
                        ${attributes.escapedId}Temp.options[i].selected = true;
                        selected = true;
                    }
                }
                <#else>
                    if (${attributes.escapedId}Temp.options[i].value == '${attributes.doubleNameValue?js_string}') {
                        ${attributes.escapedId}Temp.options[i].selected = true;
                        selected = true;
                    }
            </#if>
        </#if>
        }

        if ((${attributes.escapedId}Temp.options.length > 0) && (! selected)) {
            ${attributes.escapedId}Temp.options[0].selected = true;
        }
    }
</@s.script>
