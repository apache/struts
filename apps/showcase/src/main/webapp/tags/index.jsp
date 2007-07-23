<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags </title>
</head>
<body>
<h1> Tags </h1>

<ul>
    <li><s:url id="url" value="/tags/non-ui/" /><s:a href="%{url}">Non UI Tags Examples</s:a></li>
    <li><s:url id="url" value="/tags/ui" /><s:a href="%{url}">UI Tags Example</s:a></li>
</ul>

</body>
</html>
