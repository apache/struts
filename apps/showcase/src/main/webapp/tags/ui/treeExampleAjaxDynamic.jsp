<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
<title>Showcase - UI Tag Example - Tree Example (Dynamic)</title>
<sx:head />
</head>
<body>


<s:url var="nodesUrl" namespace="/nodecorate" action="getNodes" />
<div style="float:left; margin-right: 50px;">
    <sx:tree id="tree" href="%{#nodesUrl}" />
</div>

</body>
</html>