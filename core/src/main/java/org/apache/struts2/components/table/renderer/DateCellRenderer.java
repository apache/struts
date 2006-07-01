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

import java.text.SimpleDateFormat;


/**
 */
public class DateCellRenderer extends AbstractCellRenderer {

    SimpleDateFormat _formater = new SimpleDateFormat();

    /**
     * this is the string that  SimpleDateFormat needs to display the date
     *
     * @see SimpleDateFormat
     */
    String _formatString = null;


    public DateCellRenderer() {
        super();
    }


    public String getCellValue(WebTable table, Object data, int row, int col) {

        if (data == null) {
            return "";
        }

        if (data instanceof java.util.Date) {
            return _formater.format((java.util.Date) data);
        }

        return data.toString();
    }

    public void setFormatString(String format) {
        _formatString = format;
        _formater.applyPattern(_formatString);
    }
}
