package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;

import java.util.Map;

/**
 * @author John Lindal
 */
public class XmlConfigurationProviderAllowedMethodsTest extends ConfigurationTestBase {

    public void testDefaultAllowedMethods() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-allowed-methods.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(5, actionConfigs.size());

        ActionConfig action = (ActionConfig) actionConfigs.get("Default");
        assertEquals(1, action.getAllowedMethods().size());
        assertTrue(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertTrue(action.isAllowedMethod("foo"));
        assertTrue(action.isAllowedMethod("bar"));
        assertTrue(action.isAllowedMethod("baz"));
        assertTrue(action.isAllowedMethod("xyz"));

        action = (ActionConfig) actionConfigs.get("Boring");
        assertEquals(2, action.getAllowedMethods().size());
        assertTrue(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertFalse(action.isAllowedMethod("foo"));
        assertFalse(action.isAllowedMethod("bar"));
        assertFalse(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));

        action = (ActionConfig) actionConfigs.get("Foo");
        assertEquals(3, action.getAllowedMethods().size());
        assertTrue(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertTrue(action.isAllowedMethod("foo"));
        assertFalse(action.isAllowedMethod("bar"));
        assertFalse(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));

        action = (ActionConfig) actionConfigs.get("Bar");
        assertEquals(4, action.getAllowedMethods().size());
        assertTrue(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertTrue(action.isAllowedMethod("foo"));
        assertTrue(action.isAllowedMethod("bar"));
        assertFalse(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));

        action = (ActionConfig) actionConfigs.get("Baz");
        assertEquals(5, action.getAllowedMethods().size());
        assertFalse(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertTrue(action.isAllowedMethod("foo"));
        assertTrue(action.isAllowedMethod("bar"));
        assertTrue(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));
    }

    public void testStrictAllowedMethods() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-allowed-methods.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("strict");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(5, actionConfigs.size());

        ActionConfig action = (ActionConfig) actionConfigs.get("Default");
        assertEquals(2, action.getAllowedMethods().size());
        assertTrue(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertFalse(action.isAllowedMethod("foo"));
        assertFalse(action.isAllowedMethod("bar"));
        assertFalse(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));

        action = (ActionConfig) actionConfigs.get("Boring");
        assertEquals(2, action.getAllowedMethods().size());
        assertTrue(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertFalse(action.isAllowedMethod("foo"));
        assertFalse(action.isAllowedMethod("bar"));
        assertFalse(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));

        action = (ActionConfig) actionConfigs.get("Foo");
        assertEquals(3, action.getAllowedMethods().size());
        assertTrue(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertTrue(action.isAllowedMethod("foo"));
        assertFalse(action.isAllowedMethod("bar"));
        assertFalse(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));

        action = (ActionConfig) actionConfigs.get("Bar");
        assertEquals(4, action.getAllowedMethods().size());
        assertTrue(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertTrue(action.isAllowedMethod("foo"));
        assertTrue(action.isAllowedMethod("bar"));
        assertFalse(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));

        action = (ActionConfig) actionConfigs.get("Baz");
        assertEquals(5, action.getAllowedMethods().size());
        assertFalse(action.isAllowedMethod("execute"));
        assertTrue(action.isAllowedMethod("input"));
        assertTrue(action.isAllowedMethod("cancel"));
        assertTrue(action.isAllowedMethod("foo"));
        assertTrue(action.isAllowedMethod("bar"));
        assertTrue(action.isAllowedMethod("baz"));
        assertFalse(action.isAllowedMethod("xyz"));
    }

}
