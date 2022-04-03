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
	<title>Struts2 Showcase - Conversion - Populate Object into Struts' action List</title>
</head>
<body>
<div class="page-header">
	<h1>Conversion - Populate Object into Struts' action List</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">


			<p/>
				An example populating a list of object (Person.java) into Struts' action (PersonAction.java)
			<p/>

			See the jsp code <s:url var="url" action="showPersonJspCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for PersonAction.java <s:url var="url" action="showPersonActionJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for Person.java <s:url var="url" action="showPersonJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>

			<p/>

			<s:actionerror cssClass="alert alert-error"/>
			<s:fielderror cssClass="alert alert-error"/>

			<s:form action="submitPersonInfo" namespace="/conversion" method="post">
			    <%--
			        The following is done Dynamically
			    --%>
			    <s:iterator value="new int[3]" status="stat">
			        <s:textfield    label="%{'Person '+#stat.index+' Name'}"
			                        name="%{'persons['+#stat.index+'].name'}" />
			        <s:textfield    label="%{'Person '+#stat.index+' Age'}"
			                        name="%{'persons['+#stat.index+'].age'}" />
			    </s:iterator>



			    <%--
			    The following is done statically:-
			    --%>
			    <%--
			    <s:textfield    label="Person 1 Name"
			                    name="persons[0].name" />
			    <s:textfield    label="Person 1 Age"
			                    name="persons[0].age" />
			    <s:textfield    label="Person 2 Name"
			                    name="persons[1].name" />
			    <s:textfield    label="Person 2 Age"
			                    name="persons[1].age" />
			    <s:textfield    label="Person 3 Name"
			                    name="persons[2].name" />
			    <s:textfield    label="Person 3 Age"
			                    name="persons[2].age" />
			    --%>

			    <s:submit cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>
</body>
</html>