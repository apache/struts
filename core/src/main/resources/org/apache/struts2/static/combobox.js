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

/*
 * Copies the selected option of a combobox select into its text field. The select carries its settings in data-*
 * attributes:
 *     data-struts-combobox-target   name of the text field in the same form
 *     data-struts-header-key        value of the header option, which is not copied
 *     data-struts-empty-option      "true" when the empty option is rendered, which is not copied
 * The listener is registered once per page, in the capture phase, so it also covers comboboxes rendered later and
 * is not stopped by an ancestor's stopPropagation().
 */
(function () {
    if (window.strutsComboboxListener) {
        return;
    }
    window.strutsComboboxListener = true;

    document.addEventListener('change', function (event) {
        var select = event.target;
        if (!select || !select.getAttribute) {
            return;
        }
        var target = select.getAttribute('data-struts-combobox-target');
        if (target === null || !select.form || select.selectedIndex < 0) {
            return;
        }
        var value = select.options[select.selectedIndex].value;
        if (select.hasAttribute('data-struts-header-key') && value === select.getAttribute('data-struts-header-key')) {
            return;
        }
        if (select.getAttribute('data-struts-empty-option') === 'true' && value === '') {
            return;
        }
        var field = select.form.elements[target];
        if (field && typeof field.length === 'number' && field.tagName === undefined) {
            // Two controls in the form share the name, so elements[target] is a RadioNodeList rather than the
            // text field; assigning value to it would not reach the input. Resolve the first text input in it.
            var resolved = null;
            for (var i = 0; i < field.length; i++) {
                if (field[i].tagName === 'INPUT') {
                    resolved = field[i];
                    break;
                }
            }
            field = resolved;
        }
        if (field) {
            field.value = value;
        }
    }, true);
})();
