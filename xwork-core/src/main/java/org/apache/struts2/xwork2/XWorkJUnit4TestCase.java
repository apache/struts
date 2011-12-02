package org.apache.struts2.xwork2;

import org.apache.struts2.xwork2.config.Configuration;
import org.apache.struts2.xwork2.config.ConfigurationException;
import org.apache.struts2.xwork2.config.ConfigurationManager;
import org.apache.struts2.xwork2.config.ConfigurationProvider;
import org.apache.struts2.xwork2.inject.Container;
import org.apache.struts2.xwork2.inject.ContainerBuilder;
import org.apache.struts2.xwork2.inject.Context;
import org.apache.struts2.xwork2.inject.Factory;
import org.apache.struts2.xwork2.inject.Scope;
import org.apache.struts2.xwork2.test.StubConfigurationProvider;
import org.apache.struts2.xwork2.util.XWorkTestCaseHelper;
import org.apache.struts2.xwork2.util.location.LocatableProperties;
import org.junit.After;
import org.junit.Before;

public abstract class XWorkJUnit4TestCase {

    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;

    @Before
    public void setUp() throws Exception {
        configurationManager = XWorkTestCaseHelper.setUp();
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }

    @After
    public void tearDown() throws Exception {
        XWorkTestCaseHelper.tearDown(configurationManager);
        configurationManager = null;
        configuration = null;
        container = null;
        actionProxyFactory = null;
    }

    protected void loadConfigurationProviders(ConfigurationProvider... providers) {
        configurationManager = XWorkTestCaseHelper.loadConfigurationProviders(configurationManager, providers);
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }

    protected void loadButAdd(final Class<?> type, final Object impl) {
        loadButAdd(type, Container.DEFAULT_NAME, impl);
    }

    protected void loadButAdd(final Class<?> type, final String name, final Object impl) {
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                                 LocatableProperties props) throws ConfigurationException {
                builder.factory(type, name, new Factory() {
                    public Object create(Context context) throws Exception {
                        return impl;
                    }

                }, Scope.SINGLETON);
            }
        });
    }

}
