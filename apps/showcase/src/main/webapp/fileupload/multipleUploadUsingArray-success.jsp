<%--
/*
 * $Id: pom.xml 559206 2007-07-24 21:01:18Z apetrelli $
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
<html>
<head>
<title>Insert title here</title>
</head>
<body>

<table border="1">
<s:iterator value="upload" status="stat">
<tr>
	<td>File <s:property value="%{#stat.index}" /></td>
	<td><s:property value="%{upload[#stat.index]}" /></td>
</tr>
</s:iterator>
</table>


<table border="1">
<s:iterator value="uploadFileName" status="stat">
<tr>
	<td>File Name <s:property value="%{#stat.index}" /></td>
	<td><s:property value="%{uploadFileName[#stat.index]}" /></td>
</tr>	
</s:iterator>
</table>

<table border="1">
<s:iterator value="uploadContentType" status="stat">
<tr>
	<td>Content Type <s:property value="%{#stat.index}" /></td>
	<td><s:property value="%{uploadContentType[#stat.index]}" /></td>
</tr>
</s:iterator>
</table>

</body>
</html>