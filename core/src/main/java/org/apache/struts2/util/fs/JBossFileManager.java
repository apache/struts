package org.apache.struts2.util.fs;

import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.FileRevision;
import com.opensymphony.xwork2.util.fs.Revision;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
        if (supports && LOG.isInfoEnabled()) {
            LOG.info("JBoss server detected, Struts 2 will use [#0] to support file system operations!", JBossFileManager.class.getSimpleName());
        }
        return supports;
    }

    private boolean isJBoss5() {
        try {
            Class.forName(VFS_JBOSS5);
            return true;
        } catch (ClassNotFoundException e) {
            LOG.debug("Cannot load [#0] class, not a JBoss 5!", VFS_JBOSS7);
            return false;
        }
    }

    private boolean isJBoss7() {
        try {
            Class.forName(VFS_JBOSS7);
            return true;
        } catch (ClassNotFoundException e) {
            LOG.debug("Cannot load [#0] class, not a JBoss 7!", VFS_JBOSS7);
            return false;
        }
    }

    @Override
    public void monitorFile(URL fileUrl) {
        if (isReloadingConfigs()) {
            if (isJBossUrl(fileUrl)) {
                Revision revision = FileRevision.build(normalizeToFileProtocol(fileUrl));
                files.put(fileUrl.toString(), revision);
            } else {
                super.monitorFile(fileUrl);
            }
        }
    }

    @Override
    public URL normalizeToFileProtocol(URL url) {
        if (isJBossUrl(url))                {
            try {
                return getJBossPhysicalUrl(url);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
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

    private File readJBossPhysicalFile(Object content) throws Exception {
        Method method = content.getClass().getDeclaredMethod("getPhysicalFile");
        return (File) method.invoke(content);
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
