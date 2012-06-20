package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.LocatableProperties;

/**
 * Allows to specify custom {@link FileManagerFactory}
 */
public class FileManagerFactoryProvider implements ContainerProvider {

    private Class<? extends FileManagerFactory> factoryClass;

    public FileManagerFactoryProvider(Class<? extends FileManagerFactory> factoryClass) {
        this.factoryClass = factoryClass;
    }

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        builder.factory(FileManagerFactory.class, factoryClass.getSimpleName(), factoryClass, Scope.SINGLETON);
    }

}
