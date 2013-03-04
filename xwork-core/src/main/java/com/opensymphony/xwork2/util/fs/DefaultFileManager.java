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
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link FileManager}
 */
public class DefaultFileManager implements FileManager {

    private static Logger LOG = LoggerFactory.getLogger(DefaultFileManager.class);

    private static final Pattern JAR_PATTERN = Pattern.compile("^(jar:|wsjar:|zip:|vfsfile:|code-source:)?(file:)?(.*?)(\\!/|\\.jar/)(.*)");
    private static final int JAR_FILE_PATH = 3;

    protected static Map<String, Revision> files = Collections.synchronizedMap(new HashMap<String, Revision>());

    protected boolean reloadingConfigs = false;

    public DefaultFileManager() {
    }

    public void setReloadingConfigs(boolean reloadingConfigs) {
        this.reloadingConfigs = reloadingConfigs;
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
        String fileName = fileUrl.toString();
        Revision revision;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating revision for URL: " + fileName);
        }
        if (isJarURL(fileUrl)) {
            revision = JarEntryRevision.build(fileUrl, this);
        } else {
            revision = FileRevision.build(fileUrl);
        }
        if (revision == null) {
            files.put(fileName, Revision.build(fileUrl));
        } else {
            files.put(fileName, revision);
        }
    }

    /**
     * Check if given URL is matching Jar pattern for different servers
     *
     * @param fileUrl
     * @return
     */
    protected boolean isJarURL(URL fileUrl) {
        Matcher jarMatcher = JAR_PATTERN.matcher(fileUrl.getPath());
        return jarMatcher.matches();
    }

    public URL normalizeToFileProtocol(URL url) {
        String fileName = url.toExternalForm();
        Matcher jarMatcher = JAR_PATTERN.matcher(fileName);
        try {
            if (jarMatcher.matches()) {
                String path = jarMatcher.group(JAR_FILE_PATH);
                return new URL("file", "", path);
            } else if ("file".equals(url.getProtocol())) {
                return url; // it's already a file
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not normalize URL [#0] to file protocol!", url.toString());
                }
                return null;
            }
        } catch (MalformedURLException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Error normalizing URL [#0] to file protocol!", e, url.toString());
            }
            return null;
        }
    }

    public boolean support() {
        return false; // allow other implementation to be used first
    }

    public boolean internal() {
        return true;
    }

    public Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException {
        return Arrays.asList(url);
    }

}
