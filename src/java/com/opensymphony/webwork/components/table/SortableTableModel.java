/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.components.table;

import javax.swing.table.TableModel;


/**
 * @author $author$
 * @version $Revision: 1.2 $
 */
public interface SortableTableModel extends TableModel {

    final static public String NONE = "NONE";
    final static public String ASC = "ASC";
    final static public String DESC = "DESC";


    public int getSortedColumnNumber();

    public String getSortedDirection(int columnNumber);

    public void sort(int columnNumber, String direction);
}
