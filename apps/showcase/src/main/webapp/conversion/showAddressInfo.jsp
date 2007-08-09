<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion - Set </title>
</head>
<body>
	<s:iterator value="%{addresses}">
		<s:property value="%{top.id}" /> -> <s:property value="%{top.address}" /><br/>
	</s:iterator>
</body>
</html>