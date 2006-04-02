/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.components.table;

import javax.swing.table.TableModel;


/**
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class RenderFilterModel extends AbstractFilterModel {

	private static final long serialVersionUID = -2501708467650344057L;
	
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
