<select<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.get("size")?exists>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
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
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
>
<#if parameters.headerKey?exists && parameters.headerValue?exists>
    <option value="${parameters.headerKey?html}">${parameters.headerValue?html}</option>
</#if>
<#if parameters.emptyOption?default(false)>
    <option value=""></option>
</#if>
<@ww.iterator value="parameters.list">
        <#if parameters.listKey?exists>
            <#assign itemKey = stack.findValue(parameters.listKey)/>
        <#else>
            <#assign itemKey = stack.findValue('top')/>
        </#if>
        <#assign itemKeyStr = itemKey.toString() />
        <#if parameters.listValue?exists>
            <#assign itemValue = stack.findString(parameters.listValue)/>
        <#else>
            <#assign itemValue = stack.findString('top')/>
        </#if>
    <option value="${itemKeyStr?html}"<#rt/>
        <#if tag.contains(parameters.nameValue, itemKey) == true>
 selected="selected"<#rt/>
        </#if>
    >${itemValue?html}</option><#lt/>
</@ww.iterator>
</select>
