package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;

/**
 * Represents jar resource revision, used for jar://* resource
 */
public class JarEntryRevision extends Revision {

    private static Logger LOG = LoggerFactory.getLogger(JarEntryRevision.class);

    private URL jarFileURL;
    private long lastModified;

    public static Revision build(URL fileUrl, FileManager fileManager) {
        StrutsJarURLConnection conn = null;
        try {
            conn = StrutsJarURLConnection.openConnection(fileUrl);
            conn.setUseCaches(false);
            URL url = fileManager.normalizeToFileProtocol(fileUrl);
            if (url != null) {
                return new JarEntryRevision(fileUrl, conn.getJarEntry().getTime());
            } else {
                return null;
            }
        } catch (Throwable e) {
            LOG.warn("Could not create JarEntryRevision for [{}]!", fileUrl, e);
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                    // Cleanup failed
                }
            }
        }
    }

    private JarEntryRevision(URL jarFileURL, long lastModified) {
        if (jarFileURL == null) {
            throw new IllegalArgumentException("jarFileURL cannot be null");
        }
        this.jarFileURL = jarFileURL;
        this.lastModified = lastModified;
    }

    public boolean needsReloading() {
        long lastLastModified = lastModified;
        StrutsJarURLConnection conn = null;
        try {
            conn = StrutsJarURLConnection.openConnection(jarFileURL);
            conn.setUseCaches(false);
            lastLastModified = conn.getJarEntry().getTime();
        } catch (Throwable e) {
            LOG.warn("Could not check if needsReloading for [{}]!", jarFileURL, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                    // Cleanup failed
                }
            }
        }

        return lastModified < lastLastModified;
    }

}
