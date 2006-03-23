<%@ taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<ww:head theme="ajax"/>
</head>

<body>
<ww:form name="test1" id="test1" action="/ajax/Test1.action" theme="ajax">
    Test 1 Form
<ww:submit theme="ajax" resultDivId="result2"/>
</ww:form>

<ww:div id="result2" theme="ajax" >test2 - before</ww:div>

<ww:div id="result3" theme="ajax">test3 - before</ww:div>
</body>
</html>
