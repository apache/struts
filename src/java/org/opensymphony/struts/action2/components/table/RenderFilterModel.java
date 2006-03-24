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
public class RenderFilterModel extends AbstractFilterModel {

    private boolean rendered;


    public RenderFilterModel(TableModel tm) {
        super(tm);
    }


    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public boolean isRendered() {
        return rendered;
    }
}
