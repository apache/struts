package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Set;

/**
 * Default implementation
 */
public class DefaultFileManagerFactory implements FileManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultFileManagerFactory.class);

    private FileManager fileManager;

    @Inject
    public DefaultFileManagerFactory(Container container) {
        Set<String> names = container.getInstanceNames(FileManager.class);
        for (String fmName : names) {
            FileManager fm = container.getInstance(FileManager.class, fmName);
            if (fm.support()) {
                if (fileManager != null) {
                    LOG.error("More than one FileManager supports current file system, [#0] and [#1]! "
                            + "Remove one of them from the config! Using implementation [#2]",
                            fm.toString(), fileManager.toString(), fm.toString());
                }
                fileManager = fm;
            }
        }
        if (fileManager == null) {
            LOG.debug("Using default implementation as a FileManager.");
            fileManager = new DefaultFileManager();
            container.inject(fileManager);
        }
    }

    public FileManager getFileManager() {
        return fileManager;
    }

}
