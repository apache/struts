<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>


<body>
<s:url id="ajaxTest" value="/AjaxTest.action" />


<s:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}"
        updateFreq="2000"
        indicator="indicator"
		>
    Initial Content</s:div>
<img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>
<s:include value="../footer.jsp"/>

</body>
</html>
