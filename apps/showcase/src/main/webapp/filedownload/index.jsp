<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Showcase - Fileupload</title>
</head>

<body>
    <h1>File Download Example</h1>

    <ul>
    <li>
        <s:url var="url" action="download"/><s:a href="%{url}">Download image file.</s:a> 
          The browser should display the Struts logo.
    </li>
    <li>
        <s:url var="url" action="download2"/><s:a href="%{url}">Download ZIP file.</s:a> 
          The browser should prompt for a location to save the ZIP file.
    </li>
    </ul>

</body>
</html>

