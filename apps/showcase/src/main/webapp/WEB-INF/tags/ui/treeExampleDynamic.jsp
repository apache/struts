<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags - Tree Example (Dynamic)</title>
	<sx:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - Tree Example (Dynamic)</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<!-- START SNIPPET: treeExampleDynamicJsp -->

			<script language="JavaScript" type="text/javascript">
			    dojo.event.topic.subscribe("treeSelected", function treeNodeSelected(node) {
			        dojo.io.bind({
			            url: "<s:url value='/tags/ui/ajax/dynamicTreeSelectAction.action'/>?nodeId="+node.node.widgetId,
			            load: function(type, data, evt) {
			                var divDisplay = dojo.byId("displayId");
			                divDisplay.innerHTML=data;
			            },
			            mimeType: "text/html"
			        });
			    });
			</script>



			<div style="float:left; margin-right: 50px;">
			<sx:tree
			    id="tree"
			    rootNode="%{treeRootNode}"
			    childCollectionProperty="children"
			    nodeIdProperty="id"
			    nodeTitleProperty="name"
			    treeSelectedTopic="treeSelected">
			</sx:tree>
			</div>

			<div id="displayId">
			Please click on any of the tree nodes.
			</div>

			<!-- END SNIPPET: treeExampleDynamicJsp -->
		</div>
	</div>
</div>
</body>
</html>