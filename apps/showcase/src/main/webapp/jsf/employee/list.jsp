<%--

 Copyright 2006 The Apache Software Foundation.
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 $Id$

--%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:view>
	<html>
	<head>
		<title>Struts2 Showcase - JSF Integration - Available Employees</title>
		<s:head/>
	</head>

	<body>

	<div class="page-header">
		<h1>Available Employees</h1>
	</div>

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">

				<h:dataTable value="#{action.availableItems}" var="e" styleClass="table table-striped table-bordered table-hover table-condensed">
					<h:column>
						<f:facet name="header">
							<h:outputText value="Id"/>
						</f:facet>
						<h:outputLink value="edit.action">
							<f:param name="empId" value="#{e.empId}"/>
							<h:outputText value="#{e.empId}"/>
						</h:outputLink>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="First Name"/>
						</f:facet>
						<h:outputText value="#{e.firstName}"/>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Last Name"/>
						</f:facet>
						<h:outputText value="#{e.lastName}"/>
					</h:column>
				</h:dataTable>

				<p>
					<h:outputLink value="edit.action" styleClass="btn btn-primary">
						<h:outputText value="Create new Employee"/>
					</h:outputLink>
				</p>
			</div>
		</div>
	</div>
	</body>
	</html>
</f:view>