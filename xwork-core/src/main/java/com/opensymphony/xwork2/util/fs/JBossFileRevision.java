package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.util.URLUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Represents file resource revision, used for vfszip://* resources
 */
class JBossFileRevision extends FileRevision {

    public JBossFileRevision(File file, long lastUpdated) {
        super(file, lastUpdated);
    }

    public static Revision build(URL fileUrl) {
        File file;
        URL url = URLUtil.normalizeToFileProtocol(fileUrl);
        try {
            if (url != null) {
                file = new File(url.toURI());
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        if (file.exists() && file.canRead()) {
            long lastModified = file.lastModified();
            return new FileRevision(file, lastModified);
        }
        return null;
    }
}
