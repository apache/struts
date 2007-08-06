<%--
/*
 * $Id: pom.xml 560697 2007-07-29 08:58:03Z apetrelli $
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
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<html>
<head>
    <title>View Sources</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>
<body>
<h1>View Sources</h1>

<sx:tabbedpanel id="test">
	<sx:div id="one" label="Page" >
        <h3>${empty page ? "Unknown page" : page}</h3>
<pre>
<s:iterator value="pageLines" status="row">
${row.count}: <s:property/></s:iterator>
</pre>
    </sx:div>
    <sx:div id="two" label="Configuration" >
        <h3>${empty config ? "Unknown configuration" : config}</h3>
<pre>

<s:iterator value="configLines" status="row"><s:if test="%{(#row.count-1)==(configLines.size()/2)}">
<span style="background-color:yellow">${configLine - padding + row.count - 1}: <s:property/></span></s:if><s:else>
${configLine - padding + row.count - 1}: <s:property/></s:else></s:iterator>
</pre>
    </sx:div>
    <sx:div id="three" label="Java Action">
        <h3>${empty className ? "Unknown or unavailable Action class" : className}</h3>
<pre>
<s:iterator value="classLines" status="row">
${row.count}: <s:property/></s:iterator>
</pre>
    </sx:div>
    
</sx:tabbedpanel>

</body>
</html>
