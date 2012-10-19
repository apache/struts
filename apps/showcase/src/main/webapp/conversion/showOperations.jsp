<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Conversion - Tiger 5 Enum</title>
</head>
<body>
<div class="page-header">
	<h1>Conversion - Tiger 5 Enum</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">
			<s:iterator value="%{selectedOperations}" status="stat">
				<s:property value="%{top.name()}" /><br/>
			</s:iterator>
		</div>
	</div>
</div>
</body>
</html>