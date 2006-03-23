<#include "/${parameters.templateDir}/simple/select.ftl" />
<#assign startCount = 0/>
<#if parameters.headerKey?exists && parameters.headerValue?exists>
    <#assign startCount = startCount + 1/>
</#if>
<#if parameters.emptyOption?exists>
    <#assign startCount = startCount + 1/>
</#if>

<br/>
<select<#rt/>
 name="${parameters.doubleName?default("")?html}"<#rt/>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.doubleTabindex?exists>
 tabindex="${parameters.doubleTabindex?html}"<#rt/>
</#if>
<#if parameters.doubleId?exists>
 id="${parameters.doubleId?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
<#if parameters.multiple?default(false)>
 multiple="multiple"<#rt/>
</#if>
>
</select>
<script type="text/javascript">
<#assign itemCount = startCount/>
    var ${parameters.name}Group = new Array(${parameters.listSize} + ${startCount});
    for (i = 0; i < (${parameters.listSize} + ${startCount}); i++)
    ${parameters.name}Group[i] = new Array();

<@ww.iterator value="parameters.list">
    <#if parameters.listKey?exists>
        <#assign itemKey = stack.findValue(parameters.listKey)/>
    <#else>
        <#assign itemKey = stack.findValue('top')/>
    </#if>
    <#if parameters.listValue?exists>
        <#assign itemValue = stack.findString(parameters.listValue)/>
    <#else>
        <#assign itemValue = stack.findString('top')/>
    </#if>
    <#assign doubleItemCount = 0/>
    <@ww.iterator value="${parameters.doubleList}">
        <#if parameters.doubleListKey?exists>
            <#assign doubleItemKey = stack.findValue(parameters.doubleListKey)/>
        <#else>
            <#assign doubleItemKey = stack.findValue('top')/>
        </#if>
        <#if parameters.doubleListValue?exists>
            <#assign doubleItemValue = stack.findString(parameters.doubleListValue)/>
        <#else>
            <#assign doubleItemValue = stack.findString('top')/>
        </#if>
    ${parameters.name}Group[${itemCount}][${doubleItemCount}] = new Option("${doubleItemValue}", "${doubleItemKey}");

        <#assign doubleItemCount = doubleItemCount + 1/>
    </@ww.iterator>
    <#assign itemCount = itemCount + 1/>
</@ww.iterator>

    var ${parameters.name}Temp = document.${parameters.formName}.${parameters.doubleName};
<#assign itemCount = startCount/>
<#assign redirectTo = 0/>
<@ww.iterator value="parameters.list">
    <#if parameters.listKey?exists>
        <#assign itemKey = stack.findValue(parameters.listKey)/>
    <#else>
        <#assign itemKey = stack.findValue('top')/>
    </#if>
    <#if tag.contains(parameters.nameValue, itemKey)>
        <#assign redirectTo = itemCount/>
    </#if>
    <#assign itemCount = itemCount + 1/>
</@ww.iterator>
    ${parameters.name}Redirect(${redirectTo});
    function ${parameters.name}Redirect(x) {
    	var selected = false;
        for (m = ${parameters.name}Temp.options.length - 1; m >= 0; m--) {
            ${parameters.name}Temp.options[m] = null;
        }

        for (i = 0; i < ${parameters.name}Group[x].length; i++) {
            ${parameters.name}Temp.options[i] = new Option(${parameters.name}Group[x][i].text, ${parameters.name}Group[x][i].value);
            <#if parameters.doubleNameValue?exists>
            	if (${parameters.name}Temp.options[i].value == '${parameters.doubleNameValue}') {
            		${parameters.name}Temp.options[i].selected = true;
            		selected = true;
            	}
            </#if>
        }

        if ((${parameters.name}Temp.options.length > 0) && (! selected)) {
           	${parameters.name}Temp.options[0].selected = true;
        }
    }
</script>
