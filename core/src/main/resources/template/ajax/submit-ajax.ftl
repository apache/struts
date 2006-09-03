<#--
<tr>
    <td colspan="2"><div <#rt/>
-->
<#include "/${parameters.templateDir}/${themeProperties.parent}/controlheader.ftl" />
<button type="submit" dojoType="BindButton"<#rt/>
<#if parameters.form?exists && parameters.form.id?exists>
 formId="${parameters.form.id}"<#rt/>
</#if>
<#if parameters.name?exists>
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="<@saf.property value="parameters.nameValue"/>"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.resultDivId?exists>
 targetDiv="${parameters.resultDivId}"<#rt/>
</#if>
<#if parameters.onLoadJS?exists>
 onLoad="${parameters.onLoadJS}"<#rt/>
</#if>
<#if parameters.preInvokeJS?exists>
 preInvokeJS="${parameters.preInvokeJS}"<#rt/>
</#if>
<#if parameters.notifyTopics?exists>
 notifyTopics="${parameters.notifyTopics}"<#rt/>
</#if>
<#if parameters.listenTopics?exists>
 listenTopics="${parameters.listenTopics}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl"/>
><#if parameters.nameValue?exists><@saf.property value="parameters.nameValue"/><#rt/></#if></button>
<#--include "/${parameters.templateDir}/xhtml/controlfooter.ftl" /-->
<#include "/${parameters.templateDir}/${themeProperties.parent}/controlfooter.ftl" />

