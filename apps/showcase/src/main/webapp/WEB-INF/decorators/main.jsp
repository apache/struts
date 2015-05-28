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
    com.opensymphony.xwork2.ActionInvocation inv = com.opensymphony.xwork2.ActionContext.getContext().getActionInvocation();
    org.apache.struts2.dispatcher.mapper.ActionMapping mapping = org.apache.struts2.ServletActionContext.getActionMapping();
    if (inv != null) {
        try {
            com.opensymphony.xwork2.util.location.Location loc = inv.getProxy().getConfig().getLocation();
            sourceUrl += "?config=" + (loc != null ? loc.getURI() + ":" + loc.getLineNumber() : "");
        } catch (Exception e) {
            sourceUrl += "?config=";
        }
        sourceUrl += "&className=" + inv.getProxy().getConfig().getClassName();

        if (inv.getResult() != null && inv.getResult() instanceof org.apache.struts2.dispatcher.StrutsResultSupport) {
            sourceUrl += "&page=" + mapping.getNamespace() + "/" + ((org.apache.struts2.dispatcher.StrutsResultSupport) inv.getResult()).getLastFinalLocation();
        }
    } else {
        sourceUrl += "?page=" + request.getServletPath();
    }
%>
<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Struts2 Showcase for Apache Struts Project">
    <meta name="author" content="The Apache Software Foundation">

    <title><decorator:title default="Struts2 Showcase"/></title>

    <link href="<s:url value='/styles/bootstrap.css' encode='false' includeParams='none'/>" rel="stylesheet"
          type="text/css" media="all">
    <link href="<s:url value='/styles/bootstrap-responsive.css' encode='false' includeParams='none'/>" rel="stylesheet"
          type="text/css" media="all">
    <link href="<s:url value='/styles/main.css' encode='false' includeParams='none'/>" rel="stylesheet" type="text/css"
          media="all"/>

    <script src="<s:url value='/js/jquery-1.8.2.min.js' encode='false' includeParams='none'/>"></script>
    <script src="<s:url value='/js/bootstrap.min.js' encode='false' includeParams='none'/>"></script>
    <script type="text/javascript">
        $(function () {
            $('.dropdown-toggle').dropdown();
            var alerts = $('ul.alert').wrap('<div />');
            alerts.prepend('<a class="close" data-dismiss="alert" href="#">&times;</a>');
            alerts.alert();
        });
    </script>

    <!-- Prettify -->
    <link href="<s:url value='/styles/prettify.css' encode='false' includeParams='none'/>" rel="stylesheet">
    <script src="<s:url value='/js/prettify.js' encode='false' includeParams='none'/>"></script>

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <decorator:head/>
</head>

<body id="page-home" onload="prettyPrint();">

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <s:url var="home" action="showcase" namespace="/" includeContext="false" />
            <s:a value="%{home}" cssClass="brand">Struts2 Showcase</s:a>
            <div class="nav-collapse">
                <ul class="nav">
                    <li><s:a value="%{home}"><i class="icon-home"></i> Home</s:a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Configuration<b
                                class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><s:a action="actionChain1!input" namespace="/actionchaining"
                                     includeParams="none">Action Chaining</s:a></li>
                            <li><s:a action="index" namespace="/config-browser"
                                     includeParams="none">Config Browser</s:a></li>
                            <s:url var="conversion" action="index" namespace="/conversion" includeContext="false" />
                            <li><s:a value="%{conversion}">Conversion</s:a></li>
                            <li><s:a value="/person/index.html">Person Manager ( by Conventions )</s:a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Tags<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li class="dropdown-submenu">
                                <a href="#">Non UI Tags</a>
                                <ul class="dropdown-menu">
                                    <li><s:url var="url" action="showActionTagDemo" namespace="/tags/non-ui/actionTag"/><s:a
                                            href="%{url}">Action Tag</s:a></li>
                                    <li><s:url var="url" namespace="/tags/non-ui" action="date"/><s:a
                                            href="%{url}">Date Tag</s:a></li>
                                    <li><s:url var="url" action="debugTagDemo" namespace="/tags/non-ui"/><s:a
                                            href="%{url}">Debug Tag</s:a></li>
                                    <li><s:url var="url" action="showGeneratorTagDemo"
                                               namespace="/tags/non-ui/iteratorGeneratorTag"/><s:a
                                            href="%{url}">Iterator Generator Tag</s:a></li>
                                    <li>
                                        <s:url var="url" action="showAppendTagDemo"
                                               namespace="/tags/non-ui/appendIteratorTag"/>
                                        <s:a href="%{#url}">Append Iterator Tag</s:a>
                                    <li>
                                        <s:url var="url" action="showMergeTagDemo"
                                               namespace="/tags/non-ui/mergeIteratorTag"/>
                                        <s:a href="%{#url}">Merge Iterator Demo</s:a>
                                    <li>
                                        <s:url var="url" action="showSubsetTagDemo"
                                               namespace="/tags/non-ui/subsetIteratorTag"/>
                                        <s:a href="%{#url}">Subset Tag</s:a>
                                    <li><s:url var="url" action="actionPrefixExampleUsingFreemarker"
                                               namespace="/tags/non-ui/actionPrefix"/><s:a
                                            href="%{#url}">Action Prefix Example (Freemarker)</s:a></li>
                                    <li><s:url var="url" action="testIfTagJsp" namespace="/tags/non-ui/ifTag"/><s:a
                                            href="%{#url}">If Tag (JSP)</s:a></li>
                                    <li><s:url var="url" action="testIfTagFreemarker"
                                               namespace="/tags/non-ui/ifTag"/><s:a
                                            href="%{#url}">If Tag (Freemarker)</s:a></li>
                                </ul>

                            </li>
                            <li class="dropdown-submenu">
                                <a href="#">UI Tags</a>
                                <ul class="dropdown-menu">
                                    <li><s:url var="url" namespace="/tags/ui" action="example" method="input"/><s:a
                                            href="%{url}">UI Example</s:a></li>
                                    <li><s:url var="url" namespace="/tags/ui" action="exampleVelocity"
                                               method="input"/><s:a href="%{url}">UI Example (Velocity)</s:a></li>
                                    <li><s:url var="url" namespace="/tags/ui" action="lotsOfOptiontransferselect"
                                               method="input"/><s:a
                                            href="%{url}">Option Transfer Select UI Example</s:a></li>
                                    <li><s:url var="url" namespace="/tags/ui" action="moreSelects" method="input"/><s:a
                                            href="%{url}">More Select Box UI Examples</s:a></li>
                                    <li>
                                        <s:url var="url" namespace="/tags/ui" action="componentTagExample"/>
                                        <s:a href="%{#url}">Component Tag Example</s:a></li>
                                    <li><s:url var="url" namespace="/tags/ui" action="actionTagExample" method="input"/><s:a
                                            href="%{url}">Action Tag Example</s:a></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">File<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><s:a namespace="/filedownload" action="index">File Download</s:a></li>
                            <li class="dropdown-submenu">
                                <a href="#">File Upload</a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <s:url var="url" action="upload" namespace="/fileupload"/>
                                        <s:a href="%{#url}">Single File Upload</s:a>
                                    </li>
                                    <li>
                                        <s:url var="url" action="multipleUploadUsingList" namespace="/fileupload"/>
                                        <s:a href="%{#url}">Multiple File Upload (List)</s:a>

                                    </li>
                                    <li>
                                        <s:url var="url" action="multipleUploadUsingArray" namespace="/fileupload"/>
                                        <s:a href="%{#url}">Multiple File Upload (Array)</s:a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Examples<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li class="dropdown-submenu">
                            <li>
                                <s:url var="url" namespace="/hangman" action="hangmanNonAjax"/>
                                <s:a href="%{url}">Hangman</s:a>
                            </li>
                            <li><s:a value="/person/index.html">Person Manager</s:a></li>
                            <li><s:a value="/skill/index.html">CRUD</s:a></li>
                            <li><s:a value="/wait/index.html">Execute &amp; Wait</s:a></li>
                            <li><s:a value="/token/index.html">Token</s:a></li>
                            <li class="dropdown-submenu">
                                <a href="#">Validation</a>
                                <ul class="dropdown-menu">

                                    <s:url var="quizBasic" namespace="/validation" action="quizBasic" method="input"/>
                                    <s:url var="quizClient" namespace="/validation" action="quizClient" method="input"/>
                                    <s:url var="quizClientCss" namespace="/validation" action="quizClientCss" method="input"/>
                                    <s:url var="fieldValidatorUrl" action="showFieldValidatorsExamples" namespace="/validation"/>
                                    <s:url var="nonFieldValidatorUrl" action="showNonFieldValidatorsExamples" namespace="/validation"/>
                                    <s:url var="visitorValidatorUrl" action="showVisitorValidatorsExamples" namespace="/validation"/>
                                    <s:url var="clientSideValidationUrl" action="clientSideValidationExample" namespace="/validation"/>
                                    <s:url var="storeMessageAcrossRequestExample" namespace="/validation" action="storeErrorsAcrossRequestExample"/>
                                    <s:url var="beanValidationUrl" action="bean-validation" namespace="/bean-validation"/>
                                    <li><s:a href="%{beanValidationUrl}">Bean Validation</s:a></li>
                                    <li><s:a href="%{fieldValidatorUrl}">Field Validators</s:a></li>
                                    <li><s:a href="%{clientSideValidationUrl}">Field Validators with client-side JavaScript</s:a></li>
                                    <li><s:a href="%{nonFieldValidatorUrl}">Non Field Validator</s:a></li>
                                    <li><s:a href="%{storeMessageAcrossRequestExample}">Store across request using MessageStoreInterceptor (Example)</s:a></li>
                                    <li><s:a href="%{quizBasic}">Validation (basic)</s:a></li>
                                    <li><s:a href="%{quizClient}">Validation (client)</s:a></li>
                                    <li><s:a href="%{quizClientCss}">Validation (client using css_xhtml theme)</s:a></li>
                                    <li><s:a href="%{visitorValidatorUrl}">Visitor Validator</s:a></li>
                                </ul>
                            </li>
                            <li><s:url var="url" namespace="/modelDriven" action="modelDriven"/><s:a
                                    href="%{url}">Model Driven</s:a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Integration<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li class="dropdown-submenu">
                                <a href="#">Freemarker</a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <s:url var="url" action="customFreemarkerManagerDemo" namespace="/freemarker"/>
                                        <s:a href="%{#url}">Demo of usage of a Custom Freemarker Manager</s:a>
                                    </li>
                                    <li>
                                        <s:url var="url" action="standardTags" namespace="/freemarker"/>
                                        <s:a href="%{#url}">Demo of Standard Struts Freemarker Tags</s:a>
                                    </li>
                                </ul>
                            </li>
                            <li><s:a value="/tiles/index.action">Tiles</s:a></li>
                        </ul>
                    </li>
                </ul>

                <ul class="nav pull-right">
                    <li class="dropdown last">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-flag"></i> Help<b
                                class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><s:a value="/help.jsp">Help</s:a></li>
                            <li><a href="http://struts.apache.org/mail.html"><i class="icon-share"></i> User Mailing
                                List</a></li>
                            <li><a href="http://struts.apache.org"><i class="icon-share"></i> Struts2 Website</a>
                            </li>
                            <li><a href="http://struts.apache.org/docs/home.html"><i class="icon-share"></i>
                                Documentation</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </div>
</div>

<decorator:body/>


<hr>

<footer id="footer" class="footer">
    <div>
        <p style="text-align: center;">
            <a href="<%=sourceUrl %>" class="btn btn-info">View Sources</a>
        </p>
    </div>


    <div class="pull-right">
        <div>
            <s:action var="dateAction" name="date" namespace="/" executeResult="true"/>
        </div>
        <!-- end branding -->

        <div>
            <a href="http://struts.apache.org/2.x/">
                <img src="<s:url value='/img/struts-power.gif' encode='false' includeParams='none'/>"
                     alt="Powered by Struts"/>
            </a>
        </div>
        <!-- end search -->
    </div>

    <div class="pull-left">
        Copyright &copy; 2003-<s:property value="#dateAction.now.year + 1900"/>
        <a href="http://www.apache.org">
            The Apache Software Foundation.
        </a>
    </div>
</footer>
</body>
</html>
