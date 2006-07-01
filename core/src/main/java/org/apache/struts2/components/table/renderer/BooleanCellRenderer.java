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
 */
public class BooleanCellRenderer extends AbstractCellRenderer {

    /**
     * value used if the boolean object is false
     */
    protected String _falseValue = "false";

    /**
     * value used if the boolean object is true
     */
    protected String _trueValue = "true";


    public BooleanCellRenderer() {
        super();
    }


    public String getCellValue(WebTable table, Object data, int row, int col) {
        if (data == null) {
            return "";
        }

        if (data instanceof Boolean) {
            return ((Boolean) data).booleanValue() ? _trueValue : _falseValue;
        }

        return data.toString(); //if here then not a boolean
    }

    public void setFalseValue(String falseValue) {
        _falseValue = falseValue;
    }

    public void setTrueValue(String trueValue) {
        _trueValue = trueValue;
    }
}
