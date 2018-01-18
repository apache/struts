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
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Non UI Tags Example - Debug</title>
</head>

<body>
<div class="page-header">
	<h1>Debug Tag Usage</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<p/>
				This page shows a simple example of using the debug tag.  <br/>
				Just add <tt style="font-size: 12px; font-weight:bold;color: blue;">&lt;s:debug /&gt;</tt> to your JSP page
			    and you will see the debug link.
		    <p/>

			<p>
			    Just click on the Debug label to see the Struts ValueStack Debug information.
		    <p/>
		    <s:debug />
		</div>
	</div>
</div>
</body>
</html>