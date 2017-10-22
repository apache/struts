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
	<title>Struts2 Showcase - UI Tags - Optiontransferselect</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - Optiontransferselect</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:form action="lotsOfOptiontransferselectSubmit" namespace="/tags/ui" method="post">
			    <s:optiontransferselect
			        tooltip="Select Your Favourite Cartoon Characters"
			        headerKey="-1"
			        headerValue="--- Please Select ---"
			        doubleHeaderKey="-1"
			        doubleHeaderValue="--- Please Select ---"
			        emptyOption="true"
			        doubleEmptyOption="true"
			        label="Favourite Cartoon Characters"
			        leftTitle="Favourite Cartoon Characters"
			        rightTitle="Non Favourite Cartoon Characters"
			        name="favouriteCartoonCharacters"
			        list="defaultFavouriteCartoonCharacters"
			        doubleName="notFavouriteCartoonCharacters"
			        doubleList="defaultNotFavouriteCartoonCharacters" />

			    <br/>

			    <s:optiontransferselect
			        tooltip="Select Your Favourite Cars"
			        label="Favourite Cars"
			        leftTitle="Favourite Cars"
			        rightTitle="Non Favourite Cars"
			        name="favouriteCars"
			        list="defaultFavouriteCars"
			        doubleName="notFavouriteCars"
			        doubleList="defaultNotFavouriteCars" />

			    <br/>

			    <s:optiontransferselect
			        tooltip="Select Your Favourite Motorcycles"
			        headerKey="-1"
			        headerValue="--- Please Select ---"
			        doubleHeaderKey="-1"
			        doubleHeaderValue="--- Please Select ---"
			        label="Favourite Motorcycles"
			        leftTitle="Favourite Motorcycles"
			        rightTitle="Non Favourite Motorcycles"
			        name="favouriteMotorcycles"
			        list="defaultFavouriteMotorcycles"
			        doubleName="notFavouriteMotorcycles"
			        doubleList="defaultNotFavouriteMotorcycles" />

			    <br/>

			    <s:optiontransferselect
			        tooltip="Select Your Favourite Countries"
			        emptyOption="true"
			        doubleEmptyOption="true"
			        label="Favourite Countries"
			        leftTitle="Favourite Countries"
			        rightTitle="Non Favourite Countries"
			        name="favouriteCountries"
			        list="defaultFavouriteCountries"
			        doubleName="notFavouriteCountries"
			        doubleList="defaultNotFavouriteCountries"
			          />

			    <br/>

			    <s:optiontransferselect
			        tooltip="Pick One at a Time"
			        label="Favourite Sport"
			        leftTitle="Non Favourite Sports"
			        rightTitle="Favourite Sports"
			        name="nonFavouriteSports"
			        list="defaultNonFavoriteSports"
			        doubleName="favouriteSports"
			        doubleList="defaultFavouriteSports"
			        size="1" multiple="false"
			        doubleSize="5" doubleMultiple="true"
			        allowAddAllToLeft="false"
			        allowAddAllToRight="false"
			        allowSelectAll="false"
			          />
			    <br/>

			    <s:submit value="Submit It" />
			</s:form>
		</div>
	</div>
</div>
</body>
</html>