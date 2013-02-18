package com.opensymphony.xwork2.util.fs;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Represents file resource revision, used for file://* resources
 */
public class FileRevision extends Revision {

    private File file;
    private long lastModified;

    public static Revision build(URL fileUrl) {
        File file;
        try {
            if (fileUrl != null) {
                file = new File(fileUrl.toURI());
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            file = new File(fileUrl.getPath());
        }  catch (Throwable t) {
            return null;
        }
        if (file.exists() && file.canRead()) {
            long lastModified = file.lastModified();
            return new FileRevision(file, lastModified);
        }
        return null;
    }

    private FileRevision(File file, long lastUpdated) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        this.file = file;
        this.lastModified = lastUpdated;
    }

    public File getFile() {
        return file;
    }

    public boolean needsReloading() {
        return this.lastModified < this.file.lastModified();
    }

}
