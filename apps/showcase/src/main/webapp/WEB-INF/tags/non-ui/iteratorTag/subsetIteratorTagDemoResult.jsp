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
	<title>Struts2 Showcase - Non Ui Tag - SubsetTag Demo</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - SubsetTag Demo</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:generator var="iterator" val="%{iteratorValue}" separator="," />

		    <s:subset count="%{count}" start="%{start}" source="%{#attr.iterator}" >
		        <s:iterator>
		            <s:property /><br/>
		        </s:iterator>
		    </s:subset>

			<s:url var="url" action="showSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" />
			<s:a href="%{#url}" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back To Input</s:a>
		</div>
	</div>
</div>
</body>
</html>
