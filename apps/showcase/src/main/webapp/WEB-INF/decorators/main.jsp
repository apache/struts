<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
    
    // Calculate the view sources url
    String sourceUrl = request.getContextPath()+"/viewSource.action";
    com.opensymphony.xwork2.ActionInvocation inv = com.opensymphony.xwork2.ActionContext.getContext().getActionInvocation();
    org.apache.struts2.dispatcher.mapper.ActionMapping mapping = org.apache.struts2.ServletActionContext.getActionMapping();
    if (inv != null) {
        com.opensymphony.xwork2.util.location.Location loc = inv.getProxy().getConfig().getLocation();
        sourceUrl += "?config="+(loc != null ? loc.getURI()+":"+loc.getLineNumber() : "");
        sourceUrl += "&className="+inv.getProxy().getConfig().getClassName();
        
        if (inv.getResult() != null && inv.getResult() instanceof org.apache.struts2.dispatcher.StrutsResultSupport) {
	        sourceUrl += "&page="+mapping.getNamespace()+"/"+((org.apache.struts2.dispatcher.StrutsResultSupport)inv.getResult()).getLastFinalLocation();
        }
    } else {
        sourceUrl += "?page="+request.getServletPath();
    }
%>
<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="s" uri="/struts-tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><decorator:title default="Struts Showcase"/></title>
    <link href="<s:url value='/styles/main.css' encode='false' includeParams='none'/>" rel="stylesheet" type="text/css" media="all"/>
    <decorator:head/>
</head>

<body id="page-home">


<div id="page">
    <div id="outer-header">
        <div id="header" class="clearfix">
            <div id="branding">
                <h1 class="title">Struts Showcase</h1>
                <s:action var="dateAction" name="date" namespace="/" executeResult="true" />
            </div><!-- end branding -->

            <div id="search">
                <img src="<s:url value='/images/struts-power.gif' encode='false' includeParams='none'/>" alt="Powered by Struts"/>
            </div><!-- end search -->

            <hr/>
            <div style="clear: both;"></div>
        </div>
    </div><!-- end header -->

    <div id="content" class="clearfix">
        <div id="nav">
            <div class="wrapper">
                <h2 class="accessibility">Navigation</h2>
                <ul class="clearfix">
                    <li><a href="<s:url value="/showcase.jsp"/>">Home</a></li>
                    <li><a href="<s:url value="/ajax/index.jsp"/>">Ajax</a></li>
                    <li><a href="<s:url value="/chat/index.jsp"/>">Ajax Chat</a></li>
                    <li><a href="<s:url action="actionChain1!input" namespace="/actionchaining"  includeParams="none" />">Action Chaining</a></li>
                    <li><a href="<s:url action="index" namespace="/config-browser" includeParams="none" />">Config Browser</a></li>
                    <li><a href="<s:url value="/conversion/index.jsp"/>">Conversion</a></li>
                    <li><a href="<s:url value="/empmanager/index.jsp"/>">CRUD</a></li>
                    <li><a href="<s:url value="/wait/index.jsp"/>">Execute & Wait</a></li>
                    <li><a href="<s:url value="/filedownload/index.jsp"/>">File Download</a></li>
                    <li><a href="<s:url value="/fileupload/index.jsp" />">File Upload</a></li>
                    <li><a href="<s:url value="/freemarker/index.jsp"/>">Freemarker</a>
                    <li><a href="<s:url action="hangmanMenu" namespace="/hangman"/>">Hangman</a></li>
                    <li><a href="<s:url value="/jsf/index.jsp"/>">JavaServer Faces</a></li>
                    <li><a href="<s:url value="/tags/index.jsp"/>">Tags</a></li>
                    <li><a href="<s:url value="/tiles/index.action" />">Tiles</a></li>
                    <li><a href="<s:url value="/token/index.jsp"/>">Token</a></li>
                    <li><a href="<s:url value="/validation/index.jsp"/>">Validation</a></li>
                    <li><a href="<s:url value="/interactive/index.jsp"/>">Interactive Demo</a></li>
                    <li><a href="<s:url value="/person/index.jsp"/>">Person Manager</a></li>
                    <li class="last"><a href="<s:url value="/help.jsp"/>">Help</a></li>
                </ul>
            </div>
            <hr/>

        </div><!-- end nav -->

        <decorator:body/>
        
    </div><!-- end content -->

	<div>
    	<p>
    		<a href="<%=sourceUrl %>">View Sources</a>
    	</p>
	</div>
    <div id="footer" class="clearfix">
        <p>Copyright &copy; 2003-<s:property value="#dateAction.now.year + 1900" /> The Apache Software Foundation.</p>
        <s:hidden name="project-name" value="Struts 2" />
    </div><!-- end footer -->
    <p/>

</div><!-- end page -->

</body>
</html>
