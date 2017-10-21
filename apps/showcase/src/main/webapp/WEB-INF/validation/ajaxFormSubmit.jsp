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
    <title>Struts2 Showcase - Validation - AJAX Form Submit</title>
    <s:head theme="xhtml"/>

    <style type="text/css">
        /* see comment of script element below! */
        .ajaxVisualFeedback {
            width: 16px;
            height: 16px;
            background-image: url('../images/indicator.gif');
            background-repeat: no-repeat;
            float: right;
        }
    </style>

</head>
<body>

<div class="page-header">
    <h1>AJAX Form Submit</h1>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">

            <!-- START SNIPPET: ajaxFormSubmit -->

            <h3>Action Errors Will Appear Here</h3>
            <s:actionerror theme="ajaxErrorContainers"/>

            <hr/>

            <s:form method="POST" theme="xhtml">
                <s:textfield label="Required Validator Field" name="requiredValidatorField" theme="ajaxErrorContainers"/>
                <s:textfield label="Required String Validator Field" name="requiredStringValidatorField" theme="ajaxErrorContainers"/>
                <s:textfield label="Integer Validator Field" name="integerValidatorField" theme="ajaxErrorContainers"/>
                <s:textfield label="Date Validator Field" name="dateValidatorField" theme="ajaxErrorContainers"/>
                <s:textfield label="Email Validator Field" name="emailValidatorField" theme="ajaxErrorContainers"/>
                <s:textfield label="URL Validator Field" name="urlValidatorField" theme="ajaxErrorContainers"/>
                <s:textfield label="String Length Validator Field" name="stringLengthValidatorField" theme="ajaxErrorContainers"/>
                <s:textfield label="Regex Validator Field" name="regexValidatorField" theme="ajaxErrorContainers"/>
                <s:textfield label="Field Expression Validator Field" name="fieldExpressionValidatorField" theme="ajaxErrorContainers"/>
                <s:submit label="Submit" cssClass="btn btn-primary"/>
            </s:form>

            <!-- END SNIPPET: ajaxFormSubmit -->
        </div>
    </div>
</div>

<script type="text/javascript">
/********************************************************************
 * JS just used on this page.
 * Usually this would be placed in a JS file
 * but as this showcase app is already hard to follow
 * I place it here so it is easier to find.
 *
 * note: this requires jQuery.
 *******************************************************************/

 /**
  * Validates form per AJAX. To be called as onSubmit handler.
  *
  * @param event onSubmit event
  */
function ajaxFormValidation(event) {
    event.preventDefault();
    _removeValidationErrors();

    var _form = $(event.target);
    var _formData = _form.serialize(true);

    // prepare visual feedback
    // you may want to use other elements here
    var originalButton = _form.find('.btn-primary');
    // note: jQuery returns an array-like object
    if (originalButton && originalButton.length && originalButton.length > 0) {
        originalButton.hide();
        var feedbackElement = $('<div class="ajaxVisualFeedback"></div>').insertAfter(originalButton);
        var restoreFunction = function() {
            originalButton.show();
            feedbackElement.remove();
        }
    }


    var options = {
        data: 'struts.enableJSONValidation=true&struts.validateOnly=false&' + _formData,
        async: true,
        processData: false,
        type: 'POST',
        success: function (response, statusText, xhr) {
            if (response.location) {
                // no validation errors
                // action has been executed and sent a redirect URL wrapped as JSON
                // cannot use a normal http-redirect (status-code 3xx) as this would be followed by browsers and would not be available here

                // follow JSON-redirect
                window.location.href = response.location;
            } else {
                if (restoreFunction) {
                    restoreFunction();
                }
                _handleValidationResult(_form, response);
            }
        },
        error: function(xhr, textStatus, errorThrown) {
            if (restoreFunction) {
                restoreFunction();
            }
            // struts sends status code 400 when validation errors are present
            if (xhr.status === 400) {
                _handleValidationResult(_form, JSON.parse(xhr.responseText))
            } else {
                // a real error occurred -> show user an error message
                _handleValidationResult(_form, {errors: ['Network or server error!']})
            }
        }
    }

    // send request, after delay to make sure everybody notices the visual feedback :)
    window.setTimeout(function() {
        var url = _form[0].action;
        jQuery.ajax(url, options);
    }, 1000);
}

/**
 * Removes validation errors from HTML DOM.
 */
function _removeValidationErrors() {
    // action errors
    // you might want to use a custom ID here
    $('ul.errorMessage li').remove();

    // field errors
    $('div.errorMessage').remove();
}

/**
 * Incorporates validation errors in HTML DOM.
 *
 * @param form Form containing errors.
 * @param errors Errors from server.
 */
function _handleValidationResult(form, errors) {
    // action errors
    if (errors.errors) {
        // you might want to use a custom ID here
        var errorContainer = $('ul.errorMessage');
        $.each(errors.errors, function(index, errorMsg) {
            var li = $('<li><span></span></li>');
            li.text(errorMsg); // use text() for security reasons
            errorContainer.append(li);
        });
    }

    // field errors
    if (errors.fieldErrors) {
        $.each(errors.fieldErrors, function(fieldName, errorMsg) {
            var td = $('td[data-error-for-fieldname="' + fieldName + '"]');
            if (td) {
                var div = $('<div class="errorMessage"></div>');
                div.text(errorMsg); // use text() for security reasons
                td.append(div);
            }
        });
    }
}

// register onSubmit handler
$(window).bind('load', function() {
    $('form').bind('submit', ajaxFormValidation);
});
</script>
</body>
</html>
