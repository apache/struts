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
	<title>Struts2 Showcase - Conversion - Populate into Struts action class a Set of Address.java Object</title>
</head>
<body>
<div class="page-header">
	<h1>Conversion - Populate into Struts action class a Set of Address.java Object</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<p/>
			An example populating a Set of object (Address.java) into Struts' action (AddressAction.java)
			<p/>

			See the jsp code <s:url var="url" action="showAddressJspCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for PersonAction.java <s:url var="url" action="showAddressActionJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for Person.java <s:url var="url" action="showAddressJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>

			<p/>

				<s:form action="submitAddressesInfo" namespace="/conversion">
					<s:iterator value="%{new int[3]}" status="stat">
						<s:textfield label="%{'Address '+#stat.index}"
								     name="%{'addresses(\\'id'+#stat.index+'\\').address'}" />
					</s:iterator>
					<s:submit cssClass="btn btn-primary"/>
				</s:form>

				<%--
					The following is how its done statically
				--%>
				<%--
				<s:form action="submitAddressInfo" namespace="/conversion">
					<s:textfield label="Address 0"
								 name="addresses('id0')" />
					<s:textfield label="Address 1"
								 name="addresses('id1')" />
					<s:textfield label="Address 2"
								 name="addresses('id2')" />
					<s:submit />
				</s:form>
				--%>

		</div>
	</div>
</div>

</body>
</html>