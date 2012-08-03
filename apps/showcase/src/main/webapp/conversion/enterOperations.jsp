<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion - Tiger 5 Enum </title>
</head>
<body>

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
		<s:submit />
	</s:form>

</body>
</html>