package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation
 */
public class DefaultFileManagerFactory implements FileManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultFileManagerFactory.class);

    private boolean reloadingConfigs;
    private FileManager fileManager;
    private Container container;

    @Inject(value = "system")
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value = XWorkConstants.RELOAD_XML_CONFIGURATION, required = false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    public FileManager getFileManager() {
        Set<String> names = container.getInstanceNames(FileManager.class);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Found following implementations of FileManager interface: #0", names.toString());
        }
        Set<FileManager> internals = new HashSet<FileManager>();
        Set<FileManager> users = new HashSet<FileManager>();
        for (String fmName : names) {
            FileManager fm = container.getInstance(FileManager.class, fmName);
            if (fm.internal()) {
                internals.add(fm);
            } else {
                users.add(fm);
            }
        }
        for (FileManager fm : users) {
            if (fm.support()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Using FileManager implementation [#0]", fm.getClass().getSimpleName());
                }
                fm.setReloadingConfigs(reloadingConfigs);
                return fm;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("No user defined FileManager, looking up for internal implementations!");
        }
        for (FileManager fm : internals) {
            if (fm.support()) {
                return fm;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using default implementation of FileManager provided under name [system]: #0", fileManager.getClass().getSimpleName());
        }
        fileManager.setReloadingConfigs(reloadingConfigs);
        return fileManager;
    }

}
