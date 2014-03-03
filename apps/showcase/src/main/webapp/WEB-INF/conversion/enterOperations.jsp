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


			See the jsp code <s:url var="url" action="showEnumJspCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for OperationsEnum.java <s:url var="url" action="showOperationsEnumJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for OperationsEnumAction.java <s:url var="url" action="showOperationEnumActionJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for EnumTypeConverter.java  <s:url var="url" action="showEnumTypeConverterJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the properties for OperationsEnumAction-conversion.properties <s:url var="url" action="showOperationsEnumActionConversionProperties" namespace="/conversion" /><s:a href="%{#url}">here.</s:a>
			<br/>
			<br/>

				<s:form action="submitOperationEnumInfo" namespace="/conversion">
					<s:checkboxlist label="Operations"
									name="selectedOperations"
									list="%{availableOperations}"
									listKey="name()"
									listValue="name()"
									/>
					<s:submit cssClass="btn btn-primary"/>
				</s:form>

		</div>
	</div>
</div>
</body>
</html>