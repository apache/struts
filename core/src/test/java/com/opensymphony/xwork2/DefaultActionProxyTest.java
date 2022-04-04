package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.StrutsInternalTestCase;
import org.junit.Test;

public class DefaultActionProxyTest extends StrutsInternalTestCase {

    @Test
    public void testThorwExceptionOnNotAllowedMethod() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-allowed-methods.xml";
        loadConfigurationProviders(new XmlConfigurationProvider(filename));
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "strict", "Default", "notAllowed", true, true);
        container.inject(dap);

        try {
            dap.prepare();
            fail("Must throw exception!");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Method notAllowed for action Default is not allowed!");
        }
    }
}