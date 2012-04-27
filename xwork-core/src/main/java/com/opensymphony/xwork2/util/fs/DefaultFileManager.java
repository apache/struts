/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.util.URLUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link FileManager}
 */
public class DefaultFileManager implements FileManager {

    private static Logger LOG = LoggerFactory.getLogger(DefaultFileManager.class);

    private static Map<String, Revision> files = Collections.synchronizedMap(new HashMap<String, Revision>());

    protected boolean reloadingConfigs = true;

    static final String JAR_FILE_NAME_SEPARATOR = "!/";
    static final String JAR_FILE_EXTENSION_END = ".jar/";

    public DefaultFileManager() {
    }

    public void setReloadingConfigs(boolean reloadingConfigs) {
        this.reloadingConfigs = reloadingConfigs;
    }

    public boolean isReloadingConfigs() {
        return reloadingConfigs;
    }

    public boolean fileNeedsReloading(URL fileUrl) {
        return fileUrl != null && fileNeedsReloading(fileUrl.toString());
    }

    public boolean fileNeedsReloading(String fileName) {
        Revision revision = files.get(fileName);

        if (revision == null) {
            // no revision yet and we keep the revision history, so
            // return whether the file needs to be loaded for the first time
            return reloadingConfigs;
        }

        return revision.needsReloading();
    }

    public InputStream loadFile(URL fileUrl) {
        if (fileUrl == null) {
            return null;
        }
        InputStream is = openFile(fileUrl);
        monitorFile(fileUrl);
        return is;
    }

    private InputStream openFile(URL fileUrl) {
        try {
            InputStream is = fileUrl.openStream();
            if (is == null) {
                throw new IllegalArgumentException("No file '" + fileUrl + "' found as a resource");
            }
            return is;
        } catch (IOException e) {
            throw new IllegalArgumentException("No file '" + fileUrl + "' found as a resource");
        }
    }

    public void monitorFile(URL fileUrl) {
        if (isReloadingConfigs()) {
            String fileName = fileUrl.toString();
            Revision revision;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating revision for URL: " +fileName);
            }
            if (URLUtil.isJBossUrl(fileUrl)) {
                revision = JBossFileRevision.build(fileUrl);
            } else if (URLUtil.isJarURL(fileUrl)) {
                revision = JarEntryRevision.build(fileUrl);
            } else {
                revision = FileRevision.build(fileUrl);
            }
            if (revision == null) {
                files.put(fileName, Revision.build(fileUrl));
            } else {
                files.put(fileName, revision);
            }
        }
    }

}
