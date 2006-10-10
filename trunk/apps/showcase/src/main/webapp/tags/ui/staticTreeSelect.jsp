<%@taglib prefix="s" uri="/struts-tags" %>

<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>


<%--
<script>
    var widget = dojo.widget.byId("parentId");
    alert(widget.selectedNode);
    if (widget.selectedNode != null) {
        var inputElement = dojo.byId('sId');
        inputElement.value='true';
        alert(inputElement+'\t'+inputElement.value);
    }
    else {
        var inputElement = dojo.byId('sId');
        inputElement.value='false';
        alert(inputElement+'\t'+inputElement.value);
    }
</script>
--%>

<%=request.getParameter("nodeId") %>
