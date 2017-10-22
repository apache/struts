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
	<title>Struts2 Showcase - UI Tags - More Select Box UI Examples</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - More Select Box UI Examples</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:form action="moreSelectsSubmit" namespace="/tags/ui" method="post">

		    <s:updownselect
		        tooltip="Prioritized Your Favourite Cartoon Characters"
		        label="Prioritised Favourite Cartoon Characters"
		        list="defaultFavouriteCartoonCharacters"
		        name="prioritisedFavouriteCartoonCharacters"
		        headerKey="-1"
		        headerValue="--- Please Order ---"
		        emptyOption="true"  />

		    <br/>

		    <s:updownselect
		        tooltip="Prioritise Your Favourite Cars"
		        label="Prioritised Favourite Cars"
		        list="defaultFavouriteCars"
		        name="prioritisedFavouriteCars"
		        headerKey="-10"
		        headerValue="--- Please Order ---" />

		    <br/>

		    <s:updownselect
		        tooltip="Prioritised Your Favourite Countries"
		        label="Prioritised Favourite Countries"
		        list="defaultFavouriteCountries"
		        name="prioritisedFavouriteCountries"
		        emptyOption="true"
		        value="{'england', 'brazil'}" />

		    <br/>

		    <s:inputtransferselect
		        list="defaultFavouriteNumbers"
		        name="favouriteNumbers"
		        label="Numbers"/>

		    <s:select label="Favourite Cities"
		        list="availableCities"
		        name="favouriteCities"
		        value="%{defaultFavouriteCities}"
		        multiple="true" size="4"/>

		    <s:submit value="Submit It" cssClass="btn btn-primary"/>

		    <br/>

		</s:form>
		</div>
	</div>
</div>
</body>
</html>