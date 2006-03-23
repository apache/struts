<!DOCTYPE html PUBLIC "-//Tigris//DTD XHTML 1.0 Transitional//EN"
    "${req.contextPath}/template-tigris/css/tigris_transitional.dtd">
<html>
<head>
<title>${pageTitle}</title>
<meta http-equiv="Content-type" content="text/html; charset=ISO-8859-1" />
<#include "/config-browser/config-styles.css" parse="n"></head>
<body class="composite" marginwidth="0" marginheight="0" leftmargin="0" topmargin="0">

<div id="breadcrumbs">
	<table border="0" cellspacing="0" cellpadding="4" width="100%">
		<tr>
			<td> 
				WebWork Configuration Browser > ${pageTitle}
			</td>
		</tr>
	</table>
</div>  
  
<table border="0" cellspacing="0" cellpadding="4" width="100%" id="main">
	<tr valign="top">
	    <#if !hideNav?exists>		<td id="leftcol" width="20%">
			<div id="navcolumn">
				<#-- Quick hack to show menu features :)
-->				<#-- This should be done via contribution from the actions
-->				<#-- themselves. E.g via a collection of MenuItems with url and name
-->				<div id="projecttools" class="toolgroup">
					<#if namespaces?exists>					<div class="label"><strong>Namespaces</strong></div>
					<div class="body">
						<#foreach namespace in namespaces>						<div><@ww.url id="namespaceLink" action="actionNames" includeParams="none"><@ww.param name="namespace">${namespace}</@ww.param></@ww.url><a href="${namespaceLink}"><#if namespace == ""> default <#else> ${namespace} </#if></a></div>
						</#foreach>					</div>
					</#if>				</div>				
				<div class="toolgroup">
					<#if actionNames?exists>					<div class="label"><strong>Actions in <#if namespace == ""> default <#else> ${namespace} </#if></strong></div>
                        <#foreach name in actionNames>                        <div><@ww.url id="actionLink" action="showConfig" includeParams="none"><@ww.param name="namespace">${namespace}</@ww.param><@ww.param name="actionName">${name}</@ww.param></@ww.url><a href="${actionLink}">${name}</a></div>
						</#foreach>					</#if>				</div>
			</div>
		</td>
		</#if>		<td>
			<div id="bodycol">
				<div id="apphead">
					<h2>${pageTitle}</h2>
				</div>
				<div id="content" class="app">				
