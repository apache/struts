/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.components.table.renderer;

import org.apache.struts.action2.components.table.WebTable;

/**
 * @author $author$
 * @version $Revision$
 */
public interface CellRenderer {

    public String renderCell(WebTable table, Object data, int row, int col);
}
