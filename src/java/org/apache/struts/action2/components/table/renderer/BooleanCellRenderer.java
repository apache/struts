/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.components.table.renderer;

import org.apache.struts.action2.components.table.WebTable;


/**
 * @author $author$
 * @version $Revision: 1.2 $
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
