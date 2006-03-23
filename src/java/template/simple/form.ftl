<#if (parameters.validate?default(false) == false)><#rt/>
	<#if parameters.onsubmit?exists><#rt/>
		${tag.addParameter('onsubmit', "${parameters.onsubmit}; customOnsubmit(); return true;") }
	<#else>
		${tag.addParameter('onsubmit', "customOnsubmit(); return true;") }	
	</#if>
</#if>
<form<#rt/>
<#if parameters.namespace?exists && parameters.validate?exists>
 namespace="${parameters.namespace?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.name?exists>
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.onsubmit?exists>
 onsubmit="${parameters.onsubmit?html}"<#rt/>
</#if>
<#if parameters.action?exists>
 action="${parameters.action?html}"<#rt/>
</#if>
<#if parameters.target?exists>
 target="${parameters.target?html}"<#rt/>
</#if>
<#if parameters.method?exists>
 method="${parameters.method?html}"<#rt/>
</#if>
<#if parameters.enctype?exists>
 enctype="${parameters.enctype?html}"<#rt/>
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
>
