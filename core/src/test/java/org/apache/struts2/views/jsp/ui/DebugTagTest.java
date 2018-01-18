/*
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
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Test case for {@link org.apache.struts2.components.Debug}.
 */
public class DebugTagTest extends AbstractUITagTest {

    private DebugTag tag;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tag = new DebugTag();
        tag.setPageContext(pageContext);
        context.put("checkStackProperty", "Hello World");
    }

    public void testDevModeEnabled() throws Exception {
        setDevMode(true);

        tag.doStartTag();
        tag.doEndTag();
        String result = writer.toString();
        assertTrue(StringUtils.isNotEmpty(result));
        assertTrue("Property 'checkStackProperty' should be in Debug Tag output", StringUtils.contains(result, "<td>checkStackProperty</td>"));
    }

    public void testDevModeDisabled() throws Exception {
        setDevMode(false);

        tag.doStartTag();
        tag.doEndTag();
        assertTrue("nothing to see here, devMode=false", StringUtils.isEmpty(writer.toString()));
    }

    public void testTagAttributeOverrideDevModeTrue() throws Exception {
        setDevMode(false);

        PrepareOperations.overrideDevMode(true);
        tag.doStartTag();
        tag.doEndTag();
        String result = writer.toString();
        assertTrue(StringUtils.isNotEmpty(result));
        assertTrue("Property 'checkStackProperty' should be in Debug Tag output", StringUtils.contains(result, "<td>checkStackProperty</td>"));
    }

    public void testTagAttributeOverrideDevModeFalse() throws Exception {
        setDevMode(false);

        PrepareOperations.overrideDevMode(false);
        tag.doStartTag();
        tag.doEndTag();
        assertTrue("nothing to see here, devMode=false and overrideDevMode=false", StringUtils.isEmpty(writer.toString()));
    }

    private void setDevMode(final boolean devMode) {
        setStrutsConstant(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_DEVMODE, Boolean.toString(devMode));
        }});
    }

    /**
     * Overwrite the Struts Constant and reload container
     */
    private void setStrutsConstant(final Map<String, String> overwritePropeties) {
        configurationManager.addContainerProvider(new StubConfigurationProvider() {
            @Override
            public boolean needsReload() {
                return true;
            }

            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                for (Map.Entry<String, String> stringStringEntry : overwritePropeties.entrySet()) {
                    props.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue(), null);
                }
            }
        });

        configurationManager.reload();
        container = configurationManager.getConfiguration().getContainer();
        stack.getContext().put(ActionContext.CONTAINER, container);
    }
}