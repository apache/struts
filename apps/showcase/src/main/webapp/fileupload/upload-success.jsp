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

<p>
    <ul>
        <li>ContentType: <s:property value="uploadContentType" /></li>
        <li>FileName: <s:property value="uploadFileName" /></li>
        <li>File: <s:property value="upload" /></li>
        <li>Caption:<s:property value="caption" /></li>
    </ul>
</p>

</body>
</html>

