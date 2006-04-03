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
