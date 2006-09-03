<div dojoType="TreeNode" 
	<#if parameters.childIconSrc?exists>
	childIconSrc="<@s.url value='${parameters.childIconSrc}' encode="false" />"
	</#if>
    <#if parameters.id?exists>id="${parameters.id?html}"</#if>
    title="${parameters.label}">
