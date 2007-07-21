<#assign tiles=JspTaglibs["http://tiles.apache.org/tags-tiles"]>
<@tiles.importAttribute name="title" scope="request"/>
<html>
    <head><title><@tiles.getAsString name="title"/></title></head>
<body>
    <@tiles.insertAttribute name="header"/>
    <p id="body">
        <@tiles.insertAttribute name="body"/>
    </p>
	<p>Notice that this is a layout made in FreeMarker</p>
</body>
</html>
