<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <title>Ajax Widgets</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<br/>
NOTES:
<ul>
    <li>Make sure that there is a 'value' attribute in the textarea with the content for the editor</li>
    <li>This is experimental</li>
</ul>


Default Editor configuration:<br/>
<s:form id="form1" action="AjaxRemoteForm" method="post">
    <s:textarea name="data" theme="ajax" cols="50" rows="10" value="Test Data 1" />
    <s:submit value="Submit"/>
</s:form>
<br/>

Configured Editor configuration:<br/>
<s:form id="form2" action="AjaxRemoteForm" method="post">
    <s:textarea id="editor2" name="data" theme="ajax" cols="50" rows="10" value="Test Data 2">
        <s:param name="editorControls">textGroup;|;justifyGroup;|;listGroup;|;indentGroup</s:param>
    </s:textarea>
    <s:submit value="Submit"/>
</s:form>
<br/>

<s:include value="../footer.jsp"/>

</body>
</html>
