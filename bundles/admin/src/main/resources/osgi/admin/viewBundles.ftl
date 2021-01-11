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
        <title>OSGi Bundles</title>

        <link rel="stylesheet" type="text/css" href="<@s.url value="${parameters.staticContentPath}/css/main.css" />" />
        <link rel="stylesheet" type="text/css" href="<@s.url value="${parameters.staticContentPath}/css/redmond/jquery-ui-1.12.1.redmond.css" />" />

        <script src="<@s.url value="${parameters.staticContentPath}/js/jquery-1.12.4.min.js" />"></script>
        <script src="<@s.url value="${parameters.staticContentPath}/js/jquery-ui-1.12.1.min.js" />"></script>
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

<@s.actionerror />

<table class="properties" style="clear:both; width:700px">
    <thead>
        <tr>
            <th>Name</th>
            <th>Status</th>
            <th>Struts Bundle</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <#list bundles as bundle>
        <tr>
            <td>
                <a href="bundle_${bundle.symbolicName}!view.action">${bundle.symbolicName}</a>
            </td>
            <td>${action.getBundleState(bundle)}</td>
            <td>${action.isStrutsEnabled(bundle)?string("yes", "no")}</td>
            <td style="width:200px">
                <#if action.isAllowedAction(bundle, "start")>
                <a href="bundle_${bundle.symbolicName}!start.action" class="ui-state-default ui-corner-all fg-button-small fg-button-icon-left">
                    <span class="ui-icon ui-icon-play"></span>
                    Start
                </a>
                </#if>

                <#if action.isAllowedAction(bundle, "stop")>
                <a href="bundle_${bundle.symbolicName}!stop.action" class="ui-state-default ui-corner-all fg-button-small fg-button-icon-left">
                    <span class="ui-icon ui-icon-stop"></span>
                    Stop
                </a>
                </#if>

                <#if action.isAllowedAction(bundle, "update")>
                <a href="bundle_${bundle.symbolicName}!update.action" class="ui-state-default ui-corner-all fg-button-small fg-button-icon-left">
                    <span class="ui-icon ui-icon-refresh"></span>
                    Update
                </a>
                </#if>
            </td>
        </tr>
        </#list>
    </tbody>
</table>

<@s.actionmessage />

</body>
</html>
