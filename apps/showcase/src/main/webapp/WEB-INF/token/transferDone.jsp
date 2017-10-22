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
	<title>Struts2 Showcase - Token Examples - Transfer is Done</title>
</head>

<body>
<div class="page-header">
	<h1>Token Examples - Transfer is Done</h1>
</div>


<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<p>
				The transfer is done at
				<s:text name="token.transfer.time">
					<s:param value="#session.time"/>
				</s:text>

				<br/>New balance of source account:
				<s:property value="#session.balanceSource"/>
				<br/>New balance of destination account:
				<s:property value="#session.balanceDestination"/>

			<p/>

			<p>
				Try using the browser back button and submit the form again. This should result in a double post
				that Struts should intercept and handle accordingly.

			<p/>

			<p>
				For example 3 (session token) you should notice that the date/time stays the same. This interceptor
				catches that this is a double post but doens't display the double post page, but just renders the
				web page result from the first post.

			<p/>
			Click here to
			<s:url var="back" value="/token/index.html"/><s:a href="%{back}">return</s:a>.
		</div>
	</div>
</div>
</body>
</html>