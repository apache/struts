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

function moveSelectedOptions(objSourceElement, objTargetElement, toSort, notMove1, notMove2) {
    var test1 = compile(notMove1);
    var test2 = compile(notMove2);
    moveOptions(objSourceElement, objTargetElement, toSort,
        function (opt) {
            return (opt.selected && !test1(opt.value) && !test2(opt.value));
        }
    );
}

function moveAllOptions(objSourceElement, objTargetElement, toSort, notMove1, notMove2) {
    var test1 = compile(notMove1);
    var test2 = compile(notMove2);
    moveOptions(objSourceElement, objTargetElement, toSort,
        function (opt) {
            return (!test1(opt.value) && !test2(opt.value));
        }
    );
}

function compile(ptn) {
    if (ptn !== undefined) {
        if (ptn === '' || !window.RegExp) {
            return function (val) {
                return val === ptn;
            };
        } else {
            var reg = new RegExp("^" + ptn + "$");
            return function (val) {
                if (val === '') { // ignore empty option added by template
                    return true;
                }
                return reg.test(val);
            };
        }
    }
    return function (val) {
        return false;
    };
}

function moveOptions(objSourceElement, objTargetElement, toSort, chooseFunc) {
    var aryTempSourceOptions = [];
    var aryTempTargetOptions = [];
    var x = 0;

    //looping through source element to find selected options
    for (var i = 0; i < objSourceElement.length; i++) {
        if (chooseFunc(objSourceElement.options[i])) {
            //need to move this option to target element
            var intTargetLen = objTargetElement.length++;
            objTargetElement.options[intTargetLen].text = objSourceElement.options[i].text;
            objTargetElement.options[intTargetLen].value = objSourceElement.options[i].value;
        }
        else {
            //storing options that stay to recreate select element
            var objTempValues = {};
            objTempValues.text = objSourceElement.options[i].text;
            objTempValues.value = objSourceElement.options[i].value;
            aryTempSourceOptions[x] = objTempValues;
            x++;
        }
    }

    //sorting and refilling target list
    for (var j = 0; j < objTargetElement.length; j++) {
        var oTempValues = {};
        oTempValues.text = objTargetElement.options[j].text;
        oTempValues.value = objTargetElement.options[j].value;
        aryTempTargetOptions[j] = oTempValues;
    }

    if (toSort) {
        aryTempTargetOptions.sort(sortByText);
    }

    for (var k = 0; k < objTargetElement.length; k++) {
        objTargetElement.options[k].text = aryTempTargetOptions[k].text;
        objTargetElement.options[k].value = aryTempTargetOptions[k].value;
        objTargetElement.options[k].selected = false;
    }

    //resetting length of source
    objSourceElement.length = aryTempSourceOptions.length;
    //looping through temp array to recreate source select element
    for (var l = 0; l < aryTempSourceOptions.length; l++) {
        objSourceElement.options[l].text = aryTempSourceOptions[l].text;
        objSourceElement.options[l].value = aryTempSourceOptions[l].value;
        objSourceElement.options[l].selected = false;
    }
}

function sortByText(a, b) {
    if (a.text < b.text) {
        return -1;
    }
    if (a.text > b.text) {
        return 1;
    }
    return 0;
}

function selectAllOptionsExceptSome(objTargetElement, type, ptn) {
    var test = compile(ptn);
    for (var i = 0; i < objTargetElement.length; i++) {
        var opt = objTargetElement.options[i];
        if ((type === 'key' && !test(opt.value)) ||
            (type === 'text' && !test(opt.text))) {
            opt.selected = true;
        } else {
            opt.selected = false;
        }
    }
    return false;
}

function selectAllOptions(objTargetElement) {
    for (var i = 0; i < objTargetElement.length; i++) {
        if (objTargetElement.options[i].value !== '') {
            objTargetElement.options[i].selected = true;
        }
    }
    return false;
}

function moveOptionUp(objTargetElement, type, ptn) {
    var test = compile(ptn);
    for (var i = 0; i < objTargetElement.length; i++) {
        if (objTargetElement[i].selected) {
            var v;
            if (i !== 0 && !objTargetElement[i - 1].selected) {
                if (type === 'key') {
                    v = objTargetElement[i - 1].value;
                }
                else {
                    v = objTargetElement[i - 1].text;
                }
                if (!test(v)) {
                    swapOptions(objTargetElement, i, i - 1);
                }
            }
        }
    }
}

function moveOptionDown(objTargetElement, type, ptn) {
    var test = compile(ptn);
    for (i = (objTargetElement.length - 1); i >= 0; i--) {
        if (objTargetElement[i].selected) {
            var v;
            if ((i !== (objTargetElement.length - 1)) && !objTargetElement[i + 1].selected) {
                if (type === 'key') {
                    v = objTargetElement[i].value;
                }
                else {
                    v = objTargetElement[i].text;
                }
                if (!test(v)) {
                    swapOptions(objTargetElement, i, i + 1);
                }
            }
        }
    }
}

function swapOptions(objTargetElement, first, second) {
    var opt = objTargetElement.options;
    var temp = new Option(opt[first].text, opt[first].value, opt[first].defaultSelected, opt[first].selected);
    var temp2 = new Option(opt[second].text, opt[second].value, opt[second].defaultSelected, opt[second].selected);
    opt[first] = temp2;
    opt[second] = temp;
}

/*
 * Buttons rendered by the optiontransferselect and updownselect tags carry their action, target select ids and header
 * keys in data-* attributes instead of inline onclick handlers, so the tags work under a Content-Security-Policy
 * without 'unsafe-inline':
 *   data-struts-updownselect="moveUp|moveDown|selectAll", data-struts-target, optional data-struts-header-key
 *   data-struts-optiontransferselect="leftUp|leftDown|rightUp|rightDown|addToLeft|addToRight|addAllToLeft|
 *     addAllToRight|selectAll", data-struts-left, data-struts-right, optional data-struts-header-key and
 *     data-struts-double-header-key
 * The listener is registered once per page, in the capture phase, so the framework action still runs before a
 * developer-supplied onclick attribute on the same button and is not stopped by an ancestor's stopPropagation().
 */
(function () {
    if (window.strutsOptionTransferSelectListener) {
        return;
    }
    window.strutsOptionTransferSelectListener = true;

    function headerKey(button, name) {
        return button.hasAttribute(name) ? button.getAttribute(name) : undefined;
    }

    function selectAll(target, key) {
        if (key === undefined) {
            selectAllOptions(target);
        } else {
            selectAllOptionsExceptSome(target, 'key', key);
        }
    }

    function transfer(move, source, target, key) {
        if (key === undefined) {
            move(source, target, false, '');
        } else {
            move(source, target, false, key, '');
        }
    }

    function updownselect(button, action) {
        var target = document.getElementById(button.getAttribute('data-struts-target'));
        var key = headerKey(button, 'data-struts-header-key');
        if (action === 'moveUp') {
            moveOptionUp(target, 'key', key === undefined ? '' : key);
        } else if (action === 'moveDown') {
            moveOptionDown(target, 'key', key === undefined ? '' : key);
        } else if (action === 'selectAll') {
            selectAll(target, key);
        }
    }

    function optiontransferselect(button, action) {
        var left = document.getElementById(button.getAttribute('data-struts-left'));
        var right = document.getElementById(button.getAttribute('data-struts-right'));
        var leftKey = headerKey(button, 'data-struts-header-key');
        var rightKey = headerKey(button, 'data-struts-double-header-key');
        if (action === 'leftDown') {
            moveOptionDown(left, 'key', leftKey === undefined ? '' : leftKey);
        } else if (action === 'leftUp') {
            moveOptionUp(left, 'key', leftKey === undefined ? '' : leftKey);
        } else if (action === 'rightDown') {
            moveOptionDown(right, 'key', rightKey === undefined ? '' : rightKey);
        } else if (action === 'rightUp') {
            moveOptionUp(right, 'key', rightKey === undefined ? '' : rightKey);
        } else if (action === 'addToLeft') {
            transfer(moveSelectedOptions, right, left, rightKey);
        } else if (action === 'addToRight') {
            transfer(moveSelectedOptions, left, right, leftKey);
        } else if (action === 'addAllToLeft') {
            transfer(moveAllOptions, right, left, rightKey);
        } else if (action === 'addAllToRight') {
            transfer(moveAllOptions, left, right, leftKey);
        } else if (action === 'selectAll') {
            selectAll(left, leftKey);
            selectAll(right, rightKey);
        }
    }

    document.addEventListener('click', function (event) {
        var button = event.target;
        if (!button || !button.getAttribute) {
            return;
        }
        var action = button.getAttribute('data-struts-updownselect');
        if (action) {
            updownselect(button, action);
            return;
        }
        action = button.getAttribute('data-struts-optiontransferselect');
        if (action) {
            optiontransferselect(button, action);
        }
    }, true);
})();
