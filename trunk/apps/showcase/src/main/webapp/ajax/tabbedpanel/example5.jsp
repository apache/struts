<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<link rel="stylesheet" type="text/css" href="<s:url value='/struts/tabs.css' />" />
<s:head theme="ajax" />
</head>
<body>
<s:url id="url" action="example5" namespace="/nodecorate" includeContext="false" />
<s:tabbedPanel id="tp" theme="ajax">
	<s:panel id="t1" tabName="Tab 1" href="%{#url}">
		<s:form action="example5" namespace="" theme="ajax" validate="true" >
			<s:textfield label="Name" name="name" theme="ajax" />
			<s:textfield label="Age" name="age" theme="ajax" />
			<s:submit />
		</s:form>
	</s:panel>
</s:tabbedPanel>

</body>
</html>

