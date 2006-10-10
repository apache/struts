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
 * this is the base class that most renderers will be derived from.
 * It allows setting the alignment.  Subclasses should set there actuall
 * content by implementing getCellValue
 */
abstract public class AbstractCellRenderer implements CellRenderer {

    /**
     * used for horizontal cell alignmnet
     */
    protected String _alignment = null;


    public void setAlignment(String alignment) {
        _alignment = alignment;
    }

    public String getAlignment() {
        return _alignment;
    }

    /**
     * implememnts CellRenderer renderCell.  It sets the alignment.  gets the actual
     * data from getCellValue
     */
    public String renderCell(WebTable table, Object data, int row, int col) {
        if (isAligned()) {
            StringBuffer buf = new StringBuffer(256);
            buf.append("<div align='").append(_alignment).append("'>");
            buf.append(getCellValue(table, data, row, col));
            buf.append("</div>");

            return buf.toString();
        }

        return getCellValue(table, data, row, col);
    }

    protected boolean isAligned() {
        return _alignment != null;
    }

    /**
     * this is the method that subclasses need to implement to set their value.
     * they should not override renderCell unless they want to change the alignmnent
     * renderering
     */
    abstract protected String getCellValue(WebTable table, Object data, int row, int col);
}
