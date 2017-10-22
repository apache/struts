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
	<title>Struts2 Showcase - Execute and Wait Examples - Wait</title>
</head>

<body>
<div class="page-header">
	<h1>Execute and Wait Examples - Wait</h1>
</div>


<div class="container-fluid">
	<div class="row">
		<div class="col-md-12" style="text-align: center;">

			<p class="well">
				We are processing your request. Please wait.
			</p>

			<div class="progress progress-striped active">
				<div class="bar" style="width: 50%;"></div>
			</div>

			<p/>
			You can click this link to <a href="<s:url includeParams="all"/>">refresh</a>.

			<b>We have processed your request.</b>
			<p/>
			Click here to <s:url var="back" value="/wait/index.html"/><s:a href="%{back}" cssClass="btn btn-link">return</s:a>.

		</div>
	</div>
</div>
</body>
</html>

<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <meta http-equiv="refresh" content="5;url=<s:url includeParams="all"/>"/>
</head>

<body>
    <p style="border: 1px solid silver; padding: 5px; background: #ffd; text-align: center;">
        We are processing your request. Please wait.
    </p>

    <p/>
    You can click this link to <a href="<s:url includeParams="all"/>">refresh</a>.

</body>
</html>
