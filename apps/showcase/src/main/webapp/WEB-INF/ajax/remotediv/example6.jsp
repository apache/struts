<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/WEB-INF/ajax/commonInclude.jsp"/>
</head>

<body>

<sx:div
        id="error"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxNoUrl.jsp"
        delay="1000"
        showErrorTransportText="true"
        loadingText="reloading">loading now</sx:div>

<s:include value="../footer.jsp"/>

</body>
</html>
