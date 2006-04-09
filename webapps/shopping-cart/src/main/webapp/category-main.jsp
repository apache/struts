<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%@ taglib prefix="saf" uri="/struts-action" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Category</title>

</head>

<body id="page-home">
<%--<saf:action name="activeCategory" namespace="/catalog/remote" executeResult="true" />--%>
<saf:div id="main" href="/catalog/remote/activeCategory.action" theme="ajax" listenTopics="categorySelected" loadingText="loading..." />
</body>
</html>
