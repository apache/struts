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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link FileManager}
 */
public class DefaultFileManager implements FileManager {

    private static Logger LOG = LoggerFactory.getLogger(DefaultFileManager.class);

    public static final String JBOSS5_VFS = "vfs";
    public static final String JBOSS5_VFSZIP = "vfszip";
    public static final String JBOSS5_VFSMEMORY = "vfsmemory";
    public static final String JBOSS5_VFSFILE = "vfsfile";

    public static final String JAR_FILE_NAME_SEPARATOR = "!/";
    public static final String JAR_FILE_EXTENSION_END = ".jar/";

    private static final Pattern JAR_PATTERN = Pattern.compile("^(jar:|wsjar:|zip:|vfsfile:|code-source:)?(file:)?(.*?)(\\!/|\\.jar/)(.*)");
    private static final int JAR_FILE_PATH = 3;

    private static Map<String, Revision> files = Collections.synchronizedMap(new HashMap<String, Revision>());

    protected boolean reloadingConfigs = true;

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
            if (isJBossUrl(fileUrl)) {
                revision = JBossFileRevision.build(fileUrl, this);
            } else if (isJarURL(fileUrl)) {
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
    }

    /**
     * Check if given URL is matching Jar pattern for different servers
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
            if (isJBossUrl(url)){
                return getJBossPhysicalUrl(url);
            } else  if (jarMatcher.matches()) {
                String path = jarMatcher.group(JAR_FILE_PATH);
                return new URL("file", "", path);
            } else {
                //it is not a jar or zip file
                return null;
            }
        } catch (MalformedURLException e) {
            //can this ever happen?
            return null;
        } catch (IOException e) {
            LOG.warn("Error opening JBoss vfs file", e);
            return null;
        }
    }

    /**
     * Check if given URL is pointing to JBoss 5 VFS resource
     * @param fileUrl
     * @return
     */
    protected boolean isJBossUrl(URL fileUrl) {
        final String protocol = fileUrl.getProtocol();
        return JBOSS5_VFSZIP.equals(protocol) || JBOSS5_VFSMEMORY.equals(protocol) || JBOSS5_VFS.equals(protocol)
                || ("true".equals(System.getProperty("jboss.vfs.forceVfsJar")) && JBOSS5_VFSFILE.equals(protocol));
    }

    /**
     * Try to determine physical file location.
     *
     * @param url JBoss VFS URL
     * @return URL pointing to physical file or original URL
     * @throws IOException If conversion fails
     */
    protected URL getJBossPhysicalUrl(URL url) throws IOException {
        Object content = url.openConnection().getContent();
        try {
            String s = content.getClass().toString();
            if (s.startsWith("class org.jboss.vfs.VirtualFile")) { // JBoss 7 and probably 6
                File physicalFile = readJBossPhysicalFile(content);
                return physicalFile.toURI().toURL();
            } else if (s.startsWith("class org.jboss.virtual.VirtualFile")) { // JBoss 5
                String fileName = url.toExternalForm();
                return new URL("file", null, fileName.substring(fileName.indexOf(":") + 1));
            }
        } catch (Exception e) {
            LOG.warn("Error calling getPhysicalFile() on JBoss VirtualFile.", e);
        }
        return url;
    }

    public boolean support() {
        return false; // allow other implementation to be used first
    }

    public Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException {
        if (isJBossUrl(url)) {
            return getAllJBossPhysicalUrls(url);
        }
        return Arrays.asList(url);
    }

    private List<URL> getAllJBossPhysicalUrls(URL url) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        Object content = url.openConnection().getContent();
        try {
            if (content.getClass().toString().startsWith("class org.jboss.vfs.VirtualFile")) {
                File physicalFile = readJBossPhysicalFile(content);
                readFile(urls, physicalFile);
                readFile(urls, physicalFile.getParentFile());
            } else {
                urls.add(url);
            }
        } catch (Exception e) {
            LOG.warn("Error calling getPhysicalFile() on JBoss VirtualFile.", e);
        }
        return urls;
    }

    private File readJBossPhysicalFile(Object content) throws Exception {
        Method method = content.getClass().getDeclaredMethod("getPhysicalFile");
        return (File) method.invoke(content);
    }

    private void readFile(List<URL> urls, File physicalFile) throws MalformedURLException {
        if (physicalFile.isDirectory()) {
            for (File file : physicalFile.listFiles()) {
                if (file.isFile()) {
                    addIfAbsent(urls, file.toURI().toURL());
                } else if (file.isDirectory()) {
                    readFile(urls, file);
                }
            }
        }
    }

    private void addIfAbsent(List<URL> urls, URL fileUrl) {
        if (!urls.contains(fileUrl)) {
            urls.add(fileUrl);
        }
    }
}
