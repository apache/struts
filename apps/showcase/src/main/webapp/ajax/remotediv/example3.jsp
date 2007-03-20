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
        id="twoseconds"
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}"
        theme="ajax"
        delay="2000"
        updateFreq="%{#parameters.period}"
        errorText="There was an error">Initial Content</s:div>

<s:include value="../footer.jsp"/>

</body>
</html>
