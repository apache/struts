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
	<title>Struts2 Showcase - Non Ui Tag - AppendIterator Tag</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - AppendIterator Tag Demo</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:generator var="iterator1" separator="," val="%{iteratorValue1}" />
		    <s:generator var="iterator2" separator="," val="%{iteratorValue2}" />

		    <s:append var="appendedIterator">
		        <s:param value="%{#attr.iterator1}" />
		        <s:param value="%{#attr.iterator2}" />
		    </s:append>

		    <s:iterator value="#appendedIterator">
		        <s:property /><br/>
		    </s:iterator>

			<s:url var="url" action="showAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" />
			<s:a href="%{#url}" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back To Input</s:a>
		</div>
	</div>
</div>
</body>
</html>
