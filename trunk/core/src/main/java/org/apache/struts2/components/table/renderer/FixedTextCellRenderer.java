/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.components.table.renderer;

import org.apache.struts2.components.table.WebTable;


/**
 * usefull if a column has an embeded ID number needed for a link but you want it to
 * say something else.
 */
public class FixedTextCellRenderer extends AbstractCellRenderer {

    /**
     * this is the text that will be shown in the column
     */
    protected String _text = "";


    public String getCellValue(WebTable table, Object data, int row, int col) {
        return _text;
    }

    public void setText(String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }
}
