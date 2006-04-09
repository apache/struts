<%@ taglib uri="/webwork" prefix="ww" %>
<html>
<head>
    <title>Showcase</title>
</head>

<body>
<h1>Fileupload sample</h1>

<p>
    <ul>
        <li>ContentType: <ww:property value="uploadContentType" /></li>
        <li>FileName: <ww:property value="uploadFileName" /></li>
        <li>File: <ww:property value="upload" /></li>
        <li>Caption:<ww:property value="caption" /></li>
    </ul>
</p>

</body>
</html>

