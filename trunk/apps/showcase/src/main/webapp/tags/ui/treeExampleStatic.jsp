<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<title>Showcase - UI Tag Example - Tree Example (Static)</title>
<s:head theme="ajax" debug="true"  />
</head>
<body>

<!-- START SNIPPET: treeExampleStaticJsp -->

<script>
    function treeNodeSelected(nodeId) {
        dojo.io.bind({
            url: "<s:url value='/tags/ui/ajax/staticTreeSelectAction.action'/>?nodeId="+nodeId,
            load: function(type, data, evt) {
                var divDisplay = dojo.byId("displayIt");
                divDisplay.innerHTML=data;
            },
            mimeType: "text/html"
        });
    };

    dojo.event.topic.subscribe("treeSelected", this, "treeNodeSelected");
</script>


<div style="float:left; margin-right: 50px;">
<s:tree label="parent" id="parentId" theme="ajax" templateCssPath="/struts/tree.css" 
showRootGrid="true" showGrid="true" treeSelectedTopic="treeSelected">
    <s:treenode theme="ajax" label="child1" id="child1Id">
        <s:treenode theme="ajax" label="grandchild1" id="grandchild1Id"/>
        <s:treenode theme="ajax" label="grandchild2" id="grandchild2Id"/>
        <s:treenode theme="ajax" label="grandchild3" id="grandchild3Id"/>
    </s:treenode>
    <s:treenode theme="ajax" label="child2" id="child2Id"/>
    <s:treenode theme="ajax" label="child3" id="child3Id"/>
    <s:treenode theme="ajax" label="child4" id="child4Id"/>
    <s:treenode theme="ajax" label="child5" id="child5Id">
        <s:treenode theme="ajax" label="gChild1" id="gChild1Id"/>
        <s:treenode theme="ajax" label="gChild2" id="gChild2Id"/>
    </s:treenode>
</s:tree>
</div>


<div id="displayIt">
Please click on any node on the tree.
</div>

<!-- END SNIPPET: treeExampleStaticJsp  -->

</body>
</html>