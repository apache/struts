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
	<title>Struts2 Showcase - Token Examples - Example 2</title>
</head>

<body>
<div class="page-header">
	<h1>Token Examples - Example 2</h1>
</div>


<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<p>
				<b>Example 2:</b> This example illustrates a situation where you can transfer money from
				one account to another. We use the token to prevent double posts so the transfer only
				happens once. This action will redirect after you have submitted the form.
			<p/>

			<p>
				Balance of source account: <s:property value="#session.balanceSource"/>
			<br/>Balance of destination account: <s:property value="#session.balanceDestination"/>
			<p/>

			<s:form action="transfer2">
				<s:token/>
				<s:textfield label="Amount" name="amount" required="true" value="200"/>
				<s:submit value="Transfer money" cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>
</body>
</html>
