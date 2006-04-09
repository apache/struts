<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<saf:div
        id="fiveseconds"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        delay="1000"
        updateFreq="5000"
        errorText="There was an error"
        loadingText="reloading">loading now</saf:div>

<saf:include value="../footer.jsp"/>

</body>
</html>
