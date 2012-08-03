<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Showcase - Tags - UI Tags</title>
</head>
<body>
    <h1>UI Tags</h1>

    <ul>
        <li><s:url var="url" namespace="/tags/ui" action="example" method="input" /><s:a href="%{url}">UI Example</s:a></li>
        <li><s:url var="url" namespace="/tags/ui" action="exampleVelocity" method="input" /><s:a href="%{url}">UI Example (Velocity)</s:a></li>
        <li><s:url var="url" namespace="/tags/ui" action="lotsOfOptiontransferselect" method="input" /><s:a href="%{url}">Option Transfer Select UI Example</s:a></li>
        <li><s:url var="url" namespace="/tags/ui" action="moreSelects" method="input" /><s:a href="%{url}">More Select Box UI Examples</s:a></li>
        <li><s:url var="url" namespace="/tags/ui" value="treeExampleStatic.jsp" /><s:a href="%{url}">Tree Example (static)</s:a>
        <li><s:url var="url" namespace="/tags/ui" action="showDynamicTreeAction"/><s:a href="%{url}">Tree Example (dynamic)</s:a>
        <li><s:url var="url" namespace="/tags/ui" action="showDynamicAjaxTreeAction"/><s:a href="%{url}">Tree Example (dynamic ajax loading)</s:a>
        <li><s:url var="url" value="componentTagExample.jsp"/><s:a href="%{#url}">Component Tag Example</s:a>
        <li><s:url var="url" namespace="/tags/ui" action="actionTagExample" method="input" /><s:a href="%{url}">Action Tag Example</s:a></li>
        <li><a href="datepicker/index.jsp">DateTime picker tag - Pick a date</a></li>
        <li><a href="timepicker/index.jsp">DateTime picker tag - Pick a time</a></li>
        <%--li><s:url var="url" namespace="/tags/ui" action="populateUsingIterator" method="input" /><s:a href="%{url}">UI population using iterator tag</s:a></li--%>
    </ul>
</body>
</html>
