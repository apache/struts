<%@taglib prefix="s" uri="/tags" %>

<html>
<head>
    <title>Showcase - Tags - UI Tags</title>
</head>
<body>
    <h1>UI Tags</h1>
    
    <ul>
        <li><s:url id="url" namespace="/tags/ui" action="example" method="input" /><s:a href="%{url}">UI Example</s:a></li>
        <li><s:url id="url" namespace="/tags/ui" action="exampleVelocity" method="input" /><s:a href="%{url}">UI Example (Velocity)</s:a></li>
        <li><s:url id="url" namespace="/tags/ui" action="lotsOfOptiontransferselect" method="input" /><s:a href="%{url}">Option Transfer Select UI Example</s:a></li>
        <li><s:url id="url" namespace="/tags/ui" action="lotsOfRichtexteditor" method="input" /><s:a href="%{url}">Rich Text Editor UI Example</s:a></li>
        <li><s:url id="url" namespace="/tags/ui" value="treeExampleStatic.jsp" /><s:a href="%{url}">Tree Example (static)</s:a>
        <li><s:url id="url" namespace="/tags/ui" action="showDynamicTreeAction"/><s:a href="%{url}">Tree Example (dynamic)</s:a>
        <li><s:url id="url" value="componentTagExample.jsp"/><s:a href="%{#url}">Component Tag Example</s:a>
        <%--li><s:url id="url" namespace="/tags/ui" action="populateUsingIterator" method="input" /><s:a href="%{url}">UI population using iterator tag</s:a></li--%>
    </ul>
</body>
</html>
