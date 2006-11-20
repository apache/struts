<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Show usage; Used in Header --%>
<tiles:importAttribute name="title" scope="request"/>
<html>
    <head><title><tiles:getAsString name="title"/></title></head>
<body>
    <tiles:attribute name="header"/>

    <p id="body">
        <tiles:attribute name="body"/>
    </p>
</body>
</html>
