<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Show usage; Used in Header --%>
<tiles:importAttribute name="title" scope="request"/>
<html>
    <head><title>Struts2 Showcase - <tiles:getAsString name="title"/></title></head>
<body>
    <tiles:insertAttribute name="header"/>
    <tiles:insertAttribute name="body"/>
    <p>Notice that this is a layout made in JSP</p>
    <p>It is configured with <strong>annotations!</strong></p>
</body>
</html>
