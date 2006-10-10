<a dojoType="BindAnchor" evalResult="true"<#rt/>
<#if parameters.id?if_exists != "">
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.href?if_exists != "">
 href="${parameters.href}"<#rt/>
</#if>
<#if parameters.notifyTopics?exists>
 notifyTopics="${parameters.notifyTopics}"<#rt/>
</#if>
<#if parameters.errorText?if_exists != "">
 errorHtml="${parameters.errorText?html}"<#rt/>
</#if>
<#if parameters.showErrorTransportText?exists>
 showTransportError="true"<#rt/>
</#if>
<#if parameters.afterLoading?exists>
 onLoad="${parameters.afterLoading}"<#rt/>
</#if>
<#if parameters.preInvokeJS?exists>
 preInvokeJS="${parameters.preInvokeJS}"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl"/>
>
