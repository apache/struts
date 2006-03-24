package com.opensymphony.webwork.plexus;

import org.codehaus.plexus.PlexusContainer;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */
public class PlexusThreadLocal {
    static ThreadLocal ptl = new ThreadLocal();

    public static void setPlexusContainer(PlexusContainer pc) {
        ptl.set(pc);
    }

    public static PlexusContainer getPlexusContainer() {
        return (PlexusContainer) ptl.get();
    }
}
