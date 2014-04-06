<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/WEB-INF/ajax/commonInclude.jsp"/>
</head>

<body>

<s:url var="ajaxTest" value="/AjaxTest.action" />

<sx:div
        id="fiveseconds"
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}"
        delay="1000"
        updateFreq="5000"
        errorText="There was an error"
        loadingText="reloading"
        showLoadingText="true">loading now</sx:div>

<s:include value="../footer.jsp"/>

</body>
</html>
