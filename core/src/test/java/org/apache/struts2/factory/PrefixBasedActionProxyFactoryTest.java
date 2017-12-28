package org.apache.struts2.factory;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.StrutsInternalTestCase;

import java.util.Collections;
import java.util.Map;

public class PrefixBasedActionProxyFactoryTest extends StrutsInternalTestCase {

    public void testDifferentPrefixes() throws Exception {
        PrefixBasedActionProxyFactory factory = new PrefixBasedActionProxyFactory();
        factory.setContainer(container);
        factory.setPrefixBasedActionProxyFactories("/ns1:prefix1,/ns2:prefix2");
        factory.init();

        ActionProxy proxy1 = factory.createActionProxy("/ns1", "", "", Collections.<String, Object>emptyMap(), false, true);
        assertTrue(proxy1 instanceof Prefix1ActionProxy);

        ActionProxy proxy2 = factory.createActionProxy("/ns2", "", "", Collections.<String, Object>emptyMap(), false, true);
        assertTrue(proxy2 instanceof Prefix2ActionProxy);
    }

    @Override
    public void setUp() throws Exception {
        ConfigurationProvider[] providers = new ConfigurationProvider[] {
                new StubConfigurationProvider() {
                    @Override
                    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                        builder.factory(ActionProxyFactory.class, "prefix1", new Factory() {
                            public Object create(Context context) throws Exception {
                                return new Prefix1Factory();
                            }

                        }, Scope.SINGLETON);
                    }
                },
                new StubConfigurationProvider() {
                    @Override
                    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                        builder.factory(ActionProxyFactory.class, "prefix2", new Factory() {
                            public Object create(Context context) throws Exception {
                                return new Prefix2Factory();
                            }

                        }, Scope.SINGLETON);
                    }
                }
        };

        loadConfigurationProviders(providers);
    }

    public static class Prefix1Factory extends DefaultActionProxyFactory {
        public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
            return new Prefix1ActionProxy();
        }

    }

    public static class Prefix1ActionProxy extends MockActionProxy {
    }

    public static class Prefix2Factory extends DefaultActionProxyFactory {
        public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
            return new Prefix2ActionProxy();
        }
    }

    public static class Prefix2ActionProxy extends MockActionProxy {
    }

}