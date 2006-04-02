/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.components.table.renderer;

import org.apache.struts.webwork.components.table.WebTable;

/**
 * @author $author$
 * @version $Revision: 1.2 $
 */
public interface CellRenderer {

    public String renderCell(WebTable table, Object data, int row, int col);
}
