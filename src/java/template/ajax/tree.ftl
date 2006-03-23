 <script language="JavaScript" type="text/javascript">
        <!--
        dojo.require("dojo.lang.*");
        dojo.require("dojo.widget.*");
        dojo.require("dojo.widget.Tree");
        // dojo.hostenv.writeIncludes();
        -->
 </script>
<div dojoType="Tree" showRootGrid="false" templateCssPath="${base}/css/Tree.css"
    <#if parameters.id?exists>id="${parameters.id?html}"</#if>
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
    toggle="${parameters.toggle}"
    </#if>
    <#if parameters.openAll?exists>
    openAll="${parameters.openAll?string}"
    </#if>
    >
    <#if parameters.label?exists>
    <div dojoType="TreeNode" title="${parameters.label?html}"
    <#if parameters.nodeIdProperty?exists>
    id="${stack.findValue(parameters.nodeIdProperty)}"
    </#if>
    >
    <#elseif parameters.rootNode?exists>
    ${stack.push(parameters.rootNode)}
    <#include "/${parameters.templateDir}/ajax/treenode-include.ftl" />
    <#assign oldNode = stack.pop()/> <#-- pop the node off of the stack, but don't show it -->
    </#if>
