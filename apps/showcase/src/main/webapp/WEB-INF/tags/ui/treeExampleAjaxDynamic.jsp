<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags - Tree Example AJAX (Dynamic)</title>
	<sx:head />
</head>
<body>
<div class="page-header">
	<h1>UI Tags - Tree Example AJAX (Dynamic)</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<s:url var="nodesUrl" namespace="/nodecorate" action="getNodes" />
			<div style="float:left; margin-right: 50px;">
			    <sx:tree id="tree" href="%{#nodesUrl}" />
			</div>
		</div>
	</div>
</div>
</body>
</html>