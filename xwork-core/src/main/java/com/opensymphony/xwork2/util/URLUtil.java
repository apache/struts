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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to extract file paths from different urls
 */
public class URLUtil {

    private static final Logger LOG = LoggerFactory.getLogger(URLUtil.class);
    public static final String JBOSS5_VFS = "vfs";
    public static final String JBOSS5_VFSZIP = "vfszip";
    public static final String JBOSS5_VFSMEMORY = "vfsmemory";
    public static final String JBOSS5_VFSFILE = "vfsfile";

    private static final Pattern JAR_PATTERN = Pattern.compile("^(jar:|wsjar:|zip:|vfsfile:|code-source:)?(file:)?(.*?)(\\!/|\\.jar/)(.*)");
    private static final int JAR_FILE_PATH = 3;

    /**
     * Convert URLs to URLs with "file" protocol
     * @param url URL to convert to a jar url
     * @return a URL to a file, or null if the URL external form cannot be parsed
     */
    public static URL normalizeToFileProtocol(URL url) {
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
     * Verify That the given String is in valid URL format.
     * @param url The url string to verify.
     * @return a boolean indicating whether the URL seems to be incorrect.
     */
    public static boolean verifyUrl(String url) {
        if (url == null) {
            return false;
        }

        if (url.startsWith("https://")) {
            // URL doesn't understand the https protocol, hack it
            url = "http://" + url.substring(8);
        }

        try {
            new URL(url);

            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Check if given URL is matching Jar pattern for different servers
     * @param fileUrl
     * @return
     */
    public static boolean isJarURL(URL fileUrl) {
        Matcher jarMatcher = URLUtil.JAR_PATTERN.matcher(fileUrl.getPath());
        return jarMatcher.matches(); 
    }

    /**
     * Check if given URL is pointing to JBoss 5 VFS resource
     * @param fileUrl
     * @return
     */
    public static boolean isJBossUrl(URL fileUrl) {
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
    public static URL getJBossPhysicalUrl(URL url) throws IOException {
        Object content = url.openConnection().getContent();
        try {
            if (content.getClass().toString().startsWith("class org.jboss.vfs.VirtualFile")) {
                Method method = content.getClass().getDeclaredMethod("getPhysicalFile");
                File physicalFile = (File) method.invoke(content);
                return physicalFile.toURI().toURL();
            }
        } catch (Exception e) {
            LOG.warn("Error calling getPhysicalFile() on JBoss VirtualFile.", e);
        }
        return url;
    }

}
