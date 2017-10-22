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
<%--
    nonFieldValidatorsExample.jsp
    
    @author tm_jee
    @version $Date$ $Id$
--%>


<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
	<title>Struts2 Showcase - Validation - Non Field Validator Example</title>
	<s:head/>
</head>
<body>

<div class="page-header">
	<h1>Non Field Validator Example</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<!-- START SNIPPET: nonFieldValidatorsExample -->
			<s:actionerror/>

			<s:form method="POST" action="submitNonFieldValidatorsExamples" namespace="/validation">
				<s:textfield name="someText" label="Some Text"/>
				<s:textfield name="someTextRetype" label="Retype Some Text"/>
				<s:textfield name="someTextRetypeAgain" label="Retype Some Text Again"/>
				<s:submit label="Submit" cssClass="btn btn-primary"/>
			</s:form>


			<!--  END SNIPPET: nonFieldValidatorsExample -->
		</div>
	</div>
</div>
</body>
</html>

