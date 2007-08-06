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
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Tabbed Panes</title>
    <%@ include file="/ajax/commonInclude.jsp" %>
</head>

<body>

<h2>Examples</h2>

<p>
    <ol>
        <li><a href="example2.jsp">A local tabbed panel width fixed size (doLayout="true")</a></li>
        <li><a href="example4.jsp">A Local tabbed panel with disabled tabs</a></li>
        <li><a href="example6.jsp">A Local tabbed panel that publishes topics when tabs are selected(before and after)</a></li>
        <li><a href="example3.jsp">A remote (href != "") and local tabbed panel</a></li>
        <li><a href="example1.jsp">Various remote and local tabbed panels (with enclosed tabbed pannels) with layout (doLayout="false")</a></li>
        <li><a href="example5.jsp">A local tabbed panel width fixed size (doLayout="true") with close button on the tab pane (closable="true" on tabs), and tabs on the bottom (labelposition="bottom")</a></li>
    </ol>


</p>

<s:include value="../footer.jsp"/>

</body>
</html>
