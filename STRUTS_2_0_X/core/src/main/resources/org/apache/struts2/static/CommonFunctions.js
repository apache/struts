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
 *      Methods for the tabbed component
 */
var unselectedClass = "tab_default tab_unselected";
var unselectedContentsClass = "tab_contents_hidden";
var unselectedOverClass = "tab_default tab_unselected tab_unselected_over";
var selectedClass = "tab_default tab_selected";
var selectedContentsClass = "tab_contents_header";

function mouseIn(tab) {
    var className = tab.className;
    if (className.indexOf('unselected') > -1) {
        className = unselectedOverClass;
        tab.className = className;
    }
}

function mouseOut(tab) {
    var className = tab.className;
    if (className.indexOf('unselected') > -1) {
        className = unselectedClass;
        tab.className = className;
    }
}

/**
 * Checks whether the current form include an ajax-ified submit button, if so
 * we return true (otherwise false).
 *
 * @param form the HTML form element to check
 */
function isAjaxFormSubmit( form ) {
    // we check whether this exists
    //      <INPUT type="submit" dojoattachevent="onClick: execute" dojoattachpoint="attachBtn" />
    var thisForm = document.getElementById(form.id);
    var matchUrl = /\s+dojoAttachPoint/;
    if( thisForm.innerHTML.match(matchUrl) ) {
        return false;
    }
    for( i=0; i<thisForm.elements.length; i++ ) {
        var field = thisForm.elements[i];
        if( field.type.toLowerCase()=='submit' ) {
            if( field.hasAttribute("dojoAttachPoint") && field.getAttribute("dojoAttachPoint")=="attachBtn" ) {
                return false;
            }
        }
    }
    return true;
}

/**  end tabbed component functions ******************************************************************/
