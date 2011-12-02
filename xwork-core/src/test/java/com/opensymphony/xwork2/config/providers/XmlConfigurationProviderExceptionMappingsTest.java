package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.mock.MockResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Matthew E. Porter (matthew dot porter at metissian dot com)
 * Date: Aug 15, 2005
 * Time: 2:05:36 PM
 */
public class XmlConfigurationProviderExceptionMappingsTest extends ConfigurationTestBase {

    public void testActions() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-exception-mappings.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        List<ExceptionMappingConfig> exceptionMappings = new ArrayList<ExceptionMappingConfig>();
        HashMap<String, String> parameters = new HashMap<String, String>();
        HashMap<String, ResultConfig> results = new HashMap<String, ResultConfig>();

        exceptionMappings.add(
                new ExceptionMappingConfig.Builder("spooky-result", "com.opensymphony.xwork2.SpookyException", "spooky-result")
                    .build());
        results.put("spooky-result", new ResultConfig.Builder("spooky-result", MockResult.class.getName()).build());

        Map<String, String> resultParams = new HashMap<String, String>();
        resultParams.put("actionName", "bar.vm");
        results.put("specificLocationResult",
                new ResultConfig.Builder("specificLocationResult", ActionChainResult.class.getName())
                    .addParams(resultParams)
                    .build());

        ActionConfig expectedAction = new ActionConfig.Builder("default", "Bar", SimpleAction.class.getName())
            .addParams(parameters)
            .addResultConfigs(results)
            .addExceptionMappings(exceptionMappings)
            .build();

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(1, actionConfigs.size());

        ActionConfig action = (ActionConfig) actionConfigs.get("Bar");
        assertEquals(expectedAction, action);
    }

}
