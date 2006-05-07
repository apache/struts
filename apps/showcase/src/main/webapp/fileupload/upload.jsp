
<%@ taglib uri="/struts-action" prefix="saf" %>
<html>
<head>
    <title>Showcase</title>
</head>

<body>
    <h1>Fileupload sample</h1>

    <saf:form action="doUpload" method="POST" enctype="multipart/form-data">
        <saf:file name="upload" label="File"/>
        <saf:textfield name="caption" label="Caption"/>
        <saf:submit />
    </saf:form>
</body>
</html>

