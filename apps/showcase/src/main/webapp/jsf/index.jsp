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

 $Id: welcome.jsp 371852 2006-01-24 07:25:10Z craigmcc $

--%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:view>

<html>

  <head>
    <title>JSF Integration Examples</title>
  </head>

  <body>
  	<h2>JSF Integration Examples</h2>
  	<p>
  	The following pages show how Struts Action 2 and JSF components can work together,
  	each doing what they do best.
  	</p>
  	
  	<h3>Employee List</h3>
  	
  	<h:dataTable value="#{action.employees}" var="e">
  		<h:column>
  			<f:facet name="header">
  				<h:outputText value="ID" />
  			</f:facet>
  			<h:outputLink value="employee.action">
  				<f:param name="id" value="#{e.id}" />
  				<h:outputText value="#{e.id}" />
  			</h:outputLink>	
  		</h:column>
		<h:column>
  			<f:facet name="header">
  				<h:outputText value="Name" />
  			</f:facet>
  			<h:outputText value="#{e.name}" />
  		</h:column>
  		<h:column>
  			<f:facet name="header">
  				<h:outputText value="Skills" />
  			</f:facet>
  			<h:outputText value="#{e.skills}" />
  		</h:column>
  	</h:dataTable>	
  </body>

</html>

</f:view>
