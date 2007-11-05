/*
 * $Id$
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
    clearErrorMessagesCSS(form);
}

function clearErrorMessagesCSS(form) {
    firstFieldErrorPosition = null;
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
    clearErrorLabelsCSS(form);
}

function clearErrorLabelsCSS(form) {
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
    addErrorCSS(e, errorText);
}

var firstFieldErrorPosition = null;
function addErrorCSS(e, errorText) {
    try {
        if (!e)
            return; //ignore errors for fields that are not in the form
        var elem = (e.type ? e : e[0]); //certain input types return node list, while we single first node. I.e. set of radio buttons.
        var enclosingDiv = findWWGrpNode(elem); // find wwgrp div/span

        //try to focus on first field
        var fieldPos = findFieldPosition(elem);
        if (fieldPos != null && (firstFieldErrorPosition == null || firstFieldErrorPosition > fieldPos)) {
            firstFieldErrorPosition = fieldPos;
        }

        if (!enclosingDiv) {
            alert("Could not validate: " + e.id);
            return;
        }
        
        var label = enclosingDiv.getElementsByTagName("label")[0];
        if (label) {
            label.setAttribute("class", "errorLabel"); //standard way.. works for ie mozilla
            label.setAttribute("className", "errorLabel"); //ie hack cause ie does not support setAttribute
        }

        var firstCtrNode = findWWCtrlNode(enclosingDiv); // either wwctrl_ or wwlbl_
        
        var error = document.createTextNode(errorText);
        var errorDiv = document.createElement("div");

        errorDiv.setAttribute("class", "errorMessage");//standard way.. works for ie mozilla
        errorDiv.setAttribute("className", "errorMessage");//ie hack cause ie does not support setAttribute
        errorDiv.setAttribute("errorFor", elem.id);
        errorDiv.appendChild(error);
        enclosingDiv.insertBefore(errorDiv, firstCtrNode);
    } catch (err) {
        alert("An exception occurred: " + err.name + ". Error message: " + err.message);
    }
}

function findWWGrpNode(elem) {
    while (elem.parentNode) {
        elem = elem.parentNode;

        if (elem.className && elem.className.match(/wwgrp/))
            return elem;
    }
    return null;
}

function findWWCtrlNode(enclosingDiv) {
    for(var elem in enclosingDiv.getElementsByTagName("div")) {
        if (elem.className && elem.className.match(/(wwlbl|wwctrl)/))
            return elem
    }
    for(var elem in enclosingDiv.getElementsByTagName("span")) {
        if (elem.className && elem.className.match(/(wwlbl|wwctrl)/))
            return elem
    }
    return enclosingDiv.getElementsByTagName("span")[0];
}

//find field position in the form
function findFieldPosition(elem) {
    if (!elem.form) {
        alert("no form for " + elem);
    }
    
    var form = elem.form;
    for (i = 0; i < form.elements.length; i++) { 
        if (form.elements[i].name == elem.name) {
            return i;
        }
    }
    return null;
}

//focus first element
var StrutsUtils_showValidationErrors = StrutsUtils.showValidationErrors;
StrutsUtils.showValidationErrors = function(form, errors) {
    StrutsUtils_showValidationErrors(form, errors);
    if (firstFieldErrorPosition != null && form.elements[firstFieldErrorPosition].focus) {
        form.elements[firstFieldErrorPosition].focus();
    }
}