<#assign tiles=JspTaglibs["http://tiles.apache.org/tags-tiles"]>
<@tiles.importAttribute name="title" scope="request"/>
<html>
    <head><title>Struts2 Showcase - <@tiles.getAsString name="title"/></title></head>
<body>
    <@tiles.insertAttribute name="header"/>
    <@tiles.insertAttribute name="body"/>
	<p>Notice that this is a layout made in FreeMarker</p>
</body>
</html>
