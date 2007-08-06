<%--
/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
--%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<html>
<head>
<title>Showcase - UI Tag Example - Tree Example (Static)</title>
<sx:head />
</head>
<body>

<!-- START SNIPPET: treeExampleStaticJsp -->

<script language="JavaScript" type="text/javascript">
    dojo.event.topic.subscribe("treeSelected", function treeNodeSelected(node) {
        dojo.io.bind({
            url: "<s:url value='/tags/ui/ajax/staticTreeSelectAction.action'/>&nodeId="+node.node.title,
            load: function(type, data, evt) {
                var divDisplay = dojo.byId("displayIt");
                divDisplay.innerHTML=data;
            },
            mimeType: "text/html"
        });
    });
</script>


<div style="float:left; margin-right: 50px;">
<sx:tree label="parent" id="parentId" templateCssPath="/struts/tree.css" 
showRootGrid="true" showGrid="true" treeSelectedTopic="treeSelected">
    <sx:treenode label="child1" id="child1Id">
        <sx:treenode label="grandchild1" id="grandchild1Id"/>
        <sx:treenode label="grandchild2" id="grandchild2Id"/>
        <sx:treenode label="grandchild3" id="grandchild3Id"/>
    </sx:treenode>
    <sx:treenode label="child2" id="child2Id"/>
    <sx:treenode label="child3" id="child3Id"/>
    <sx:treenode label="child4" id="child4Id"/>
    <sx:treenode label="child5" id="child5Id">
        <sx:treenode label="gChild1" id="gChild1Id"/>
        <sx:treenode label="gChild2" id="gChild2Id"/>
    </sx:treenode>
</sx:tree>
</div>


<div id="displayIt">
Please click on any node on the tree.
</div>

<!-- END SNIPPET: treeExampleStaticJsp  -->

</body>
</html>