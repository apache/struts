
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<saf:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        updateFreq="0"
        delay="0">
    Initial Content ... should not change</saf:div>

<saf:include value="../footer.jsp"/>

</body>
</html>


