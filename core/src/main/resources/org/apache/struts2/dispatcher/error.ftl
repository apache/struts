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
<html>
<head>
    <title>Struts Problem Report</title>
    <style>
    	pre {
	    	margin: 0;
	        padding: 0;
	    }    
    </style>
</head>
<body>
    <h2>Struts Problem Report</h2>
    <p>
    Struts has detected an unhandled exception:
    </p>

<#assign msgs = [] />
<#list chain as ex>
    <#if ex.message??>
        <#assign msgs = [ex.message] + msgs/>
    </#if>    
</#list>
<#assign rootex = exception/>
<#list chain as ex>
    <#if (ex.location?? && (ex.location != unknown))>
        <#assign rootloc = ex.location/>
        <#assign rootex = ex/>
    <#else>
            <#assign tmploc = locator.getLocation(ex) />
            <#if (tmploc != unknown)>
            <#assign rootloc = tmploc/>
                <#assign rootex = ex/>
            </#if>  
    </#if>    
</#list>

<div id="exception-info">
<table>
    <tr>
        <td><strong>Messages</strong>:</td>
        <td>
            <#if (msgs?size > 1)>
            <ol>
                <#list msgs as msg>
                    <#if (msg?is_method)>
                        <li>${msg[0]?html}</li>
                    <#else>
                        <li>${msg?html}</li>
                    </#if>
                </#list>
            </ol>
            <#elseif (msgs?size == 1)>
                <#if (msgs[0]?is_method)>
                    <li>${msgs[0][0]?html}</li>
                <#else>
                    <li>${msgs[0]?html}</li>
                </#if>
            </#if>
        </td>
    </tr>
    <#if rootloc??>
    <tr>
        <td><strong>File</strong>:</td>
        <td>${rootloc.URI}</td>
    </tr>
    <tr>
        <td><strong>Line number</strong>:</td>
        <td>${rootloc.lineNumber}</td>
    </tr>
    <#if (rootloc.columnNumber >= 0)>
    <tr>
        <td><strong>Column number</strong>:</td>
        <td>${rootloc.columnNumber}</td>
    </tr>
    </#if>
    </#if>
    
</table>
</div>

<#if rootloc??>
    <#assign snippet = rootloc.getSnippet(2) />
    <#if (snippet?size > 0)>
        <div id="snippet">
        <hr />
            
            <#list snippet as line>
                <#if (line_index == 2)>
                	<#if (rootloc.columnNumber >= 3)>
                        <pre style="background:yellow">${(line[0..(rootloc.columnNumber-3)]?html)}<span style="background:red">${(line[(rootloc.columnNumber-2)]?html)}</span><#if ((rootloc.columnNumber)<line.length())>${(line[(rootloc.columnNumber-1)..]?html)}</#if></pre>
                    <#else>
                       	<pre style="background:yellow">${line?html}</pre>
                    </#if>    
                <#else>
                    <pre>${line?html}</pre>
                </#if>    
            </#list>
        </div>
    </#if>    
</#if>

<div id="stacktraces">
<hr />
<h3>Stacktraces</h3>
<#list chain as ex>
<div class="stacktrace" style="padding-left: ${ex_index * 2}em">
    <strong>${ex?html}</strong>
    <div>
    <pre>
    <#list ex.stackTrace as frame>
    ${frame?html}
    </#list>
    </pre>
    </div>
</div>
</#list>
</div>

<div class="footer">
<hr />
<p>
You are seeing this page because development mode is enabled.  Development mode, or devMode, enables extra
debugging behaviors and reports to assist developers.  To disable this mode, set:
<pre>
  struts.devMode=false
</pre>
in your <code>WEB-INF/classes/struts.properties</code> file.
</p>
</div>
</body>
</html>
