<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Conversion - Populate into Struts action class a Set of Address.java Object</title>
</head>
<body>
<div class="page-header">
	<h1>Conversion - Populate into Struts action class a Set of Address.java Object</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">
			<s:iterator value="%{addresses}">
				<s:property value="%{top.id}" /> -> <s:property value="%{top.address}" /><br/>
			</s:iterator>
		</div>
	</div>
</div>
</body>
</html>