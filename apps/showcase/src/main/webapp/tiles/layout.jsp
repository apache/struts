<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Show usage; Used in Header --%>
<tiles:importAttribute name="title" scope="request"/>
<html>
    <head><title><tiles:getAsString name="title"/></title></head>
<body>
    <tiles:insertAttribute name="header"/>

    <p id="body">
        <tiles:insertAttribute name="body"/>
    </p>

	<p>Notice that this is a layout made in JSP</p>
</body>
</html>
