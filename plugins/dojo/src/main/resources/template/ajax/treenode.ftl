<div dojoType="struts:StrutsTreeNode" 
	<#if parameters.childIconSrc?exists>
	childIconSrc="<@s.url value='${parameters.childIconSrc}' includeParams='none' encode='false' />"
	</#if>
    <#if parameters.id?exists>id="${parameters.id?html}"</#if>
    title="${parameters.label}">
