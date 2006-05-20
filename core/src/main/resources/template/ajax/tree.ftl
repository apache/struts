 <script language="JavaScript" type="text/javascript">
        <!--
        dojo.require("dojo.lang.*");
        dojo.require("dojo.widget.*");
        dojo.require("dojo.widget.Tree");
        // dojo.hostenv.writeIncludes();
        -->
 </script>
<div dojoType="Tree"   
	<#if parameters.blankIconSrc?exists>
	gridIconSrcT="<@saf.url value='${parameters.blankIconSrc}' encode="false" />"
	</#if>
	<#if parameters.gridIconSrcL?exists>
	gridIconSrcL="<@saf.url value='${parameters.gridIconSrcL}' encode="false" />"
	</#if>
	<#if parameters.gridIconSrcV?exists>
	gridIconSrcV="<@saf.url value='${parameters.gridIconSrcV}' encode="false" />"
	</#if>
	<#if parameters.gridIconSrcP?exists>
	gridIconSrcP="<@saf.url value='${parameters.gridIconSrcP}' encode="false" />"
	</#if>
	<#if parameters.gridIconSrcC?exists>
	gridIconSrcC="<@saf.url value='${parameters.gridIconSrcC}' encode="false" />"
	</#if>
	<#if parameters.gridIconSrcX?exists>
	gridIconSrcX="<@saf.url value='${parameters.gridIconSrcX}' encode="false" />"
	</#if>
	<#if parameters.gridIconSrcY?exists>
	gridIconSrcY="<@saf.url value='${parameters.gridIconSrcY}' encode="false" />"
	</#if>
	<#if parameters.gridIconSrcZ?exists>
	gridIconSrcZ="<@saf.url value='${parameters.gridIconSrcZ}' encode="false" />"
	</#if>
	<#if parameters.expandIconSrcPlus?exists>
	expandIconSrcPlus="<@saf.url value='${parameters.expandIconSrcPlus}' />"
	</#if>
	<#if parameters.expandIconSrcMinus?exists>
	expandIconSrcMinus="<@saf.url value='${parameters.expandIconSrcMinus?html}' />"
	</#if>
	<#if parameters.iconWidth?exists>
	iconWidth="<@saf.url value='${parameters.iconWidth?html}' encode="false" />"
	</#if>
	<#if parameters.iconHeight?exists>
	iconHeight="<@saf.url value='${parameters.iconHeight?html}' encode="false" />"
	</#if>
	<#if parameters.toggleDuration?exists>
	toggleDuration=${parameters.toggleDuration?c}
	</#if>
	<#if parameters.templateCssPath?exists>
	templateCssPath="<@saf.url value='${parameters.templateCssPath}' encode="false" />"
	</#if>
	<#if parameters.showGrid?exists>
	showGrid="${parameters.showGrid?default(true)?string}"
	</#if>
	<#if parameters.showRootGrid?exists>
	showRootGrid="${parameters.showRootGrid?default(true)?string}"
	</#if>
    <#if parameters.id?exists>
    id="${parameters.id?html}"
    </#if>
    <#if parameters.treeSelectedTopic?exists>
    publishSelectionTopic="${parameters.treeSelectedTopic?html}"
    </#if>
    <#if parameters.treeExpandedTopic?exists>
    publishExpandedTopic="${parameters.treeExpandedTopic?html}"
    </#if>
    <#if parameters.treeCollapsedTopic?exists>
    publishCollapsedTopic="${parameters.treeCollapsedTopic?html}"
    </#if>
    <#if parameters.toggle?exists>
    toggle="${parameters.toggle?html}"
    </#if>
    >
    <#if parameters.label?exists>
    <div dojoType="TreeNode" id="${parameters.id}_root" title="${parameters.label?html}"
    <#if parameters.nodeIdProperty?exists>
    id="${stack.findValue(parameters.nodeIdProperty)}"
    </#if>
    >
    <#elseif parameters.rootNode?exists>
    ${stack.push(parameters.rootNode)}
    <#include "/${parameters.templateDir}/ajax/treenode-include.ftl" />
    <#assign oldNode = stack.pop()/> <#-- pop the node off of the stack, but don't show it -->
    </#if>
