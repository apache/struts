/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.components.table;

import org.apache.struts2.components.table.renderer.CellRenderer;
import org.apache.struts2.components.table.renderer.DefaultCellRenderer;


/**
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
