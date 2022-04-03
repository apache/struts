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
	<title>Struts2 Showcase - Execute and Wait Examples - Complete</title>
</head>

<body>
<div class="page-header">
	<h1>The process is complete</h1>
</div>


<div class="container-fluid">
	<div class="row">
		<div class="col-md-12" style="text-align: center;">

			<b>We have processed your request.</b>
			<p/>
			Click here to <s:url var="back" value="/wait/index.html"/><s:a href="%{back}" cssClass="btn btn-link">return</s:a>.

		</div>
	</div>
</div>
</body>
</html>
