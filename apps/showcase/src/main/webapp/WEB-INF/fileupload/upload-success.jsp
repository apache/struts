<!--
/*
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
-->
<%@ page
	language="java" 
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Fileupload sample</title>
</head>

<body>
<div class="page-header">
	<h1>Fileupload sample</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">
			<ul>
		        <li>ContentType: <s:property value="uploadContentType" /></li>
		        <li>FileName: <s:property value="uploadFileName" /></li>
		        <li>File: <s:property value="upload" /></li>
		        <li>Caption:<s:property value="caption" /></li>
	        </ul>
		</div>
	</div>
</div>

</body>
</html>

