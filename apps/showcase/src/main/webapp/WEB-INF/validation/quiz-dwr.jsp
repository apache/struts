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
<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- START SNIPPET: dwrValidation -->

<html>
<head>
	<title>Struts2 Showcase - Validation - DWR</title>
	<s:head/>
	<s:script type='text/javascript' src='../dwr/engine.js'></s:script>
    <s:script type='text/javascript' src='../dwr/util.js'></s:script>
	<s:script type='text/javascript' src='../dwr/interface/validator.js'></s:script>
	<s:script type='text/javascript'>
        var dwrValidateReply = function(data) {
            var validationResult = '';
            for (index = 0; index < data.actionErrors.length; ++index) {
                validationResult += (data.actionErrors[index] + '. ');
            }
            for (index = 0; index < data.actionMessages.length; ++index) {
                validationResult += (data.actionMessages[index] + '. ');
            }
            if (typeof data.fieldErrors.name !== 'undefined') {
                for (index = 0; index < data.fieldErrors.name.length; ++index) {
                    validationResult += (data.fieldErrors.name[index] + '. ');
                }
            }
            if (typeof data.fieldErrors.age !== 'undefined') {
                for (index = 0; index < data.fieldErrors.age.length; ++index) {
                    validationResult += (data.fieldErrors.age[index] + '. ');
                }
            }
            if (validationResult === '') {
                $('form').submit();
            } else {
                dwr.util.setValue('validationResult', validationResult);
            }
        };
		function dwrFormValidation() {
            var postData = {};
            $('form').serializeArray().map(function (x) {
                postData[x.name] = x.value;
            });
            validator.doPost('/validation', 'quizDwr', postData, dwrValidateReply);
            return false;
        }
	</s:script>
</head>

<body>

<div class="page-header">
	<h1>DWR validation Example</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

			<s:form method="post">
				<s:textfield label="Name" name="name"/>
				<s:textfield label="Age" name="age"/>
				<s:textfield label="Favorite color" name="answer"/>
				<s:submit cssClass="btn btn-primary" onClick="return dwrFormValidation()"/>
			</s:form>
			<div id="validationResult" class="errorMessage"></div>
		</div>
	</div>
</div>
</body>
</html>

<!-- END SNIPPET: dwrValidation -->

