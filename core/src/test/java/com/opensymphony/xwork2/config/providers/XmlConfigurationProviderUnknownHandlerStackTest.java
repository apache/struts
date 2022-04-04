package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.DefaultUnknownHandlerManager;

import java.util.List;

public class XmlConfigurationProviderUnknownHandlerStackTest extends ConfigurationTestBase {

    public void testStackWithElements() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-unknownhandler-stack.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        loadConfigurationProviders(provider);
        configurationManager.reload();

        List<UnknownHandlerConfig> unknownHandlerStack = configuration.getUnknownHandlerStack();
        assertNotNull(unknownHandlerStack);
        assertEquals(2, unknownHandlerStack.size());

        assertEquals("uh1", unknownHandlerStack.get(0).getName());
        assertEquals("uh2", unknownHandlerStack.get(1).getName());

        UnknownHandlerManager unknownHandlerManager = new DefaultUnknownHandlerManager();
        container.inject(unknownHandlerManager);
        assertTrue(unknownHandlerManager.hasUnknownHandlers());
    }

    public void testEmptyStack() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-unknownhandler-stack-empty.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        loadConfigurationProviders(provider);
        configurationManager.reload();

        List<UnknownHandlerConfig> unknownHandlerStack = configuration.getUnknownHandlerStack();
        assertNull(unknownHandlerStack);
    }
}
