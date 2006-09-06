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
<head><title>Available Employees</title></head>

<body>
<h1>Available Employees</h1>
  	
  	<h:dataTable value="#{action.availableItems}" var="e">
  		<h:column>
  			<f:facet name="header">
  				<h:outputText value="Id" />
  			</f:facet>
  			<h:outputLink value="edit.action">
  				<f:param name="empId" value="#{e.empId}" />
  				<h:outputText value="#{e.empId}" />
  			</h:outputLink>	
  		</h:column>
		<h:column>
  			<f:facet name="header">
  				<h:outputText value="First Name" />
  			</f:facet>
  			<h:outputText value="#{e.firstName}" />
  		</h:column>
  		<h:column>
  			<f:facet name="header">
  				<h:outputText value="Last Name" />
  			</f:facet>
  			<h:outputText value="#{e.lastName}" />
  		</h:column>
  	</h:dataTable>	
  	
  	<p>
  	<h:outputLink value="edit.action">
  		<h:outputText value="Create new Employee" />
  	</h:outputLink>
  	</p>
  	
  	<h:outputLink value="../../showcase.action">
  		<h:outputText value="Back to Showcase Startpage" />
  	</h:outputLink>
  </body>

</html>

</f:view>