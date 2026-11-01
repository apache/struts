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
 *     data-struts-combobox-target-id   id of the text field to copy the value into
 *     data-struts-header-key           value of the header option, which is not copied
 *     data-struts-empty-option         "true" when the empty option is rendered, which is not copied
 * The field is resolved by id, not by name, so two comboboxes that share a name in one form each drive their own
 * text field, and a same-named hidden input cannot capture the write. The listener is registered once per page, in
 * the capture phase, so it also covers comboboxes rendered later and is not stopped by an ancestor's stopPropagation().
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
        var targetId = select.getAttribute('data-struts-combobox-target-id');
        if (targetId === null || select.selectedIndex < 0) {
            return;
        }
        var value = select.options[select.selectedIndex].value;
        if (select.hasAttribute('data-struts-header-key') && value === select.getAttribute('data-struts-header-key')) {
            return;
        }
        if (select.getAttribute('data-struts-empty-option') === 'true' && value === '') {
            return;
        }
        var field = document.getElementById(targetId);
        if (field) {
            field.value = value;
        }
    }, true);
})();
