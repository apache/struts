package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.LocatableProperties;

/**
 * Allows to specify custom {@link FileManager} by user
 */
public class FileManagerProvider implements ContainerProvider {

    private Class<? extends FileManager> fileManagerClass;
    private String name;

    public FileManagerProvider(Class<? extends FileManager> fileManagerClass, String name) {
        this.fileManagerClass = fileManagerClass;
        this.name = name;
    }

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        builder.factory(FileManager.class, name, fileManagerClass, Scope.SINGLETON);
    }

}
