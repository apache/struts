<%@ taglib prefix="ww" uri="/webwork" %>

<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>

Result: <ww:property value="count"/> @ <ww:property value="serverTime"/>

The value you entered was: <ww:property value="data"/><br/>
