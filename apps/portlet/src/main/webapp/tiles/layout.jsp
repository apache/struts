<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<tiles:importAttribute name="title" scope="request"/>

<tiles:insertAttribute name="header"/>
<h2><tiles:insertAttribute name="title"/></h2>
<p id="body">
    <tiles:insertAttribute name="body"/>
</p>

<p>Notice that this is a layout made in JSP</p>

<tiles:insertAttribute name="footer"/>
