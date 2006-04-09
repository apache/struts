<%@ taglib uri="/struts-action" prefix="saf" %>
<html>
<head>
    <title>Showcase</title>
</head>

<body>
<h1>Fileupload sample</h1>

<p>
    <ul>
        <li>ContentType: <saf:property value="uploadContentType" /></li>
        <li>FileName: <saf:property value="uploadFileName" /></li>
        <li>File: <saf:property value="upload" /></li>
        <li>Caption:<saf:property value="caption" /></li>
    </ul>
</p>

</body>
</html>

