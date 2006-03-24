package com.opensymphony.webwork.plexus;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */
public class PlexusUtils {
    private static final Log log = LogFactory.getLog(PlexusObjectFactory.class);

    public static void configure(PlexusContainer pc, String file) throws PlexusConfigurationResourceException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (is == null) {
            log.info("Could not find " + file + ", skipping");
            is = new ByteArrayInputStream("<plexus><components></components></plexus>".getBytes());
        }
        pc.setConfigurationResource(new InputStreamReader(is));
    }
}
