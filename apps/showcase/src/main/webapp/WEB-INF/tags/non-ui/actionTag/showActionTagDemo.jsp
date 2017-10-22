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
<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Non-Ui Tag - Action Tag </title>
</head>
<body>

<div class="page-header">
	<h1>Non-Ui Tag - Action Tag</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<div class="well">
				<h2> This is Not - Included by the Action Tag</h2>
			</div>


			<!-- lets include the first page many times -->
			<div class="well">
				<s:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
			</div>


			<!-- lets include the second page many times -->
			<div class="well">
				<s:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
			</div>


			<!--  lets include the third page many time -->
			<div class="well">
				<s:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
			</div>


			<s:url var="url" action="lookAtSource" namespace="/tags/non-ui/actionTag" />
			<s:a href="%{#url}" cssClass="btn btn-info">Source</s:a>
		</div>
	</div>
</div>
</body>
</html>

