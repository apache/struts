<%@ taglib uri="/struts-action" prefix="saf" %>
<html>
<head>
    <title>Showcase</title>
</head>

<body>
    <h1>File Download Example</h1>

    Click this <saf:url id="url" action="download"/><saf:a href="%{url}">link</saf:a> to download Struts logo.

</body>
</html>

