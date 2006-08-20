<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/plain;charset=UTF-8" language="java" %>
Category set to <s:property value="categoryId"/>
