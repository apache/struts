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
    <title>View Sources</title>
</head>
<body>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">
			<h1>View Sources</h1>

			<ul class="nav nav-tabs" id="codeTab">
				<li class="active"><a href="#page">Page</a></li>
				<li><a href="#config">Configuration</a></li>
				<li><a href="#java">Java Action</a></li>
			</ul>

			<div class="tab-content">
				<div class="tab-pane active" id="page">
					<h3><s:property default="Unknown page" value="page"/></h3>
					<pre class="prettyprint lang-html linenums">
						<s:iterator value="pageLines" status="row">
<s:property/></s:iterator>
					</pre>
				</div>
				<div class="tab-pane" id="config">
					<h3><s:property default="Unknown configuration" value="config"/></h3>
					<pre class="prettyprint lang-xml linenums">
						<s:iterator value="configLines" status="row">
<s:property/></s:iterator>
					</pre>
				</div>
				<div class="tab-pane" id="java">
					<h3><s:property default="Unknown or unavailable Action class" value="className"/></h3>
					<pre class="prettyprint lang-java linenums">
						<s:iterator value="classLines" status="row">
<s:property/></s:iterator>
					</pre>
				</div>
			</div>
		</div>
	</div>
</div>


<script>
	$('#codeTab a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	})
</script>
</body>
</html>
