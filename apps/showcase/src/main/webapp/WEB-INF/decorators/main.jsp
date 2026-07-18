<!--
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
<%@ page import="org.apache.struts2.result.StrutsResultSupport" %>
<!DOCTYPE html>
<%@ page
        language="java"
        contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);

    // Calculate the view sources url
    String sourceUrl = request.getContextPath() + "/viewSource.action";
    org.apache.struts2.ActionInvocation inv = org.apache.struts2.ActionContext.getContext().getActionInvocation();
    org.apache.struts2.dispatcher.mapper.ActionMapping mapping = org.apache.struts2.ServletActionContext.getActionMapping();
    if (inv != null) {
        try {
            org.apache.struts2.util.location.Location loc = inv.getProxy().getConfig().getLocation();
            sourceUrl += "?config=" + (loc != null ? loc.getURI() + ":" + loc.getLineNumber() : "");
        } catch (Exception e) {
            sourceUrl += "?config=";
        }
        sourceUrl += "&className=" + inv.getProxy().getConfig().getClassName();

        if (inv.getResult() != null && inv.getResult() instanceof StrutsResultSupport) {
            sourceUrl += "&page=" + mapping.getNamespace() + "/" + ((StrutsResultSupport) inv.getResult()).getLastFinalLocation();
        }
    } else {
        sourceUrl += "?page=" + request.getServletPath();
    }
%>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Struts2 Showcase for Apache Struts Project">
    <meta name="author" content="The Apache Software Foundation">

    <title><sitemesh:write property="title"/></title>

    <link rel="stylesheet" type="text/css" media="all" href="<s:webjar path='bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<s:webjar path='bootstrap-icons/font/bootstrap-icons.min.css'/>"/>
    <s:url var="mainCss" value='/styles/main.css' encode='false' includeParams='none'/>
    <s:link href="%{mainCss}" rel="stylesheet" type="text/css" media="all"/>

    <script src="<s:webjar path='jquery/jquery.min.js'/>"></script>
    <script src="<s:webjar path='bootstrap/js/bootstrap.bundle.min.js'/>"></script>
    <s:script>
        $(function () {
            $('ul.alert').each(function () {
                var wrapper = $('<div class="alert alert-dismissible" />');
                $(this).before(wrapper);
                wrapper.append('<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>').append(this);
            });
        });
    </s:script>

    <!-- Prettify -->
    <s:url var="prettifyCss" value='/styles/prettify.css' encode='false' includeParams='none'/>
    <s:link href="%{prettifyCss}" rel="stylesheet"/>
    <s:url var="prettifyJs" value='/js/prettify.js' encode='false' includeParams='none'/>
    <s:script src="%{prettifyJs}"/>

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <s:script src="https://html5shim.googlecode.com/svn/trunk/html5.js"/>
    <![endif]-->

    <s:script>
        jQuery(document).ready(function() { prettyPrint(); } );
    </s:script>
    <sitemesh:write property="head"/>
</head>

<body id="page-home">

<nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top">
    <div class="container-fluid">

        <div class="navbar-header">
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbar-main" aria-controls="navbar-main" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <s:url var="home" action="showcase" namespace="/" includeContext="false" />
            <s:a value="%{home}" cssClass="navbar-brand">
                Struts2 Showcase
            </s:a>
        </div>

        <div class="collapse navbar-collapse" id="navbar-main">
                <ul class="nav navbar-nav">
                    <li class="nav-item"><s:a value="%{home}" cssClass="nav-link"><i class="bi bi-house"></i> Home</s:a></li>
                    <li class="dropdown">
                        <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                            <i class="bi bi-gear"></i> Configuration</a>
                        <ul class="dropdown-menu" role="menu">
                            <li><s:a action="actionChain1!input" namespace="/actionchaining"
                                     includeParams="none" cssClass="dropdown-item">Action Chaining</s:a></li>
                            <li><s:a action="index" namespace="/config-browser"
                                     includeParams="none" cssClass="dropdown-item">Config Browser</s:a></li>
                            <s:url var="conversion" action="index" namespace="/conversion" includeContext="false" />
                            <li><s:a value="%{conversion}" cssClass="dropdown-item">Conversion</s:a></li>
                            <li><s:a value="/person/index.html" cssClass="dropdown-item">Person Manager ( by Conventions )</s:a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">Non UI Tags</a>
                        <ul class="dropdown-menu" role="menu">
                            <li><s:url var="url" action="showActionTagDemo" namespace="/tags/non-ui/actionTag"/>
                                <s:a href="%{url}" cssClass="dropdown-item">Action Tag</s:a></li>
                            <li><s:url var="url" namespace="/tags/non-ui" action="date"/>
                                <s:a href="%{url}" cssClass="dropdown-item">Date Tag</s:a></li>
                            <li><s:url var="url" action="debugTagDemo" namespace="/tags/non-ui"/>
                                <s:a href="%{url}" cssClass="dropdown-item">Debug Tag</s:a></li>
                            <li><s:url var="url" action="showGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag"/>
                                <s:a href="%{url}" cssClass="dropdown-item">Iterator Generator Tag</s:a></li>
                            <li>
                                <s:url var="url" action="showAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Append Iterator Tag</s:a>
                            <li>
                                <s:url var="url" action="showMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Merge Iterator Demo</s:a>
                            <li>
                                <s:url var="url" action="showSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Subset Tag</s:a>
                            <li><s:url var="url" action="actionPrefixExampleUsingFreemarker" namespace="/tags/non-ui/actionPrefix"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Action Prefix Example (Freemarker)</s:a></li>
                            <li><s:url var="url" action="testIfTagJsp" namespace="/tags/non-ui/ifTag"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">If Tag (JSP)</s:a></li>
                            <li><s:url var="url" action="testIfTagFreemarker" namespace="/tags/non-ui/ifTag"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">If Tag (Freemarker)</s:a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">UI Tags</a>
                        <ul class="dropdown-menu" role="menu">
                            <li><s:url var="url" namespace="/tags/ui" action="example" method="input"/>
                                <s:a href="%{url}" cssClass="dropdown-item">UI Example</s:a></li>
                            <li><s:url var="url" namespace="/tags/ui" action="exampleVelocity" method="input"/>
                                <s:a href="%{url}" cssClass="dropdown-item">UI Example (Velocity)</s:a></li>
                            <li><s:url var="url" namespace="/tags/ui" action="lotsOfOptiontransferselect" method="input"/>
                                <s:a href="%{url}" cssClass="dropdown-item">Option Transfer Select UI Example</s:a></li>
                            <li><s:url var="url" namespace="/tags/ui" action="moreSelects" method="input"/>
                                <s:a href="%{url}" cssClass="dropdown-item">More Select Box UI Examples</s:a></li>
                            <li>
                                <s:url var="url" namespace="/tags/ui" action="componentTagExample"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Component Tag Example</s:a></li>
                            <li><s:url var="url" namespace="/tags/ui" action="actionTagExample" method="input"/>
                                <s:a href="%{url}" cssClass="dropdown-item">Action Tag Example</s:a></li>
                            <li><s:url var="url" action="index"  namespace="/html5"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Html 5 theme</s:a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                            <i class="bi bi-file-earmark"></i> File</a>
                        <ul class="dropdown-menu" role="menu">
                            <li><s:a namespace="/filedownload" action="index" cssClass="dropdown-item">File Download</s:a></li>
                            <li>
                                <s:url var="url" action="upload" namespace="/fileupload"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Single File Upload</s:a>
                            </li>
                            <li>
                                <s:url var="url" action="dynamicUpload" namespace="/fileupload"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Single File Upload - dynamic config</s:a>
                            </li>
                            <li>
                                <s:url var="url" action="multipleUploadUsingList" namespace="/fileupload"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Multiple File Upload (List)</s:a>

                            </li>
                            <li>
                                <s:url var="url" action="multipleUploadUsingArray" namespace="/fileupload"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Multiple File Upload (Array)</s:a>
                            </li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">Validation</a>
                        <ul class="dropdown-menu" role="menu">
                            <s:url var="quizBasic" namespace="/validation" action="quizBasic" method="input"/>
                            <s:url var="quizClient" namespace="/validation" action="quizClient" method="input"/>
                            <s:url var="quizDwr" namespace="/validation" action="quizDwr" method="input"/>
                            <s:url var="quizClientCss" namespace="/validation" action="quizClientCss" method="input"/>
                            <s:url var="fieldValidatorUrl" action="showFieldValidatorsExamples" namespace="/validation"/>
                            <s:url var="nonFieldValidatorUrl" action="showNonFieldValidatorsExamples" namespace="/validation"/>
                            <s:url var="visitorValidatorUrl" action="showVisitorValidatorsExamples" namespace="/validation"/>
                            <s:url var="clientSideValidationUrl" action="clientSideValidationExample" namespace="/validation"/>
                            <s:url var="storeMessageAcrossRequestExample" namespace="/validation" action="storeErrorsAcrossRequestExample"/>
                            <s:url var="beanValidationUrl" action="bean-validation" namespace="/bean-validation"/>
                            <s:url var="ajaxFormSubmitUrl" action="ajaxFormSubmit" namespace="/validation" method="input"/>
                            <li><s:a href="%{beanValidationUrl}" cssClass="dropdown-item">Bean Validation</s:a></li>
                            <li><s:a href="%{fieldValidatorUrl}" cssClass="dropdown-item">Field Validators</s:a></li>
                            <li><s:a href="%{clientSideValidationUrl}" cssClass="dropdown-item">Field Validators with client-side JavaScript</s:a></li>
                            <li><s:a href="%{nonFieldValidatorUrl}" cssClass="dropdown-item">Non Field Validator</s:a></li>
                            <li><s:a href="%{storeMessageAcrossRequestExample}" cssClass="dropdown-item">Store across request using MessageStoreInterceptor (Example)</s:a></li>
                            <li><s:a href="%{quizBasic}" cssClass="dropdown-item">Validation (basic)</s:a></li>
                            <li><s:a href="%{quizClient}" cssClass="dropdown-item">Validation (client)</s:a></li>
                            <li><s:a href="%{quizClientCss}" cssClass="dropdown-item">Validation (client using css_xhtml theme)</s:a></li>
                            <li><s:a href="%{visitorValidatorUrl}" cssClass="dropdown-item">Visitor Validator</s:a></li>
                            <li><s:a href="%{ajaxFormSubmitUrl}" cssClass="dropdown-item">AJAX Form Submit</s:a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">Examples</a>
                        <ul class="dropdown-menu" role="menu">
                            <li class="dropdown-submenu">
                            <li>
                                <s:url var="url" namespace="/hangman" action="hangmanNonAjax"/>
                                <s:a href="%{url}" cssClass="dropdown-item">Hangman</s:a>
                            </li>
                            <li><s:a value="/person/index.html" cssClass="dropdown-item">Person Manager</s:a></li>
                            <li><s:a value="/skill/index.html" cssClass="dropdown-item">CRUD</s:a></li>
                            <li><s:a value="/wait/index" cssClass="dropdown-item">Execute &amp; Wait</s:a></li>
                            <li><s:a value="/token/index.html" cssClass="dropdown-item">Token</s:a></li>
                            <li><s:url var="url" namespace="/modelDriven" action="modelDriven"/><s:a cssClass="dropdown-item"
                                    href="%{url}">Model Driven</s:a></li>
                            <li><s:a value="/async/index.html" cssClass="dropdown-item">Async</s:a></li>
                            <li><s:a value="/dispatcher/dispatch.action" cssClass="dropdown-item">Dispatcher result - dispatch</s:a></li>
                            <li><s:a value="/dispatcher/forward.action" cssClass="dropdown-item">Dispatcher result - forward</s:a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">Integration</a>
                        <ul class="dropdown-menu" role="menu">
                            <li>
                                <s:url var="url" action="customFreemarkerManagerDemo" namespace="/freemarker"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Demo of usage of a Custom Freemarker Manager</s:a>
                            </li>
                            <li>
                                <s:url var="url" action="standardTags" namespace="/freemarker"/>
                                <s:a href="%{#url}" cssClass="dropdown-item">Demo of Standard Struts Freemarker Tags</s:a>
                            </li>
                            <li><s:a value="/tiles/index.action" cssClass="dropdown-item">Tiles</s:a></li>
                        </ul>
                    </li>
                </ul>

                <ul class="nav navbar-nav ms-auto">
                    <li class="dropdown last">
                        <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                            <i class="bi bi-question-circle"></i> Help</a>
                        <ul class="dropdown-menu">
                            <s:url var="help" action="help" namespace="/" includeContext="false" />
                            <li><s:a value="%{help}" cssClass="dropdown-item">Help</s:a></li>
                            <li><a href="http://struts.apache.org/mail.html" class="dropdown-item"><i class="bi bi-share"></i> User Mailing
                                List</a></li>
                            <li><a href="http://struts.apache.org" class="dropdown-item"><i class="bi bi-share"></i> Struts2 Website</a>
                            </li>
                            <li><a href="http://struts.apache.org/docs/home.html" class="dropdown-item"><i class="bi bi-share"></i>
                                Documentation</a></li>
                        </ul>
                    </li>
                </ul>
        </div>
    </div>
</nav>

<sitemesh:write property="body"/>

<hr>

<footer id="footer" class="footer">
    <div>
        <p style="text-align: center;">
            <a href="<%=sourceUrl %>" class="btn btn-info">View Sources</a>
        </p>
    </div>


    <div class="float-end">
        <div>
            <s:action var="dateAction" name="date" namespace="/" executeResult="true"/>
        </div>
        <!-- end branding -->

        <div>
            <a href="http://struts.apache.org">
                <img src="<s:url value='/img/struts-power.gif' encode='false' includeParams='none'/>"
                     alt="Powered by Struts"/>
            </a>
        </div>
        <!-- end search -->
    </div>

    <div class="float-start">
        Copyright &copy; 2003-<s:property value="#dateAction.now.year + 1900"/>
        <a href="https://www.apache.org">The Apache Software Foundation.</a>
    </div>
</footer>
</body>
</html>
