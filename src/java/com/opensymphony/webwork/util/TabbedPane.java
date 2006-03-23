/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import java.util.Vector;


/**
 * A bean that helps implement a tabbed pane
 *
 * @author Onyeje Bose (digi9ten@yahoo.com)
 * @author Rickard Öberg (rickard@middleware-company.com)
 * @version $Revision: 1.4 $
 */
public class TabbedPane {

    protected String tabAlign = null;

    // Attributes ----------------------------------------------------
    protected Vector content = null;
    protected int selectedIndex = 0;


    // Public --------------------------------------------------------
    public TabbedPane(int defaultIndex) {
        selectedIndex = defaultIndex;
    }


    public void setContent(Vector content) {
        this.content = content;
    }

    public Vector getContent() {
        return content;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setTabAlign(String tabAlign) {
        this.tabAlign = tabAlign;
    }

    public String getTabAlign() {
        return tabAlign;
    }
}
