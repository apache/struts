/*
 * $Id: Action.java 502296 2007-02-01 17:33:39Z niallp $
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

function clearErrorMessages(form) {
	// clear out any rows with an "errorFor" attribute
	var divs = form.getElementsByTagName("div");
    var paragraphsToDelete = new Array();

    for(var i = 0; i < divs.length; i++) {
        var p = divs[i];
        if (p.getAttribute("errorFor")) {
            paragraphsToDelete.push(p);
        }
    }

    // now delete the paragraphsToDelete
    for (var i = 0; i < paragraphsToDelete.length; i++) {
        var r = paragraphsToDelete[i];
        var parent = r.parentNode;
        parent.removeChild(r);
    }
}

function clearErrorLabels(form) {
    // set all labels back to the normal class
    var labels = form.getElementsByTagName("label");
    for (var i = 0; i < labels.length; i++) {
        var label = labels[i];
        if (label) {
            if(label.getAttribute("class") == "errorLabel"){
                label.setAttribute("class", "label");//standard way.. works for ie mozilla
                label.setAttribute("className", "label"); //ie hack cause ie does not support setAttribute
            }
        }
    }

}

function addError(e, errorText) {
    try {
        var ctrlDiv = e.parentNode; // wwctrl_ div or span
        var enclosingDiv = ctrlDiv.parentNode; // wwgrp_ div

		if (!ctrlDiv || (ctrlDiv.nodeName != "DIV" && ctrlDiv.nodeName != "SPAN") || !enclosingDiv || enclosingDiv.nodeName != "DIV") {
			alert("do not validate:" + e.id);
			return;
		}
		
        var label = enclosingDiv.getElementsByTagName("label")[0];
		if (label) {
	        label.setAttribute("class", "errorLabel"); //standard way.. works for ie mozilla
	        label.setAttribute("className", "errorLabel"); //ie hack cause ie does not support setAttribute
	    }

		var firstDiv = enclosingDiv.getElementsByTagName("div")[0]; // either wwctrl_ or wwlbl_
		if (!firstDiv) {
			firstDiv = enclosingDiv.getElementsByTagName("span")[0];
		}
        var error = document.createTextNode(errorText);
        var errorDiv = document.createElement("div");

        errorDiv.setAttribute("class", "errorMessage");//standard way.. works for ie mozilla
        errorDiv.setAttribute("className", "errorMessage");//ie hack cause ie does not support setAttribute
        errorDiv.setAttribute("errorFor", e.id);;
        errorDiv.appendChild(error);
        enclosingDiv.insertBefore(errorDiv, firstDiv);
    } catch (e) {
        alert(e);
    }
}


