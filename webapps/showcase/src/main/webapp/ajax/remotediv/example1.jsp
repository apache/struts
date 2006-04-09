<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ww" uri="/webwork" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<ww:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        delay="5000"
        loadingText="loading...">
    Initial Content</ww:div>

<ww:include value="../footer.jsp"/>

</body>
</html>
