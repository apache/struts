<%--
  ~ Copyright (c) 2006, Your Corporation. All Rights Reserved.
  --%>

<%@taglib uri="/webwork" prefix="ww" %>

<html>
<head>
	<title>Showcase - Tags - UI Tags</title>
</head>
<body>
	<h1>UI Tags</h1>
	
	<ul>
		<li><ww:url id="url" namespace="/tags/ui" action="example" method="input" /><ww:a href="%{url}">UI Example</ww:a></li>
		<li><ww:url id="url" namespace="/tags/ui" action="exampleVelocity" method="input" /><ww:a href="%{url}">UI Example (Velocity)</ww:a></li>
		<li><ww:url id="url" namespace="/tags/ui" action="lotsOfOptiontransferselect" method="input" /><ww:a href="%{url}">Option Transfer Select UI Example</ww:a></li>
		<li><ww:url id="url" namespace="/tags/ui" action="lotsOfRichtexteditor" method="input" /><ww:a href="%{url}">Rich Text Editor UI Example</ww:a></li>
		<%--li><ww:url id="url" namespace="/tags/ui" action="populateUsingIterator" method="input" /><ww:a href="%{url}">UI population using iterator tag</ww:a></li--%>
	</ul>
</body>
</html>
