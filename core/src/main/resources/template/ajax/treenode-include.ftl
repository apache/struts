<div dojoType="TreeNode" title="${stack.findValue(parameters.nodeTitleProperty)}" id="${stack.findValue(parameters.nodeIdProperty)}">
<#list stack.findValue(parameters.childCollectionProperty.toString()) as child>
    ${stack.push(child)}
    <#include "/${parameters.templateDir}/ajax/treenode-include.ftl" />
    <#assign oldNode = stack.pop() /> <#-- pop the node off of the stack, but don't show it -->
</#list>
</div>
