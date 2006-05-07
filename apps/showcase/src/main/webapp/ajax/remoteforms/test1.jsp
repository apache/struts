<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<saf:head theme="ajax"/>
</head>

<body>
<saf:form name="test1" id="test1" action="/ajax/Test1.action" theme="ajax">
    Test 1 Form
<saf:submit theme="ajax" resultDivId="result2"/>
</saf:form>

<saf:div id="result2" theme="ajax" >test2 - before</saf:div>

<saf:div id="result3" theme="ajax">test3 - before</saf:div>
</body>
</html>
