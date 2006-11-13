<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<s:head theme="ajax"/>
</head>

<body>
<s:form name="test1" id="test1" action="/ajax/Test1.action" theme="ajax">
    Test 1 Form
<s:submit theme="ajax" targets="result2"/>
</s:form>

<s:div id="result2" theme="ajax" >test2 - before</s:div>

<s:div id="result3" theme="ajax">test3 - before</s:div>
</body>
</html>
