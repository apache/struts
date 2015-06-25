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

function addOption(objTextBox, objTargetElement) {
    var value = objTextBox.value;
    if(value != null && value != '') {
        objTargetElement.options[objTargetElement.options.length] = new Option(value, value);
        objTextBox.value = '';
    }
}

function removeOptions(objTargetElement) {
    var i = 0;
    while(objTargetElement.options.length > i) {
        if (objTargetElement.options[i].selected) {
            objTargetElement.options.remove(i);
        } else {
            i++;
        }
    }
}

function removeAllOptions(objTargetElement) {
    while(objTargetElement.options.length != 0) {
        objTargetElement.options[0] = null;
    }
}

function selectAllOptionsExceptSome(objTargetElement, type, ptn) {
    var test = compile(ptn);
    for (var i = 0; i < objTargetElement.length; i++) {
        var opt = objTargetElement.options[i];
        if ((type == 'key' && !test(opt.value)) ||
              (type == 'text' && !test(opt.text))) {
            opt.selected = true;
        } else {
            opt.selected = false;
        }    
    }
    return false;
}

function compile(ptn) {
    if (ptn != undefined) {
    	if (ptn == '' || !window.RegExp) {
            return function(val) { return val == ptn; }
        } else {
            var reg = new RegExp("^" + ptn + "$");
            return function (val) {
                if (val == '') { // ignore empty option added by template
                	return true;
                }
            	return reg.test(val); }
        }
    }
    return function(val) { return false; }
}

function selectAllOptions(objTargetElement) {
    for (var i = 0; i < objTargetElement.length; i++) {
        if (objTargetElement.options[i].value != '') {
            objTargetElement.options[i].selected = true;    
        }    
    }
    return false;
}

function moveOptionUp(objTargetElement, type, ptn) {
	var test = compile(ptn);
	for (i=0; i<objTargetElement.length; i++) {
		if (objTargetElement[i].selected) {
			var v;
			if (i != 0 && !objTargetElement[i-1].selected) {
		    	if (type == 'key') {
		    		v = objTargetElement[i-1].value
		    	}
		    	else {
		    		v = objTargetElement[i-1].text;
		    	}
				if (!test(v)) {
					swapOptions(objTargetElement,i,i-1);
				}
		    }
		}
	}
}

function moveOptionDown(objTargetElement, type, ptn) {
	var test = compile(ptn);
	for (i=(objTargetElement.length-1); i>= 0; i--) {
		if (objTargetElement[i].selected) {
			var v;
			if ((i != (objTargetElement.length-1)) && !objTargetElement[i+1].selected) {
		    	if (type == 'key') {
		    		v = objTargetElement[i].value
		    	}
		    	else {
		    		v = objTargetElement[i].text;
		    	}
				if (!test(v)) {
					swapOptions(objTargetElement,i,i+1);
				}
		    }
		}
	}
}

function swapOptions(objTargetElement, first, second) {
	var opt = objTargetElement.options;
	var temp = new Option(opt[first].text, opt[first].value, opt[first].defaultSelected, opt[first].selected);
	var temp2= new Option(opt[second].text, opt[second].value, opt[second].defaultSelected, opt[second].selected);
	opt[first] = temp2;
	opt[second] = temp;
}
