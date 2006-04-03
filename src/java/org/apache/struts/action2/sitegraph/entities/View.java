/*
 * Created on Aug 13, 2004 by mgreer
 */
package org.apache.struts.action2.sitegraph.entities;

import java.util.Set;

/**
 * TODO Describe View
 */
public interface View {
    public static final int TYPE_JSP = 0;
    public static final int TYPE_VM = 1;
    public static final int TYPE_FTL = 2;

    /**
     * Name of view file
     *
     * @return The name of the view file.
     */
    public String getName();

    /**
     * Returns Set of Commands linked to by this view
     *
     * @return a set of Targets
     */
    public Set getTargets();
}
