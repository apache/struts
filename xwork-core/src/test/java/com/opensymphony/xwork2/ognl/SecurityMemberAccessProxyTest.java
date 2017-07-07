package com.opensymphony.xwork2.ognl;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

public class SecurityMemberAccessProxyTest extends XWorkTestCase {
    private Map<String, Object> context;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        context = new HashMap<String, Object>();
        // Set up XWork
        XmlConfigurationProvider provider = new XmlConfigurationProvider("com/opensymphony/xwork2/spring/actionContext-xwork.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
    }

    public void testProxyAccessIsBlocked() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null,
                "paramsAwareProxiedAction", null, context);

        SecurityMemberAccess sma = new SecurityMemberAccess(false);
        sma.setDisallowProxyMemberAccess(true);

        Member member = proxy.getAction().getClass().getMethod("isExposeProxy");

        boolean accessible = sma.isAccessible(context, proxy.getAction(), member, "");
        assertFalse(accessible);
    }

    public void testProxyAccessIsAccessible() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null,
                "paramsAwareProxiedAction", null, context);

        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        Member member = proxy.getAction().getClass().getMethod("isExposeProxy");

        boolean accessible = sma.isAccessible(context, proxy.getAction(), member, "");
        assertTrue(accessible);
    }
}