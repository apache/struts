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
<title>Showcase - UI Tag Example - Tree Example (Dynamic)</title>
<sx:head/>
</head>
<body>

<!-- START SNIPPET: treeExampleDynamicJsp -->

<script language="JavaScript" type="text/javascript">
    dojo.event.topic.subscribe("treeSelected", function treeNodeSelected(node) {
        dojo.io.bind({
            url: "<s:url value='/tags/ui/ajax/dynamicTreeSelectAction.action'/>&nodeId="+node.node.widgetId,
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

</body>
</html>