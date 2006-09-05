package tutorial;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.Map;

public class ConfigTest extends XWorkTestCase {

    protected void AssertSuccess(String result) throws Exception {
        assertTrue("Expected a success result!",
                ActionSupport.SUCCESS.equals(result));
    }

    protected void setUp() throws Exception {
        super.setUp();
        XmlConfigurationProvider c = new XmlConfigurationProvider("struts.xml");
        configurationManager.addConfigurationProvider(c);
        configurationManager.reload();
    }

    protected ActionConfig assertClass(String action_name, String class_name) {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();
        ActionConfig config = configuration.getActionConfig("", action_name);
        assertNotNull("Mssing action", config);
        assertTrue("Wrong class name: [" + config.getClassName() + "]",
                class_name.equals(config.getClassName()));
        return config;
    }

    protected void assertResult(ActionConfig config, String result_name, String result_value) {
        Map results = config.getResults();
        ResultConfig result = (ResultConfig) results.get(result_name);
        Map params = result.getParams();
        String value = (String) params.get("actionName");
        if (value == null)
            value = (String) params.get("location");
        assertTrue("Wrong result value: [" + value + "]",
                result_value.equals(value));
    }

    public void testConfig() throws Exception {
        assertNotNull(configurationManager);
    }

}
