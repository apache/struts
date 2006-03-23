<script language="JavaScript" type="text/javascript">
<!--
    function toggleDebug(debugId) {
        var debugDiv = document.getElementById(debugId);
        if (debugDiv) {
            var display = debugDiv.style.display;
            if (display == 'none') {
                debugDiv.style.display = 'block';
            } else if (display == 'block') {
                debugDiv.style.display = 'none';
            }
        }
    }
-->
</script>
<p/>

<a href="#" onclick="toggleDebug('${parameters.id?default("debug")}');return false;">[Debug]</a>
<div style="display:none" id="${parameters.id?default("debug")}">
<h2>WebWork ValueStack Debug</h2>
<p/>

<h3>Value Stack Contents</h3>
<table border="0" cellpadding="2" cellspacing="0" width="400" bgcolor="#DDDDDD">
    <tr><th>Object</th><th>Property Name</th><th>Property Value</th></tr>

    <#assign index=1>
    <#list parameters.stackValues as stackObject>
    <tr>
        <td rowspan="${stackObject.value.size()}">${stackObject.key}</td>

        <#assign renderRow=false>
        <#list stackObject.value.keySet() as propertyName>
            <#if renderRow==true><tr><#else> <#assign renderRow=false> </#if>
            <td bgcolor="<#if (index % 2) == 0>#BBBBBB<#else>#CCCCCC</#if>">${propertyName}</td>
            <td bgcolor="<#if (index % 2) == 0>#BBBBBB<#else>#CCCCCC</#if>"><#if stackObject.value.get(propertyName)?exists>${stackObject.value.get(propertyName).toString()}<#else>null</#if></td>
    </tr>
            <#assign index= index + 1>
        </#list>
    </#list>
</table>
<p/>

<h3>Stack Context</h3>
<i>These items are available using the #key notation</i>
<table border="0" cellpadding="2" cellspacing="0" width="400" bgcolor="#DDDDDD">
    <tr>
        <th>Key</th><th>Value</th>
    </tr>

    <#assign index=1>
    <#list stack.context.keySet() as contextKey>
    <tr bgcolor="<#if (index % 2) == 0>#BBBBBB<#else>#CCCCCC</#if>">
        <td>${contextKey}</td><td><#if stack.context.get(contextKey)?exists>${stack.context.get(contextKey).toString()}<#else>null</#if></td>
    </tr>
        <#assign index= index + 1>
    </#list>
</table>
</div>
