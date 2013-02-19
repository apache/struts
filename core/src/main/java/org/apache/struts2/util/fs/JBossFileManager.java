package org.apache.struts2.util.fs;

import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.FileRevision;
import com.opensymphony.xwork2.util.fs.JarEntryRevision;
import com.opensymphony.xwork2.util.fs.Revision;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * FileManager implementation used with JBoss AS
 */
public class JBossFileManager extends DefaultFileManager {

    private static final Logger LOG = LoggerFactory.getLogger(JBossFileManager.class);

    private static final String JBOSS5_VFS = "vfs";
    private static final String JBOSS5_VFSZIP = "vfszip";
    private static final String JBOSS5_VFSMEMORY = "vfsmemory";
    private static final String JBOSS5_VFSFILE = "vfsfile";

    private static final String VFS_JBOSS7 = "org.jboss.vfs.VirtualFile";
    private static final String VFS_JBOSS5 = "org.jboss.virtual.VirtualFile";

    @Override
    public boolean support() {
        boolean supports = isJBoss7() || isJBoss5();
        if (supports && LOG.isDebugEnabled()) {
            LOG.debug("JBoss server detected, Struts 2 will use [#0] to support file system operations!", JBossFileManager.class.getSimpleName());
        }
        return supports;
    }

    private boolean isJBoss5() {
        try {
            Class.forName(VFS_JBOSS5);
            return true;
        } catch (ClassNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot load [#0] class, not a JBoss 5!", VFS_JBOSS5);
            }
            return false;
        }
    }

    private boolean isJBoss7() {
        try {
            Class.forName(VFS_JBOSS7);
            return true;
        } catch (ClassNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot load [#0] class, not a JBoss 7!", VFS_JBOSS7);
            }
            return false;
        }
    }

    @Override
    public void monitorFile(URL fileUrl) {
        if (isJBossUrl(fileUrl)) {
            String fileName = fileUrl.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating revision for URL: " + fileName);
            }
            URL normalizedUrl = normalizeToFileProtocol(fileUrl);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Normalized URL for [#0] is [#1]", fileName, normalizedUrl.toString());
            }
            Revision revision;
            if ("file".equals(normalizedUrl.getProtocol())) {
                revision = FileRevision.build(normalizedUrl);
            } else if ("jar".equals(normalizedUrl.getProtocol())) {
                revision = JarEntryRevision.build(normalizedUrl);
            } else {
                revision = Revision.build(normalizedUrl);
            }
            files.put(fileName, revision);
        } else {
            super.monitorFile(fileUrl);
        }
    }

    @Override
    public URL normalizeToFileProtocol(URL url) {
        if (isJBossUrl(url))                {
            try {
                return getJBossPhysicalUrl(url);
            } catch (IOException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getMessage(), e);
                }
                return null;
            }
        } else {
            return super.normalizeToFileProtocol(url);
        }
    }

    @Override
    public Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException {
        if (isJBossUrl(url)) {
            return getAllJBossPhysicalUrls(url);
        } else {
            return super.getAllPhysicalUrls(url);
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
     * @throws java.io.IOException If conversion fails
     */
    protected URL getJBossPhysicalUrl(URL url) throws IOException {
        Object content = url.openConnection().getContent();
        String classContent = content.getClass().toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reading physical URL for [#0]", url.toString());
        }
        if (classContent.startsWith("class org.jboss.vfs.VirtualFile")) { // JBoss 7 and probably 6
            File physicalFile = readJBossPhysicalFile(content);
            return physicalFile.toURI().toURL();
        } else if (classContent.startsWith("class org.jboss.virtual.VirtualFile")) { // JBoss 5
            return readJBoss5Url(content);
        }
        return url;
    }

    private List<URL> getAllJBossPhysicalUrls(URL url) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        Object content = url.openConnection().getContent();
        String classContent = content.getClass().toString();
        if (classContent.startsWith("class org.jboss.vfs.VirtualFile")) {
            File physicalFile = readJBossPhysicalFile(content);
            if (physicalFile != null) {
                readFile(urls, physicalFile);
                readFile(urls, physicalFile.getParentFile());
            }
        } else if (classContent.startsWith("class org.jboss.virtual.VirtualFile")) {
            URL physicalUrl = readJBoss5Url(content);
            if (physicalUrl != null) {
                urls.add(physicalUrl);
            }
        } else {
            urls.add(url);
        }
        return urls;
    }

    private File readJBossPhysicalFile(Object content) {
        try {
            Method method = content.getClass().getDeclaredMethod("getPhysicalFile");
            return (File) method.invoke(content);
        } catch (NoSuchMethodException e) {
            LOG.error("Provided class content [#0] is not a JBoss VirtualFile, getPhysicalFile() method not found!", e, content.getClass().getSimpleName());
        } catch (InvocationTargetException e) {
            LOG.error("Cannot invoke getPhysicalFile() method!", e);
        } catch (IllegalAccessException e) {
            LOG.error("Cannot access getPhysicalFile() method!", e);
        }
        return null;
    }

    private URL readJBoss5Url(Object content) {
        try {
            Method method = content.getClass().getDeclaredMethod("getHandler");
            method.setAccessible(true);
            Object handler = method.invoke(content);
            method = handler.getClass().getMethod("getRealURL");
            return (URL) method.invoke(handler);
        } catch (NoSuchMethodException e) {
            LOG.error("Provided class content [#0] is not a JBoss VirtualFile, getHandler() or getRealURL() method not found!", e, content.getClass().getSimpleName());
        } catch (InvocationTargetException e) {
            LOG.error("Cannot invoke getHandler() or getRealURL() method!", e);
        } catch (IllegalAccessException e) {
            LOG.error("Cannot access getHandler() or getRealURL() method!", e);
        }
        return null;
    }

    private void readFile(List<URL> urls, File physicalFile) throws MalformedURLException {
        File[] files = physicalFile.listFiles();
        if (physicalFile.isDirectory() && files != null) {
            for (File file : files) {
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
