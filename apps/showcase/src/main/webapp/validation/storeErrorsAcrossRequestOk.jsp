<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Validation - Store Errors Across Request Example</title>
</head>
<body>

	<s:actionmessage/>
	<s:actionerror/>
	<s:fielderror />

	<h2>Ok !</h2>
	
	<s:url id="url" value="/validation/storeErrorsAcrossRequestExample.jsp" />
	<s:a href="%{#id}">Try Again</s:a>

</body>
</html>

