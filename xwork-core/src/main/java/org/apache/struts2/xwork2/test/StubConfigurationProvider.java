package org.apache.struts2.xwork2.test;

import org.apache.struts2.xwork2.config.Configuration;
import org.apache.struts2.xwork2.config.ConfigurationException;
import org.apache.struts2.xwork2.config.ConfigurationProvider;
import org.apache.struts2.xwork2.inject.ContainerBuilder;
import org.apache.struts2.xwork2.util.location.LocatableProperties;

public class StubConfigurationProvider implements ConfigurationProvider {

    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void init(Configuration configuration) throws ConfigurationException {
        // TODO Auto-generated method stub
    }

    public void loadPackages() throws ConfigurationException {
        // TODO Auto-generated method stub

    }

    public boolean needsReload() {
        // TODO Auto-generated method stub
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {
        // TODO Auto-generated method stub

    }

}
