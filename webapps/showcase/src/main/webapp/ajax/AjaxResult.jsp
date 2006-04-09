<%@ taglib prefix="saf" uri="/struts-action" %>

<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>

Result: <saf:property value="count"/> @ <saf:property value="serverTime"/>
