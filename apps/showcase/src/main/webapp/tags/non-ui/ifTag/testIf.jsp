<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags" %>
    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<!--  1 -->
<s:if test="true">
	1]THIS SHOULD APPEAR <br/>
</s:if>
<s:else>
	THIS SHOULD NOT APPEAR <br/>
</s:else>


<!--  2 -->
<s:if test="false">
	THIS SHOULD NOT APPEAR <br/>
</s:if>
<s:elseif test="true">
	2]THIS SHOULD APPEAR <br/>
</s:elseif>

<!--  3 -->
<s:if test="false">
	THIS SHOULD NOT APPEAR <br/>
</s:if>
<s:elseif test="false">
	THIS SHOULD NOT APPEAR <br/>
</s:elseif>
<s:elseif test="true">
	3]THIS SHOULD APPEAR <br/>
</s:elseif>
<s:elseif test="true">
	THIS SHOULD NOT APPEAR <br/>
</s:elseif>
<s:else>
	THIS SHOULD NOT APPEAR <br/>
</s:else>

<!-- 4 -->
<s:if test="false">
	THIS SHOULD NOT APPEAR<br/>
</s:if>
<s:elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</s:elseif>
<s:else>
	4]THIS SHOULD APPEAR<br/>
</s:else>

<!-- 5 -->
<s:if test="false">
	THIS SHOULD NOT APPEAR<br/>
</s:if>
<s:elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</s:elseif>
<s:elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</s:elseif>
<s:elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</s:elseif>
<s:else>
	5]THIS SHOULD APPEAR<br/>
</s:else>


</body>
</html>