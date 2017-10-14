package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;

/**
 * Represents jar resource revision, used for jar://* resource
 */
public class JarEntryRevision extends Revision {

    private static Logger LOG = LogManager.getLogger(JarEntryRevision.class);

    private URL jarFileURL;
    private long lastModified;

    public static Revision build(URL fileUrl, FileManager fileManager) {
        // File within a Jar
        // Find separator index of jar filename and filename within jar
        JarURLConnection conn = null;
        try {
            conn = (JarURLConnection) fileUrl.openConnection();
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
        }
        finally {
            if(null != conn) {
                try {
                    conn.getInputStream().close();
                } catch (IOException ignored) {
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
        JarURLConnection conn = null;
        long lastLastModified = lastModified;
        try {
            conn = (JarURLConnection) jarFileURL.openConnection();
            conn.setUseCaches(false);
            lastLastModified = conn.getJarEntry().getTime();
        } catch (IOException ignored) {
        }
        finally {
            if(null != conn) {
                try {
                    conn.getInputStream().close();
                } catch (IOException ignored) {
                }
            }
        }

        return lastModified < lastLastModified;
    }

}
