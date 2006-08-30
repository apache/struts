package mailreader2;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.Map;

public class ConfigTest extends XWorkTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        XmlConfigurationProvider c = new XmlConfigurationProvider("struts.xml");
        configurationManager.addConfigurationProvider(c);
        configurationManager.reload();
    }

    private ActionConfig assertClass(String action_name, String class_name) {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();
        ActionConfig config = configuration.getActionConfig("", action_name);
        assertNotNull(config);
        assertTrue("Wrong class name: [" + config.getClassName() + "]",
                class_name.equals(config.getClassName()));
        return config;
    }

    private void assertResult(ActionConfig config, String result_name, String result_value) {
        Map results = config.getResults();
        ResultConfig result = (ResultConfig) results.get(result_name);
        Map params = result.getParams();
        String value = (String) params.get("actionName");
        if (value==null)
            value = (String) params.get("location");
        assertTrue("Wrong result value: [" + value + "]",
                result_value.equals(value));
    }

    public void testSubscriptionSave() throws Exception {
        ActionConfig config = assertClass("subscription/Update", "mailreader2.subscription.Update");
        assertResult(config, ActionSupport.SUCCESS, "registration/+Input");
        assertResult(config, ActionSupport.INPUT, "/pages/subscription.jsp");
    }

    // */!*
    public void testPrepareWildcard() throws Exception {
        ActionConfig config = assertClass("logon/+Input", "mailreader2.logon.Input");
        assertResult(config, ActionSupport.SUCCESS, "/pages/logon.jsp");
    }

    // */*
    public void testExecuteWildcard() throws Exception {
        ActionConfig config = assertClass("registration/Update", "mailreader2.registration.Update");
        assertResult(config, ActionSupport.INPUT, "/pages/registration.jsp");
        ActionConfig config2 = assertClass("logon/Retrieve", "mailreader2.logon.Retrieve");
        assertResult(config2, ActionSupport.INPUT, "/pages/logon.jsp");
    }

    // single wildcard
    public void testDisplayWildcard() throws Exception {
        assertClass("Welcome", "mailreader2.Welcome");
    }

}
