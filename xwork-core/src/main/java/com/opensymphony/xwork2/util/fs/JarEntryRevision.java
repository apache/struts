package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Represents jar resource revision, used for jar://* resource
 */
public class JarEntryRevision extends Revision {

    private static Logger LOG = LoggerFactory.getLogger(JarEntryRevision.class);

    private static final String JAR_FILE_NAME_SEPARATOR = "!/";
    private static final String JAR_FILE_EXTENSION_END = ".jar/";

    private String jarFileName;
    private String fileNameInJar;
    private long lastModified;

    public static Revision build(URL fileUrl, FileManager fileManager) {
        // File within a Jar
        // Find separator index of jar filename and filename within jar
        String jarFileName = "";
        try {
            String fileName = fileUrl.toString();
            int separatorIndex = fileName.indexOf(JAR_FILE_NAME_SEPARATOR);
            if (separatorIndex == -1) {
                separatorIndex = fileName.lastIndexOf(JAR_FILE_EXTENSION_END);
            }
            if (separatorIndex == -1) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not find end of jar file!");
                }
                return null;
            }
            // Split file name
            jarFileName = fileName.substring(0, separatorIndex);
            int index = separatorIndex + JAR_FILE_NAME_SEPARATOR.length();
            String fileNameInJar = fileName.substring(index).replaceAll("%20", " ");

            URL url = fileManager.normalizeToFileProtocol(fileUrl);
            if (url != null) {
                JarFile jarFile = new JarFile(FileUtils.toFile(url));
                ZipEntry entry = jarFile.getEntry(fileNameInJar);
                return new JarEntryRevision(jarFileName, fileNameInJar, entry.getTime());
            } else {
                return null;
            }
        } catch (Throwable e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not create JarEntryRevision for [#0]!", e, jarFileName);
            }
            return null;
        }
    }

    private JarEntryRevision(String jarFileName, String fileNameInJar, long lastModified) {
        if ((jarFileName == null) || (fileNameInJar == null)) {
            throw new IllegalArgumentException("JarFileName and FileNameInJar cannot be null");
        }
        this.jarFileName = jarFileName;
        this.fileNameInJar = fileNameInJar;
        this.lastModified = lastModified;
    }

    public boolean needsReloading() {
        ZipEntry entry;
        try {
            JarFile jarFile = new JarFile(this.jarFileName);
            entry = jarFile.getEntry(this.fileNameInJar);
        } catch (IOException e) {
            entry = null;
        }

        return entry != null && (lastModified < entry.getTime());
    }

}
