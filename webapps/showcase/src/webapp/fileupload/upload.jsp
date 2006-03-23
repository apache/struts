
<%@ taglib uri="/webwork" prefix="ww" %>
<html>
<head>
    <title>Showcase</title>
</head>

<body>
    <h1>Fileupload sample</h1>

    <ww:form action="doUpload" method="POST" enctype="multipart/form-data">
        <ww:file name="upload" label="File"/>
        <ww:textfield name="caption" label="Caption"/>
        <ww:submit />
    </ww:form>
</body>
</html>

