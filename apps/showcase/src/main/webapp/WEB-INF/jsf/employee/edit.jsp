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
		<title>Struts2 Showcase - JSF Integration - Modify Employee</title>
		<s:head/>
	</head>

	<body>

	<div class="page-header">
		<h1>Modify Employee</h1>
	</div>

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">

				<h:form>
					<h:inputHidden value="#{action.currentEmployee.empId}"/>
					<h:panelGrid columns="3">
						<h:outputText value="Employee Id:"/>
						<h:inputText id="id" size="5" value="#{action.currentEmployee.empId}" required="true"/>
						<h:message for="id"/>

						<h:outputText value="First Name:"/>
						<h:inputText id="firstName" size="30" value="#{action.currentEmployee.firstName}"
						             required="true">
							<f:validateLength minimum="2" maximum="30"/>
						</h:inputText>
						<h:message for="firstName"/>

						<h:outputText value="Last Name:"/>
						<h:inputText id="lastName" size="30" value="#{action.currentEmployee.lastName}" required="true">
							<f:validateLength minimum="2" maximum="30"/>
						</h:inputText>
						<h:message for="lastName"/>

						<h:outputText value="Salary:"/>
						<h:inputText id="salary" size="10" value="#{action.currentEmployee.salary}"/>
						<h:message for="salary"/>

						<h:outputText value="Married:"/>
						<h:selectBooleanCheckbox id="married" value="#{action.currentEmployee.married}"/>
						<h:message for="married"/>

						<h:outputText value="Position:"/>
						<h:selectOneMenu id="position" value="#{action.currentEmployee.position}">
							<f:selectItems value="#{action.availablePositionsAsMap}"/>
						</h:selectOneMenu>
						<h:message for="position"/>

						<h:outputText value="Main Skill:"/>
						<h:selectOneMenu id="mainSkill" value="#{action.currentEmployee.mainSkill.name}">
							<f:selectItems value="#{action.availableSkills}"/>
						</h:selectOneMenu>
						<h:message for="mainSkill"/>

						<h:outputText value="Other Skills:"/>
						<h:selectManyListbox id="otherSkills" value="#{action.selectedSkills}">
							<f:selectItems value="#{action.availableSkills}"/>
						</h:selectManyListbox>
						<h:message for="otherSkills"/>

						<h:outputText value="Password:"/>
						<h:inputSecret id="password" value="#{action.currentEmployee.password}"/>
						<h:message for="password"/>

						<h:outputText value="Level:"/>
						<h:selectOneRadio id="level" value="#{action.currentEmployee.level}">
							<f:selectItems value="#{action.availableLevelsAsMap}"/>
						</h:selectOneRadio>
						<h:message for="level"/>

						<h:outputText value="Comment:"/>
						<h:inputTextarea id="comment" value="#{action.currentEmployee.comment}" cols="50" rows="3"/>
						<h:message for="comment"/>
					</h:panelGrid>

					<h:commandButton value="Save" action="#{action.save}" styleClass="btn btn-primary"/>
					<br/><br/>
					<h:outputLink value="list.action" styleClass="btn btn-info">
						<h:outputText value="Back"/>
					</h:outputLink>
				</h:form>
			</div>
		</div>
	</div>
	</body>
	</html>
</f:view>