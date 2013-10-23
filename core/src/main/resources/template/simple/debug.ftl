<#--
/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<script type="text/javascript">
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
<p />

<a href="#" onclick="toggleDebug('<#if parameters.id?if_exists != "">${parameters.id?html}<#else>debug</#if>');return false;">[Debug]</a>
<div style="display:none" id="<#if parameters.id?if_exists != "">${parameters.id?html}<#else>debug</#if>">
<h2>Struts ValueStack Debug</h2>
<p />

<h3>Value Stack Contents</h3>
<table border="0" cellpadding="2" cellspacing="0" bgcolor="#DDDDDD">
    <tr><th>Object</th><th>Property Name</th><th>Property Value</th></tr>

    <#assign index=1>
    <#list parameters.stackValues as stackObject>
    <tr>
        <td rowspan="${stackObject.value.size()}">${stackObject.key}</td>

        <#assign renderRow=false>
        <#list stackObject.value.keySet() as propertyName>
            <#if renderRow==true></tr><tr><#else> <#assign renderRow=false> </#if>
            <td bgcolor="<#if (index % 2) == 0>#BBBBBB<#else>#CCCCCC</#if>">${propertyName}</td>
            <td bgcolor="<#if (index % 2) == 0>#BBBBBB<#else>#CCCCCC</#if>"><#if stackObject.value.get(propertyName)??>${stackObject.value.get(propertyName).toString()?html}<#else>null</#if></td>
    </tr>
            <#assign index= index + 1>
        </#list>
    </#list>
</table>
<p />

<h3>Stack Context</h3>
<i>These items are available using the #key notation</i>
<table border="0" cellpadding="2" cellspacing="0" bgcolor="#DDDDDD">
    <tr>
        <th>Key</th><th>Value</th>
    </tr>

    <#assign index=1>
    <#list stack.context.keySet() as contextKey>
    <tr bgcolor="<#if (index % 2) == 0>#BBBBBB<#else>#CCCCCC</#if>">
        <td>${contextKey}</td>
        <td><#if stack.context.get(contextKey)??>${struts.toStringSafe(stack.context.get(contextKey))?html}<#else>null</#if></td>
    </tr>
        <#assign index= index + 1>
    </#list>
</table>
</div>
