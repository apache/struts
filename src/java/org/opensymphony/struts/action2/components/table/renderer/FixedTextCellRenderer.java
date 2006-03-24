/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.components.table.renderer;

import com.opensymphony.webwork.components.table.WebTable;


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
