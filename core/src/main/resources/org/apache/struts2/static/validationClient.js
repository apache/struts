/*
 * $Id: pom.xml 560558 2007-07-28 15:47:10Z apetrelli $
 *
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

/**
 *
 * Common code to interface with the validationServlet 
 *
 */

function ValidationClient(servletUrl) {

	this.servletUrl = servletUrl;

	this.validate = function(input, namespace, actionName) {
		var vc = this;
		var form = input.form;
		var params = new Object();
	    for (var i = 0; i < form.elements.length; i++) {
	        var e = form.elements[i];
            if (e.name != null && e.name != '') {
                params[e.name] = e.value;
            }
        }

		validator.doPost(function(action) {
            if (action) {
                vc.onErrors(input, action);
            }
        }, namespace, actionName, params);
    }
    

	// @param formObject - the form object that triggered the validate call
	// @param errors - a javascript object representing the action errors and field errors
	// client should overwrite this handler to display the new error messages
	this.onErrors = function(inputObject, errors) {
	}
	
	return this;
}
