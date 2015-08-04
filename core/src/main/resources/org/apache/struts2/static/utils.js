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

var StrutsUtils = {};

// gets an object with validation errors from string returned by 
// the ajaxValidation interceptor
StrutsUtils.getValidationErrors = function(data) {
    if (typeof data === "object") {
        return data;
    }
    else {
        if (data.indexOf("/* {") === 0) {
            return JSON.parse( data.substring(2, data.length - 2) + " )");
        } else {
            return null;
        }
    }
};

StrutsUtils.clearValidationErrors = function(form) {
    var firstNode = StrutsUtils.firstElement(form);
    var xhtml = firstNode.tagName.toLowerCase() === "table";

    if(xhtml) {
        clearErrorMessagesXHTML(form);
        clearErrorLabelsXHTML(form);
    } else {
        clearErrorMessagesCSS(form);
        clearErrorLabelsCSS(form);
    }

    //clear previous global error messages
	if(StrutsUtils.errorLists[form] && StrutsUtils.errorLists[form] !== null) {
		form.parentNode.removeChild(StrutsUtils.errorLists[form]);
		StrutsUtils.errorLists[form] = null;
	}

};

StrutsUtils.errorLists = [];

// shows validation errors using functions from xhtml/validation.js
// or css_xhtml/validation.js
StrutsUtils.showValidationErrors = function(form, errors) {
    StrutsUtils.clearValidationErrors(form, errors);

	if (errors.errors) {
		var l, errorList = document.createElement("ul");

		errorList.setAttribute("class", "errorMessage");
		errorList.setAttribute("className", "errorMessage"); // ie hack cause ie does not support setAttribute

		for ( l = 0; l < errors.errors.length; l++) {
			var item = document.createElement("li");
			var itemText = document.createTextNode(errors.errors[l]);
			item.appendChild(itemText);

			errorList.appendChild(item);
		}
		
		form.parentNode.insertBefore(errorList, form);
		StrutsUtils.errorLists[form] = errorList;
	}

  var i, fieldName, firstNode = StrutsUtils.firstElement(form);
  var xhtml = firstNode.tagName.toLowerCase() === "table";
  if(errors.fieldErrors) {
    for(fieldName in errors.fieldErrors) {
      if(errors.fieldErrors.hasOwnProperty(fieldName)) {
        for(i = 0; i < errors.fieldErrors[fieldName].length; i++) {
          if(xhtml) {
            addErrorXHTML(form.elements[fieldName], errors.fieldErrors[fieldName][i]);
          } else {
            addErrorCSS(form.elements[fieldName], errors.fieldErrors[fieldName][i]);
          }
        }
      }
    }
  }
};

StrutsUtils.firstElement  = function(parentNode, tagName) {
  var node = parentNode.firstChild;
  while(node && node.nodeType !== 1){
    node = node.nextSibling;
  }
  if(tagName && node && node.tagName && node.tagName.toLowerCase() !== tagName.toLowerCase()) {
    node = StrutsUtils.nextElement(node, tagName);
  }
  return node;  
};

StrutsUtils.nextElement = function(node, tagName) {
  if(!node) { return null; }
  do {
    node = node.nextSibling;
  } while(node && node.nodeType !== 1);

  if(node && tagName && tagName.toLowerCase() !== node.tagName.toLowerCase()) {
    return StrutsUtils.nextElement(node, tagName);
  }
  return node;  
};

StrutsUtils.previousElement = function(node, tagName) {
  if(!node) { return null; }
  if(tagName) { tagName = tagName.toLowerCase(); }
  do {
    node = node.previousSibling;
  } while(node && node.nodeType !== 1);
  
  if(node && tagName && tagName.toLowerCase() !== node.tagName.toLowerCase()) {
    return StrutsUtils.previousElement(node, tagName);
  }
  return node;  
};

StrutsUtils.addOnLoad = function(func) {
  var oldonload = window.onload;
  if (typeof window.onload !== 'function') {
    window.onload = func;
  } else {
    window.onload = function() {
      oldonload();
      func();
    };
  }
};

StrutsUtils.addEventListener = function(element, name, observer, capture) {
  if (element.addEventListener) {
    element.addEventListener(name, observer, false);
  } else if (element.attachEvent) {
    element.attachEvent('on' + name, observer);
  }
};
