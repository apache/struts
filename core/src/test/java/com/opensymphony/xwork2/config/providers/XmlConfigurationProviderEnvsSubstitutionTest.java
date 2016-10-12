package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import org.apache.struts2.StrutsConstants;

public class XmlConfigurationProviderEnvsSubstitutionTest extends ConfigurationTestBase {

    public void testSubstitution() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-envs-substitution.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        configurationManager.addContainerProvider(provider);
        configurationManager.reload();
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();

        String foo = container.getInstance(String.class, "foo");
        assertEquals("bar", foo);

        String user = container.getInstance(String.class, "user");
        assertEquals(System.getenv("USER"), user);

        String home = container.getInstance(String.class, "home");
        assertEquals("Current HOME = " + System.getenv("HOME"), home);

        String os = container.getInstance(String.class, "os");
        assertEquals("Current OS = " + System.getProperty("os.name"), os);

        String unknown = container.getInstance(String.class, "unknown");
        assertEquals("Unknown = default", unknown);

        String devMode = container.getInstance(String.class, StrutsConstants.STRUTS_DEVMODE);
        assertEquals("false", devMode);
    }

}
