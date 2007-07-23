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
 searchType="${parameters.searchType?html}"<#rt/>
</#if>
<#if parameters.autoComplete?exists>
 autoComplete="${parameters.autoComplete?string?html}"<#rt/>
</#if>
<#if parameters.delay?exists>
 searchDelay="${parameters.delay?c}"<#rt/>
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
<#if parameters.keyName?if_exists != "">
 keyName="${parameters.keyName?html}"<#rt/>
</#if>
<#if parameters.maxlength?exists>
 maxlength="${parameters.maxlength?string?html}"<#rt/>
</#if>
<#if parameters.nameValue?if_exists != "">
 initialValue="${parameters.nameValue}"<#rt/>
</#if>
<#if parameters.key?if_exists != "">
 initialKey="${parameters.key}"<#rt/>
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
<#if parameters.listenTopics?if_exists != "">
 listenTopics="${parameters.listenTopics?html}"<#rt/>
</#if>
<#if parameters.notifyTopics?if_exists != "">
 notifyTopics="${parameters.notifyTopics?html}"<#rt/>
</#if>
<#if parameters.indicator?if_exists != "">
 indicator="${parameters.indicator?html}"<#rt/>
</#if>
<#if parameters.loadOnTextChange?default(false)>
 loadOnType="${parameters.loadOnTextChange?string?html}"<#rt/>
</#if>
<#if parameters.loadMinimumCount?exists>
 loadMinimum="${parameters.loadMinimumCount?c}"<#rt/>
</#if>
<#if parameters.showDownArrow?exists>
 visibleDownArrow="${parameters.showDownArrow?string?html}"<#rt/>
</#if>
<#if parameters.iconPath?if_exists != "">
 buttonSrc="<@s.url value='${parameters.iconPath}' encode="false" includeParams='none'/>"<#rt/>
</#if>
<#if parameters.templateCssPath?if_exists != "">
 templateCssPath="<@s.url value='${parameters.templateCssPath}' encode="false" includeParams='none'/>"
</#if>
<#if parameters.dataFieldName?if_exists != "">
 dataFieldName="${parameters.dataFieldName?html}"
</#if>
<#if parameters.searchLimit?if_exists != "">
 searchLimit="${parameters.searchLimit?html}"
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
>



