<#--
/*
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
        <title>OSGi Console</title>

        <@s.link rel="stylesheet" type="text/css" href="${base}${parameters.staticContentPath}/css/shell.css" />
        <@s.link rel="stylesheet" type="text/css" href="${base}${parameters.staticContentPath}/css/main.css" />
        <@s.link rel="stylesheet" type="text/css" href="${base}${parameters.staticContentPath}/css/redmond/jquery-ui-1.12.1.redmond.css" />

        <@s.script src="${base}${parameters.staticContentPath}/js/shell.js" />
        <@s.script src="${base}${parameters.staticContentPath}/js/jquery-1.12.4.min.js" />
        <@s.script src="${base}${parameters.staticContentPath}/js/jquery-ui-1.12.1.min.js" />
    </head>
<body>
<div class="menu">
    <div  style="float:right;">
        <@s.url var="bundlesUrl" namespace="/osgi/admin" action="bundles" includeParams="none" />
        <@s.url var="osgiShellUrl" namespace="/osgi/admin" action="shell" includeParams="none" />
        <a href="${bundlesUrl}" class="ui-state-default ui-corner-all fg-button fg-button-icon-left">
            <span class="ui-icon ui-icon-bullet"></span>
            Bundles
        </a>
        <a href="${osgiShellUrl}" class="ui-state-default ui-corner-all fg-button fg-button-icon-left">
            <span class="ui-icon ui-icon-transferthick-e-w"></span>
            OSGi Shell
        </a>
    </div>
</div>
<div id="shell" >
   <form onsubmit="return false" id="wc-form">
        <div class="wc-results" id="wc-result">
             Welcome to the OSGi console! Type 'help' to see the list of available commands.
             <br />
        </div>
        <@s.url var="execUrl" namespace="/osgi/admin" action="execCommand" />
        <label for"command">Command:</label>
        <input name="command" onkeyup="keyEvent(event, '${execUrl}')" class="wc-command" id="wc-command" type="text" />
    </form>
</div>
</body>
</html>
