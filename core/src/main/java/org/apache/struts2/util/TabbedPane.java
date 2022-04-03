/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.util;

import java.util.Vector;

/**
 * A bean that helps implement a tabbed pane
 *
 * FIXME: use it remove
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
