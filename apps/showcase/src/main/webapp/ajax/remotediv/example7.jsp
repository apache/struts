<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<s:url var="test3" value="/Test3.action" />


<sx:div
        id="error"
        cssStyle="border: 1px solid yellow;"
        href="%{test3}"
        delay="1000"
		executeScripts="true"
        loadingText="reloading">loading now</sx:div>

<s:include value="../footer.jsp"/>

</body>
</html>
