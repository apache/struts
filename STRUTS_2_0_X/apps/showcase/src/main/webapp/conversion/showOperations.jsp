<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion - Tiger 5 Enum</title>
</head>
<body>

	<s:iterator value="%{selectedOperations}" status="stat">
		<s:property value="%{top.name()}" /><br/>
	</s:iterator>
	
</body>
</html>