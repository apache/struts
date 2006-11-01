<%@ page 
	language="java" 
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Showcase</title>
</head>

<body>
    <h1>Fileupload sample</h1>

	<s:actionerror />
	<s:fielderror />
    <s:form action="doUpload" method="POST" enctype="multipart/form-data">
        <s:file name="upload" label="File"/>
        <s:textfield name="caption" label="Caption"/>
        <s:submit />
    </s:form>
</body>
</html>

