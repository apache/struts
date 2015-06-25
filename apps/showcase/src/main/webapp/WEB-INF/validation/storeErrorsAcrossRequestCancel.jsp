<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Validation - Store Errors Across Request Example</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>Application Canceled</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:actionmessage cssClass="alert alert-info"/>
			<s:actionerror cssClass="alert alert-error"/>
			<s:fielderror cssClass="alert alert-error"/>

			<s:url var="url" value="/validation/storeErrorsAcrossRequestExample.jsp" />
			<s:a href="%{#url}">Try Again</s:a>
		</div>
	</div>
</div>
</body>
</html>
