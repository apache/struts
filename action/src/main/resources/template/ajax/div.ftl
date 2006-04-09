<div dojoType='BindDiv'
	<#if parameters.id?if_exists != "">id="${parameters.id?html}"</#if>
    <#if parameters.title?exists>title="${parameters.title?html}"</#if>
    <#if parameters.name?exists>name="${parameters.name?html}"</#if>
	<#if parameters.href?if_exists != "">href="${parameters.href}"</#if>
	<#if parameters.loadingText?if_exists != "">loadingHtml="${parameters.loadingText?html}"</#if>
	<#if parameters.errorText?if_exists != "">errorHtml="${parameters.errorText?html}"</#if>
	<#if parameters.showErrorTransportText?exists>showTransportError='true'</#if>
	<#if parameters.delay?exists>delay='${parameters.delay}'</#if>
	<#if parameters.updateFreq?exists>refresh='${parameters.updateFreq}'</#if>
	<#if parameters.listenTopics?exists>listenTopics='${parameters.listenTopics}'</#if>
	<#if parameters.afterLoading?exists>onLoad='${parameters.afterLoading}'</#if>

<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
>
