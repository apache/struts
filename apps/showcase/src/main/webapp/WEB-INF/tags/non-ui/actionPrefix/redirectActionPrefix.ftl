<#--
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
<html>
<head>
	<title>Struts2 Showcase - Non UI Tags - Action Prefix (Freemarker)</title>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - Action Prefix (Freemarker)</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<p>You have come to this page because you used an 'redirect-action' prefix.<p/>
	
			<p>Because this is a <strong>redirect-action</strong>, the text will be lost, due to a redirection
			implies a new request being issued from the client.<p/>

			The text you've enter is ${text!''?html}<p/>

			<@s.a href="javascript:history.back();" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back</@s.a>
		</div>
	</div>
</div>
</body>
</html>


