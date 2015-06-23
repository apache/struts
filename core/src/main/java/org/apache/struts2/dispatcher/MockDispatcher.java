package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.config.ConfigurationManager;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

public class MockDispatcher extends Dispatcher {

    private final ConfigurationManager copyConfigurationManager;

    public MockDispatcher(ServletContext servletContext, Map<String, String> context, ConfigurationManager configurationManager) {
        super(servletContext, context);
        this.copyConfigurationManager = configurationManager;
    }

    @Override
    public void init() {
        super.init();
        ContainerHolder.clear();
        this.configurationManager = copyConfigurationManager;
    }
}
