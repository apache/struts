<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/WEB-INF/ajax/commonInclude.jsp"/>
</head>

<s:url var="ajaxTest" value="/AjaxTest.action" />


<body>
<sx:div
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}">
    Initial Content</sx:div>

<s:include value="../footer.jsp"/>

</body>
</html>
