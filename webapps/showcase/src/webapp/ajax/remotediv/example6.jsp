<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ww" uri="/webwork" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<ww:div
        id="error"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxNoUrl.jsp"
        theme="ajax"
        delay="1000"
        showErrorTransportText="true"
        loadingText="reloading">loading now</ww:div>

<ww:include value="../footer.jsp"/>

</body>
</html>
