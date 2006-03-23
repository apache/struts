<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>

<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib uri="sitemesh-page" prefix="page" %>
<%@ taglib prefix="ww" uri="/webwork" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><decorator:title default="WebWork Showcase"/></title>
    <link href="<ww:url value='/styles/main.css'/>" rel="stylesheet" type="text/css" media="all"/>
    <link href="<ww:url value='/webwork/niftycorners/niftyCorners.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<ww:url value='/webwork/niftycorners/niftyPrint.css'/>" rel="stylesheet" type="text/css" media="print"/>

    <script language="JavaScript" type="text/javascript" src="<ww:url value='/webwork/niftycorners/nifty.js'/>"></script>

    <script language="JavaScript" type="text/javascript">

        window.onload=function(){
            if(!NiftyCheck())
                return;
            Rounded("blockquote","tr bl","#ECF1F9","#CDFFAA","smooth border #88D84F");
            Rounded("div#outer-header", "all", "white", "#818EBD", "smooth border #434F7C");
            Rounded("div#footer", "all", "white", "#818EBD", "smooth border #434F7C");
        }

    </script>

    <decorator:head/>
</head>

<body id="page-home">


<div id="page">
    <div id="outer-header">
        <div id="header" class="clearfix">
            <div id="branding">
                <h1 class="title">WebWork Showcase</h1>
                <ww:action id="dateAction" name="date" namespace="/" executeResult="true" />
            </div><!-- end branding -->

            <div id="search">
                <img src="<ww:url value='/images/logo-small.png'/>" alt="WebWork logo"/>
            </div><!-- end search -->

            <hr/>
        </div>
    </div><!-- end header -->

    <div id="content" class="clearfix">

        <decorator:body/>

        <div id="nav">
            <div class="wrapper">
                <h2 class="accessibility">Navigation</h2>
                <ul class="clearfix">
                    <li><strong><a href="<ww:url action="showcase" namespace="/"  includeParams="none" />">Home</a></strong></li>
                    <li><a href="<ww:url action="index" namespace="/config-browser" includeParams="none" />">Config Browser</a></li>
                    <li><a href="<ww:url action="guess" namespace="/continuations" />">Continuations</a></li>
                    <li><a href="<ww:url value="/tags/index.jsp"/>">Tags</a></li>
                    <li><a href="<ww:url action="upload" namespace="/fileupload" includeParams="none"/>">File Upload</a></li>
                    <li><a href="<ww:url value="/empmanager/index.jsp"/>">CRUD</a></li>
                    <li><a href="<ww:url value="/person/index.jsp"/>">Person Manager</a></li>
                    <li><a href="<ww:url value="/validation/index.jsp"/>">Validation</a></li>
                    <li><a href="<ww:url value="/ajax/index.jsp"/>">AJAX</a></li>
                    <li><a href="<ww:url action="actionChain1!input" namespace="/actionchaining"  includeParams="none" />">Action Chaining</a></li>
                    <li><a href="<ww:url value="/wait/index.jsp"/>">Execute & Wait</a></li>
                    <li><a href="<ww:url value="/token/index.jsp"/>">Token</a></li>
                    <li><a href="<ww:url value="/filedownload/index.jsp"/>">File Download</a></li>
                    <li class="last"><a href="<ww:url value="/help.jsp"/>">Help</a></li>
                </ul>
            </div>
            <hr/>

        </div><!-- end nav -->

    </div><!-- end content -->


    <div id="footer" class="clearfix">
        <p>&copy; Copyright 2003-<ww:property value="#dateAction.now.year + 1900" /> OpenSymphony</p>
    </div><!-- end footer -->
    <p/>

</div><!-- end page -->

</body>
</html>
