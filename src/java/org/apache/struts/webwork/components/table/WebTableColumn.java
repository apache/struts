/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.components.table;

import org.apache.struts.webwork.components.table.renderer.CellRenderer;
import org.apache.struts.webwork.components.table.renderer.DefaultCellRenderer;


/**
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class WebTableColumn {

    static final private CellRenderer DEFAULT_RENDERER = new DefaultCellRenderer();


    CellRenderer _renderer = null;
    String _displayName = null;
    String _name = null;
    boolean _hidden = false;
    int _offset = -1;


    public WebTableColumn(String name, int offset) {
        _name = name;
        _offset = offset;
        _displayName = name;
        _renderer = DEFAULT_RENDERER;
    }


    public void setDisplayName(String displayName) {
        _displayName = displayName;
    }

    public String getDisplayName() {
        return (_displayName);
    }

    public void setHidden(boolean hidden) {
        _hidden = hidden;
    }

    public boolean isHidden() {
        return _hidden;
    }

    public String getName() {
        return (_name);
    }

    public int getOffset() {
        return (_offset);
    }

    public void setRenderer(CellRenderer renderer) {
        _renderer = renderer;
    }

    public CellRenderer getRenderer() {
        return (_renderer);
    }

    public boolean isVisible() {
        return !isHidden();
    }
}
