<%@ page 
	language="java" 
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<title>Showcase - fileupload - multiple fileupload using List</title>
</head>
<body>

<s:form action="doMultipleUploadUsingList" method="POST" enctype="multipart/form-data">
	<s:file label="File (1)" name="upload" />
	<s:file label="File (2)" name="upload" />
	<s:file label="FIle (3)" name="upload" />
	<s:submit />
</s:form>

</body>
</html>