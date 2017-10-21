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
	<title>Struts2 Showcase - UI Tags Example</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags Example</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:actionerror cssClass="alert alert-error"/>
			<s:actionmessage cssClass="alert alert-info"/>
			<s:fielderror  cssClass="alert alert-error"/>


			<s:form action="exampleSubmit" enctype="multipart/form-data" javascriptTooltip="true">
			    <s:textfield
			            label="Name"
			            name="name"
			            tooltip="Enter your Name here"/>

			    <s:textfield
			            tooltip="Select Your Birthday"
			            label="Birthday"
			            name="birthday" />

			    <s:textfield
			            tooltip="Enter the time you wake up"
			            label="Wake up time"
			            name="wakeup"
			            type="time"/>

			    <s:textarea
			            tooltip="Enter your Biography"
			            label="Biography"
			            name="bio"
			            cols="20"
			            rows="3"/>

			    <s:select
			            tooltip="Choose Your Favourite Color"
			            label="Favorite Color"
			            list="{'Red', 'Blue', 'Green'}"
			            name="favouriteColor"
			            emptyOption="true"
			            headerKey="None"
			            headerValue="None"/>

			    <s:select
			            tooltip="Choose Your Favourite Language"
			            label="Favourite Language"
			            list="favouriteLanguages"
			            name="favouriteLanguage"
			            listKey="key"
			            listValue="description"
			            listCssStyle="style"
			            emptyOption="true"
			            headerKey="None"
			            headerValue="None"/>

			    <s:checkboxlist
			            tooltip="Choose your Friends"
			            label="Friends"
			            list="{'Wes', 'Patrick', 'Jason', 'Jay', 'Toby', 'Rene'}"
			            name="friends"/>

			    <s:radio
			            tooltip="Choose your Best Friend"
			            label="Best Friend"
			            list="{'Wes', 'Patrick', 'Jason', 'Jay', 'Toby', 'Rene'}"
			            name="bestFriend"
			            cssErrorClass="foo" />

			    <s:checkbox
			            tooltip="Confirmed that your are Over 18"
			            label="Age 18+"
			            name="legalAge"/>

			    <s:doubleselect
			            tooltip="Choose Your State"
			            label="State"
			            name="region" list="{'North', 'South'}"
			            value="'South'"
			            doubleValue="'Florida'"
			            doubleList="top == 'North' ? {'Oregon', 'Washington'} : {'Texas', 'Florida'}"
			            doubleName="state"
			            headerKey="-1"
			            headerValue="---------- Please Select ----------"
			            emptyOption="true" />

			    <s:doubleselect
			            tooltip="Choose your Vehical"
			            label="Favourite Vehical"
			            name="favouriteVehicalType"
			            list="vehicalTypeList"
			            listKey="key"
			            listValue="description"
			            value="'MotorcycleKey'"
			            doubleValue="'YamahaKey'"
			            doubleList="vehicalSpecificList"
			            doubleListKey="key"
			            doubleListValue="description"
			            doubleName="favouriteVehicalSpecific" headerKey="-1"
			            headerValue="---------- Please Select ----------"
			            emptyOption="true" />

			    <s:file
			            tooltip="Upload Your Picture"
			            label="Picture"
			            name="picture" />

			    <s:optiontransferselect
			            tooltip="Select Your Favourite Cartoon Characters"
			            label="Favourite Cartoons Characters"
			            name="leftSideCartoonCharacters"
			            leftTitle="Left Title"
			            rightTitle="Right Title"
			            list="{'Popeye', 'He-Man', 'Spiderman'}"
			            multiple="true"
			            headerKey="headerKey"
			            headerValue="--- Please Select ---"
			            emptyOption="true"
			            doubleList="{'Superman', 'Mickey Mouse', 'Donald Duck'}"
			            doubleName="rightSideCartoonCharacters"
			            doubleHeaderKey="doubleHeaderKey"
			            doubleHeaderValue="--- Please Select ---"
			            doubleEmptyOption="true"
			            doubleMultiple="true" />

			    <s:textarea
			            label="Your Thougths"
			            name="thoughts"
			            tooltip="Enter your thoughts here" />

			    <s:submit cssClass="btn btn-primary"/>
			    <s:reset cssClass="btn btn-danger" onclick="alert('Resetting form now... Press OK to continue!');" />
			</s:form>
		</div>
	</div>
</div>
</body>
</html>
