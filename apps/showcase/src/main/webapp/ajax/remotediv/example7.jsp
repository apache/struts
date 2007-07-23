<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<s:url id="test3" value="/Test3.action" />


<s:div
        id="error"
        cssStyle="border: 1px solid yellow;"
        href="%{test3}"
        theme="ajax"
        delay="1000"
		executeScripts="true"
        loadingText="reloading">loading now</s:div>

<s:include value="../footer.jsp"/>

</body>
</html>
