package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.XWorkJUnit4TestCase;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationProviderOgnlAllowlistTest extends XWorkJUnit4TestCase {

    private final ConfigurationProvider testXml1 = new StrutsXmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-allowlist.xml");
    private final ConfigurationProvider testXml2 = new StrutsXmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-allowlist-2.xml");
    private ProviderAllowlist providerAllowlist;

    @Before
    public void setUp() throws Exception {
        loadConfigurationProviders(testXml1, testXml2);
        providerAllowlist = container.getInstance(ProviderAllowlist.class);
    }

    @Test
    public void allowlist() throws Exception {
        loadConfigurationProviders(testXml1, testXml2);
        providerAllowlist = container.getInstance(ProviderAllowlist.class);

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(
                Class.forName("com.opensymphony.xwork2.interceptor.ValidationAware"),
                Class.forName("com.opensymphony.xwork2.LocaleProvider"),
                Class.forName("java.io.Serializable"),
                Class.forName("com.opensymphony.xwork2.mock.MockResult"),
                Class.forName("com.opensymphony.xwork2.interceptor.ConditionalInterceptor"),
                Class.forName("com.opensymphony.xwork2.ActionSupport"),
                Class.forName("com.opensymphony.xwork2.ActionChainResult"),
                Class.forName("com.opensymphony.xwork2.TextProvider"),
                Class.forName("org.apache.struts2.interceptor.NoOpInterceptor"),
                Class.forName("com.opensymphony.xwork2.interceptor.Interceptor"),
                Class.forName("java.lang.Object"),
                Class.forName("com.opensymphony.xwork2.Validateable"),
                Class.forName("com.opensymphony.xwork2.mock.MockInterceptor"),
                Class.forName("com.opensymphony.xwork2.Action"),
                Class.forName("com.opensymphony.xwork2.interceptor.AbstractInterceptor"),
                Class.forName("com.opensymphony.xwork2.Result"),
                Class.forName("com.opensymphony.xwork2.SimpleAction")
        );
    }

    @Test
    public void allowlist_1only() throws Exception {
        loadConfigurationProviders(testXml1);
        providerAllowlist = container.getInstance(ProviderAllowlist.class);

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(
                Class.forName("com.opensymphony.xwork2.interceptor.ValidationAware"),
                Class.forName("com.opensymphony.xwork2.LocaleProvider"),
                Class.forName("java.io.Serializable"),
                Class.forName("com.opensymphony.xwork2.mock.MockResult"),
                Class.forName("com.opensymphony.xwork2.interceptor.ConditionalInterceptor"),
                Class.forName("com.opensymphony.xwork2.ActionSupport"),
                Class.forName("com.opensymphony.xwork2.TextProvider"),
                Class.forName("com.opensymphony.xwork2.interceptor.Interceptor"),
                Class.forName("java.lang.Object"),
                Class.forName("com.opensymphony.xwork2.Validateable"),
                Class.forName("com.opensymphony.xwork2.mock.MockInterceptor"),
                Class.forName("com.opensymphony.xwork2.Action"),
                Class.forName("com.opensymphony.xwork2.interceptor.AbstractInterceptor"),
                Class.forName("com.opensymphony.xwork2.Result"),
                Class.forName("com.opensymphony.xwork2.SimpleAction")
        );
    }

    @Test
    public void allowlist_2only() throws Exception {
        loadConfigurationProviders(testXml2);
        providerAllowlist = container.getInstance(ProviderAllowlist.class);

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(
                Class.forName("com.opensymphony.xwork2.interceptor.ValidationAware"),
                Class.forName("com.opensymphony.xwork2.LocaleProvider"),
                Class.forName("java.io.Serializable"),
                Class.forName("com.opensymphony.xwork2.interceptor.ConditionalInterceptor"),
                Class.forName("com.opensymphony.xwork2.ActionSupport"),
                Class.forName("com.opensymphony.xwork2.ActionChainResult"),
                Class.forName("com.opensymphony.xwork2.TextProvider"),
                Class.forName("org.apache.struts2.interceptor.NoOpInterceptor"),
                Class.forName("com.opensymphony.xwork2.interceptor.Interceptor"),
                Class.forName("java.lang.Object"),
                Class.forName("com.opensymphony.xwork2.Validateable"),
                Class.forName("com.opensymphony.xwork2.Action"),
                Class.forName("com.opensymphony.xwork2.interceptor.AbstractInterceptor"),
                Class.forName("com.opensymphony.xwork2.Result")
        );
    }
}
