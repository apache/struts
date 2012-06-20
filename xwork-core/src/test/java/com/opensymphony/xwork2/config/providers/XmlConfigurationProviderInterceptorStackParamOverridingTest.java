package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>XmlConfigurationProviderInterceptorStackParamOverridingTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class XmlConfigurationProviderInterceptorStackParamOverridingTest extends XWorkTestCase {
    
    public void testInterceptorStackParamOveriding() throws Exception {
    	DefaultConfiguration conf = new DefaultConfiguration();
    	final XmlConfigurationProvider p = new XmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-interceptor-stack-param-overriding.xml");
        DefaultFileManagerFactory factory = new DefaultFileManagerFactory();
        factory.setContainer(container);
        factory.setFileManager(new DefaultFileManager());
        p.setFileManagerFactory(factory);
    	configurationManager.addContainerProvider(p);
        conf.reload(new ArrayList<ConfigurationProvider>(){
            {
                add(new XWorkConfigurationProvider());
                add(p);
            }
        });


    	RuntimeConfiguration rtConf = conf.getRuntimeConfiguration();

    	ActionConfig actionOne = rtConf.getActionConfig("", "actionOne");
    	ActionConfig actionTwo = rtConf.getActionConfig("", "actionTwo");

    	List actionOneInterceptors = actionOne.getInterceptors();
    	List actionTwoInterceptors = actionTwo.getInterceptors();

    	assertNotNull(actionOne);
    	assertNotNull(actionTwo);
    	assertNotNull(actionOneInterceptors);
    	assertNotNull(actionTwoInterceptors);
    	assertEquals(actionOneInterceptors.size(), 3);
    	assertEquals(actionTwoInterceptors.size(), 3);

    	InterceptorMapping actionOneInterceptorMapping1 = (InterceptorMapping) actionOneInterceptors.get(0);
    	InterceptorMapping actionOneInterceptorMapping2 = (InterceptorMapping) actionOneInterceptors.get(1);
    	InterceptorMapping actionOneInterceptorMapping3 = (InterceptorMapping) actionOneInterceptors.get(2);
    	InterceptorMapping actionTwoInterceptorMapping1 = (InterceptorMapping) actionTwoInterceptors.get(0);
    	InterceptorMapping actionTwoInterceptorMapping2 = (InterceptorMapping) actionTwoInterceptors.get(1);
    	InterceptorMapping actionTwoInterceptorMapping3 = (InterceptorMapping) actionTwoInterceptors.get(2);

    	assertNotNull(actionOneInterceptorMapping1);
    	assertNotNull(actionOneInterceptorMapping2);
    	assertNotNull(actionOneInterceptorMapping3);
    	assertNotNull(actionTwoInterceptorMapping1);
    	assertNotNull(actionTwoInterceptorMapping2);
    	assertNotNull(actionTwoInterceptorMapping3);


    	assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping1.getInterceptor()).getParamOne(), "i1p1");
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping1.getInterceptor()).getParamTwo(), "i1p2");
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping2.getInterceptor()).getParamOne(), "i2p1");
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping2.getInterceptor()).getParamTwo(), null);
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping3.getInterceptor()).getParamOne(), null);
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping3.getInterceptor()).getParamTwo(), null);

    	assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping1.getInterceptor()).getParamOne(), null);
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping1.getInterceptor()).getParamTwo(), null);
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping2.getInterceptor()).getParamOne(), null);
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping2.getInterceptor()).getParamTwo(), "i2p2");
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping3.getInterceptor()).getParamOne(), "i3p1");
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping3.getInterceptor()).getParamTwo(), "i3p2");

    }

    @Override
    protected void tearDown() throws Exception {
    	configurationManager.clearContainerProviders();
    }
}
