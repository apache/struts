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
	String sourceUrl = request.getContextPath()+"/viewSource.action";
	com.opensymphony.xwork2.ActionInvocation inv = com.opensymphony.xwork2.ActionContext.getContext().getActionInvocation();
	org.apache.struts2.dispatcher.mapper.ActionMapping mapping = org.apache.struts2.ServletActionContext.getActionMapping();
	if (inv != null) {
		try {
			com.opensymphony.xwork2.util.location.Location loc = inv.getProxy().getConfig().getLocation();
			sourceUrl += "?config="+(loc != null ? loc.getURI()+":"+loc.getLineNumber() : "");
		} catch (Exception e) {
			sourceUrl += "?config=";
		}
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

<html lang="en">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
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
			<s:a value="/showcase.jsp" cssClass="brand">Struts2 Showcase</s:a>
			<div class="nav-collapse">
				<ul class="nav">
					<li><s:a value="/showcase.jsp"><i class="icon-home"></i> Home</s:a></li>
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">Configuration<b
								class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><s:a action="actionChain1!input" namespace="/actionchaining"
							         includeParams="none">Action Chaining</s:a></li>
							<li><s:a action="index" namespace="/config-browser"
							         includeParams="none">Config Browser</s:a></li>
							<li><s:a value="/conversion/index.jsp">Conversion</s:a></li>
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
										<s:url var="url" namespace="/tags/ui" action="treeExampleStatic"/>
										<s:a href="%{url}">Tree Example (static)</s:a>
									<li>
										<s:url var="url" namespace="/tags/ui" action="showDynamicTreeAction"/>
										<s:a href="%{url}">Tree Example (dynamic)</s:a>
									<li>
										<s:url var="url" namespace="/tags/ui" action="showDynamicAjaxTreeAction"/>
										<s:a href="%{url}">Tree Example (dynamic ajax loading)</s:a>
									<li>
										<s:url var="url" namespace="/tags/ui" action="componentTagExample"/>
										<s:a href="%{#url}">Component Tag Example</s:a>
									<li><s:url var="url" namespace="/tags/ui" action="actionTagExample" method="input"/><s:a
											href="%{url}">Action Tag Example</s:a></li>
									<li><s:url var="url" namespace="/tags/ui" action="datepicker"/><s:a
											href="%{#url}">DateTime picker tag - Pick a date</s:a></li>
									<li><s:url var="url" namespace="/tags/ui" action="timepicker"/><s:a
											href="%{#url}">DateTime picker tag - Pick a time</s:a></li>
									<%--li><s:url var="url" namespace="/tags/ui" action="populateUsingIterator" method="input" /><s:a href="%{url}">UI population using iterator tag</s:a></li--%>
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
								<a href="#">Hangman</a>
								<ul class="dropdown-menu">
									<li><s:url var="url" namespace="/hangman" action="hangmanNonAjax"/><s:a
											href="%{url}">Hangman (Non Ajax)</s:a></li>
									<li><s:url var="url" namespace="/hangman" action="hangmanAjax"/><s:a
											href="%{url}">Hangman (Ajax - Experimental)</s:a></li>
								</ul>
							</li>
							<li><s:a value="/person/index.html">Person Manager</s:a></li>
							<li><s:a value="/skill/index.html">CRUD</s:a></li>
							<li><s:a value="/wait/index.html">Execute &amp; Wait</s:a></li>
							<li><s:a value="/token/index.html">Token</s:a></li>
							<li><s:a value="/validation/index.action">Validation</s:a></li>
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
							<li><s:a namespace="/jsf" action="index">JavaServer Faces</s:a></li>
							<li><s:a namespace="/integration" action="editGangster">Struts 1 Integration</s:a></li>
							<li><s:a value="/tiles/index.action">Tiles</s:a></li>
						</ul>
					</li>
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">AJAX<b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><s:a value="/ajax/index.html">Ajax plugin</s:a></li>
							<li><s:a value="/chat/index.html">Ajax Chat</s:a></li>
						</ul>
					</li>
					<li><s:a value="/interactive/index.action">Interactive Demo</s:a></li>
				</ul>
				<ul class="nav pull-right">
					<li class="dropdown last">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-flag"></i> Help<b
								class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><s:a value="/help.jsp">Help</s:a></li>
							<li><a href="http://struts.apache.org/mail.html"><i class="icon-share"></i> User Mailing
								List</a></li>
							<li><a href="http://struts.apache.org/2.x/"><i class="icon-share"></i> Struts2 Website</a>
							</li>
							<li><a href="http://struts.apache.org/2.x/docs/home.html"><i class="icon-share"></i>
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
