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
	<title>Struts2 Showcase - UI Tags - More Select Box UI Examples - Result</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - More Select Box UI Examples - Result</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">


			<table>
			    <tr>
			        <td>Prioritised Favourite Cartoon Characters:</td>
			        <td>
			            <s:iterator value="prioritisedFavouriteCartoonCharacters" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			    <tr>
			        <td>Prioritised Favourite Cars:</td>
			        <td>
			            <s:iterator value="prioritisedFavouriteCars" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			    <tr>
			        <td>Prioritised Favourite Countries</td>
			        <td>
			            <s:iterator value="prioritisedFavouriteCountries" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			        <tr>
			        <td>Favourite Cities</td>
			        <td>
			            <s:iterator value="favouriteCities" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			        <tr>
			        <td>Favourite Numbers</td>
			        <td>
			            <s:iterator value="favouriteNumbers" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
		    </table>
		</div>
	</div>
</div>
</body>
</html>
