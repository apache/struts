<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>


<body>
<s:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action">
    Initial Content</s:div>

<s:include value="../footer.jsp"/>

</body>
</html>
