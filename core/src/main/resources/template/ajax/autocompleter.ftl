<input dojoType="struts:ComboBox"<#rt/>
<#if parameters.id?if_exists != "">
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?if_exists != "">
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?if_exists != "">
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.href?if_exists != "">
 dataUrl="${parameters.href}"<#rt/>
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
<#if parameters.formId?if_exists != "">
 formId="${parameters.formId?html}"<#rt/>
</#if>
<#if parameters.formFilter?if_exists != "">
 formFilter="${parameters.formFilter?html}"<#rt/>
</#if>
<#if parameters.refreshListenTopic?if_exists != "">
 refreshListenTopic="${parameters.refreshListenTopic?html}"<#rt/>
</#if>
<#if parameters.onValueChangedPublishTopic?if_exists != "">
 onValueChangedPublishTopic="${parameters.onValueChangedPublishTopic?html}"<#rt/>
</#if>
<#if parameters.beforeLoading?if_exists != "">
 beforeLoading="${parameters.beforeLoading?html}"<#rt/>
</#if>
<#if parameters.afterLoading?if_exists != "">
 afterLoading="${parameters.afterLoading?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
>


