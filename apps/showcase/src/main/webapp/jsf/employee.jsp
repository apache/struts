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
  
  	<h2>Modify Employee</h2>
  	
  	<h:form>
  		<h:inputHidden value="#{action.employee.id}" />
  		<h:panelGrid columns="2">
  			<h:outputText value="ID:" />
  			<h:outputText value="#{action.employee.id}" />
  			<h:outputText value="Name:" />
  			<h:inputText size="30" value="#{action.employee.name}" />
  			<h:outputText value="Skills:" />
  			<h:selectManyCheckbox value="#{action.employee.skills}">
  				<f:selectItem itemValue="Java" itemLabel="Java" />
				<f:selectItem itemValue="PHP" itemLabel="PHP" />
				<f:selectItem itemValue="Ruby" itemLabel="Ruby" />
  			</h:selectManyCheckbox>	
  		</h:panelGrid>
  		
  		<h:commandButton value="Submit" action="#{action.save}" />
  		<br /><br />
  		<h:outputLink value="index.action">
  			<h:outputText value="Back" />
  		</h:outputLink>
  	</h:form>
  </body>

</html>

</f:view>
