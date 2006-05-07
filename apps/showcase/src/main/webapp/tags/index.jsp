<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tags </title>
</head>
<body>
<h1> Tags </h1>

<ul>
	<li><saf:url id="url" value="/tags/non-ui/" /><saf:a href="%{url}">Non UI Tags Examples</saf:a></li>
	<li><saf:url id="url" value="/tags/ui" /><saf:a href="%{url}">UI Tags Example</saf:a></li>
</ul>

</body>
</html>
