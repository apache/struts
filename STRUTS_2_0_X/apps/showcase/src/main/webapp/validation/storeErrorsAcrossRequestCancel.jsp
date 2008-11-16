<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<s:actionmessage/>
	<s:actionerror/>
	<s:fielderror />

	<h1>Application Canceled</h1>	
	<s:url id="url" value="/validation/storeErrorsAcrossRequestExample.jsp" />
	<s:a href="%{#url}">Try Again</s:a>
</body>
</html>

