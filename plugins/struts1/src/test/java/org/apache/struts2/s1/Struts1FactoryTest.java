/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.s1;

import java.lang.reflect.InvocationTargetException;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ExceptionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;

/**
 * Test of Struts1Factory, which creates Struts 1.x wrappers around XWork config objects.
 */
public class Struts1FactoryTest extends StrutsTestCase {

    private static final String PACKAGE_NAME = "org/apache/struts2/s1";
    
    protected Struts1Factory factory = null;

    /**
     * Set up instance variables required by this test case.
     * @throws Exception 
     */
    public void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(PACKAGE_NAME + "/test-struts-factory.xml", true, null));
        factory = new Struts1Factory(configuration);
        
    }

    /**
     * Test the creation of a Struts 1.x ModuleConfig wrapper around an XWork PackageConfig.
     * The PackageConfig is loaded from test-struts-factory.xml.
     */
    public void testCreateModuleConfig() {
        ModuleConfig moduleConfig = factory.createModuleConfig(PACKAGE_NAME);
        assertNotNull(moduleConfig);
        
        assertEquals("/"+PACKAGE_NAME, moduleConfig.getPrefix());
        
        ActionConfig actionConfig = moduleConfig.findActionConfig("/action1");
        assertNotNull(actionConfig);
        assertEquals("/action1", actionConfig.getPath());
        
        ActionConfig[] actionConfigs = moduleConfig.findActionConfigs();
        assertNotNull(actionConfigs);
        assertEquals(2, actionConfigs.length);
        
        ExceptionConfig exceptionConfig = moduleConfig.findExceptionConfig(Exception.class.getName());
        assertNotNull(exceptionConfig);
        assertEquals(Exception.class.getName(), exceptionConfig.getType());
        
        ExceptionConfig[] exceptionConfigs = moduleConfig.findExceptionConfigs();
        assertNotNull(exceptionConfigs);
        assertEquals(1, exceptionConfigs.length);
        
        ForwardConfig fwdConfig = moduleConfig.findForwardConfig("globalResult");
        assertNotNull(fwdConfig);
        assertEquals("globalResult", fwdConfig.getName());
        
        // These methods are currently not implemented -- replace as functionality is added.
        assertNYI(moduleConfig, "getControllerConfig", null);
        assertNYI(moduleConfig, "getActionFormBeanClass", null);
        assertNYI(moduleConfig, "getActionMappingClass", null);
        assertNYI(moduleConfig, "getActionForwardClass", null);
        assertNYI(moduleConfig, "findException", Class.class);
        assertNYI(moduleConfig, "findFormBeanConfig", String.class);
        assertNYI(moduleConfig, "findFormBeanConfigs", null);
        assertNYI(moduleConfig, "findMessageResourcesConfig", String.class);
        assertNYI(moduleConfig, "findMessageResourcesConfigs", null);
        assertNYI(moduleConfig, "findPlugInConfigs", null);
    }
    
    /**
     * Test the creation of a Struts 1.x ActionMapping wrapper around an XWork ActionConfig.
     * The ActionConfig is loaded from test-struts-factory.xml.
     */
    public void testCreateActionMapping() {
        PackageConfig packageConfig = configuration.getPackageConfig(PACKAGE_NAME);
        com.opensymphony.xwork2.config.entities.ActionConfig actionConfig =
                (com.opensymphony.xwork2.config.entities.ActionConfig) packageConfig.getActionConfigs().get("action1");
        ActionMapping mapping = factory.createActionMapping(actionConfig);
        assertNotNull(mapping);

        assertNotNull(mapping.findForward("result1"));
        assertNotNull(mapping.findForwardConfig("result2"));

        ForwardConfig[] configs = mapping.findForwardConfigs();
        assertNotNull(configs);
        assertEquals(2, configs.length);

        String[] forwards = mapping.findForwards();
        assertNotNull(forwards);
        assertEquals(2, forwards.length);
        
        ActionForward fwd = mapping.findForward("result1");
        assertNotNull(fwd);
        assertEquals("result1", fwd.getName());

        assertNotNull(mapping.findException(NullPointerException.class));
        assertNotNull(mapping.findExceptionConfig("java.lang.IllegalStateException"));

        ExceptionConfig[] exceptionConfigs = mapping.findExceptionConfigs();
        assertNotNull(exceptionConfigs);
        assertEquals(2, exceptionConfigs.length);
        
        ModuleConfig moduleConfig = mapping.getModuleConfig();
        assertNotNull(moduleConfig);
        
        // For now, the path will be null if the ActionMapping was created on its own (as opposed to from a
        // WrapperModuleConfig, which knows the path).
        assertNull(mapping.getPath());
        
        // These methods are currently not implemented -- replace as functionality is added.
        assertNYI(mapping, "getInputForward", null);
        assertNYI(mapping, "getForward", null);
        assertNYI(mapping, "getInclude", null);
        assertNYI(mapping, "getInput", null);
        assertNYI(mapping, "getMultipartClass", null);
        assertNYI(mapping, "getName", null);
        assertNYI(mapping, "getParameter", null);
        assertNYI(mapping, "getPrefix", null);
        assertNYI(mapping, "getRoles", null);
        assertNYI(mapping, "getRoleNames", null);
        assertNYI(mapping, "getScope", null);
        assertNYI(mapping, "getSuffix", null);
        assertNYI(mapping, "getType", null);
        assertNYI(mapping, "getUnknown", null);
        assertNYI(mapping, "getValidate", null);
    }

    /**
     * Test the creation of a Struts 1.x ActionForward wrapper around an XWork ResultConfig.
     * The ResultConfig is loaded from test-struts-factory.xml.
     */
    public void testCreateActionForward() {
        PackageConfig packageConfig = configuration.getPackageConfig(PACKAGE_NAME);
        ResultConfig resultConfig = (ResultConfig) packageConfig.getGlobalResultConfigs().get("globalResult");
        ActionForward fwd = factory.createActionForward(resultConfig);
        assertNotNull(fwd);
        assertEquals("globalResult", fwd.getName());
        
        // These methods are currently not implemented -- replace as functionality is added.
        assertNYI(fwd, "getPath", null);
        assertNYI(fwd, "getModule", null);
        assertNYI(fwd, "getRedirect", null);
    }

    /**
     * Test the creation of a Struts 1.x ExceptionConfig wrapper around an XWork ExceptionHandlerConfig.
     * The ExceptionConfig is loaded from test-struts-factory.xml.
     */
    public void testCreateExceptionConfig() {
        PackageConfig packageConfig = configuration.getPackageConfig(PACKAGE_NAME);
        ExceptionMappingConfig cfg = (ExceptionMappingConfig) packageConfig.getGlobalExceptionMappingConfigs().get(0);
        ExceptionConfig exceptionConfig = factory.createExceptionConfig(cfg);
        assertNotNull(exceptionConfig);
        assertEquals(Exception.class.getName(), exceptionConfig.getType());

        assertNYI(exceptionConfig, "getBundle", null);
        assertNYI(exceptionConfig, "getHandler", null);
        assertNYI(exceptionConfig, "getKey", null);
        assertNYI(exceptionConfig, "getPath", null);
        assertNYI(exceptionConfig, "getScope", null);
    }

    public void testConvertErrors() throws Exception {

        ActionMessage err1 = new ActionMessage("error1");
        ActionMessage err2 = new ActionMessage("error2", new Integer(1));
        ActionErrors errors = new ActionErrors();
        errors.add(errors.GLOBAL_MESSAGE, err1);
        errors.add("foo", err2);

        ActionSupport action = new ActionSupport();
        factory.convertErrors(errors, action);

        assertTrue(1 == action.getActionErrors().size());
        assertTrue(1 == action.getFieldErrors().size());
    }

    /**
     * Assert that the given method throws UnsupportedOperationException.
     */
    private void assertNYI(Object o, String methodName, Class argType) {
        try {
            Class[] argTypes = argType != null ? new Class[]{argType} : null;
            
            Object[] args = null;
            if (argType != null) {
                if (Class.class == argType) {
                    args = new Object[]{argType};
                } else {
                    args = new Object[]{argType.newInstance()};
                }
            }
            o.getClass().getMethod(methodName, argTypes).invoke(o, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertEquals(cause.getMessage(), UnsupportedOperationException.class, cause.getClass());
            
            // OK -- it's what we expected
            return;
        } catch (Exception e) {
            fail(e.getClass().getName() + ": " + e.getMessage());
        }

        fail("Expected UnsupportedOperationException for " + methodName + "() on " + o.getClass().getName());
    }
}
