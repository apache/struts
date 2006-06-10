<%@taglib uri="/struts-action" prefix="saf" %>

<html>
<head>
	<title>Showcase - Tags - UI Tags</title>
</head>
<body>
	<h1>UI Tags</h1>
	
	<ul>
		<li><saf:url id="url" namespace="/tags/ui" action="example" method="input" /><saf:a href="%{url}">UI Example</saf:a></li>
		<li><saf:url id="url" namespace="/tags/ui" action="exampleVelocity" method="input" /><saf:a href="%{url}">UI Example (Velocity)</saf:a></li>
		<li><saf:url id="url" namespace="/tags/ui" action="lotsOfOptiontransferselect" method="input" /><saf:a href="%{url}">Option Transfer Select UI Example</saf:a></li>
		<li><saf:url id="url" namespace="/tags/ui" action="lotsOfRichtexteditor" method="input" /><saf:a href="%{url}">Rich Text Editor UI Example</saf:a></li>
		<li><saf:url id="url" namespace="/tags/ui" value="treeExampleStatic.jsp" /><saf:a href="%{url}">Tree Example (static)</saf:a>
		<li><saf:url id="url" namespace="/tags/ui" action="showDynamicTreeAction"/><saf:a href="%{url}">Tree Example (dynamic)</saf:a>
		<li><saf:url id="url" value="componentTagExample.jsp"/><saf:a href="%{#url}">Component Tag Example</saf:a>
		<%--li><saf:url id="url" namespace="/tags/ui" action="populateUsingIterator" method="input" /><saf:a href="%{url}">UI population using iterator tag</saf:a></li--%>
	</ul>
</body>
</html>
