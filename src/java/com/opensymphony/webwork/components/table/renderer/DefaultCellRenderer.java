/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.components.table.renderer;

import com.opensymphony.webwork.components.table.WebTable;


/**
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class DefaultCellRenderer extends AbstractCellRenderer {

    public DefaultCellRenderer() {
        super();
    }


    public String getCellValue(WebTable table, Object data, int row, int col) {
        if (data != null) {
            return data.toString();
        }

        return "null";
    }
}
