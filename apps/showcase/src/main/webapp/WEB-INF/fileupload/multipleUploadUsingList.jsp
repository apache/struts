<%@ page 
	language="java" 
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Fileupload sample - Multiple fileupload using List</title>
</head>

<body>
<div class="page-header">
	<h1>Fileupload sample - Multiple fileupload using List</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:form action="doMultipleUploadUsingList" method="POST" enctype="multipart/form-data">
				<s:file label="File (1)" name="upload" />
				<s:file label="File (2)" name="upload" />
				<s:file label="FIle (3)" name="upload" />
				<s:submit cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>

</body>
</html>