<select dojoType="struts:ComboBox" mode="local"<#rt/>
<#if parameters.id?if_exists != "">
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?if_exists != "">
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?if_exists != "">
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.forceValidOption?exists>
 forceValidOption="${parameters.forceValidOption?string?html}"<#rt/>
</#if>
<#if parameters.searchType?if_exists != "">
 searchType="${parameters.searchType}"<#rt/>
</#if>
<#if parameters.autoComplete?exists>
 autoComplete="${parameters.autoComplete?string?html}"<#rt/>
</#if>
<#if parameters.searchDelay?exists>
 searchDelay="${parameters.searchDelay?c}"<#rt/>
</#if>
<#if parameters.disabled?exists>
 disabled="${parameters.disabled?string?html}"<#rt/>
</#if>
<#if parameters.dropdownWidth?exists>
 dropdownWidth="${parameters.dropdownWidth?c}"<#rt/>
</#if>
<#if parameters.dropdownHeight?exists>
 dropdownHeight="${parameters.dropdownHeight?c}"<#rt/>
</#if>
<#if parameters.name?if_exists != "">
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.get("size")?exists>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.maxlength?exists>
 maxlength="${parameters.maxlength?string?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="<@s.property value="parameters.nameValue"/>"<#rt/>
</#if>
<#if parameters.readonly?default(false)>
 readonly="readonly"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
>
<#if parameters.list?exists>
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
</#if>
</select>

