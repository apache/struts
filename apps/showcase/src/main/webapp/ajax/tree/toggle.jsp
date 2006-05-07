<%@ taglib prefix="saf" uri="/struts-action" %>
<%@include file="partialChunkHeader.jsp"%>
<%
    response.setContentType("text/javascript");
%>
dojo.event.topic.publish("children_<saf:property value="category.id"/>");
var d = document.getElementById("children_<saf:property value="category.id"/>");
if (d.style.display != "none") {
    d.style.display = "none";
} else {
    d.style.display = "";
}
