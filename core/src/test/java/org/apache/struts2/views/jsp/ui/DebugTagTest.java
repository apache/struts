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
        stack.getActionContext().getSession().put("nonce", "r4nd0m");

        tag.doStartTag();
        tag.doEndTag();
        String result = writer.toString();

        assertTrue("Nonce value not included", result.contains("nonce=\"r4nd0m\""));
        assertTrue(StringUtils.isNotEmpty(result));
        assertTrue("Property 'checkStackProperty' should be in Debug Tag output", StringUtils.contains(result, "<td>checkStackProperty</td>"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        DebugTag freshTag = new DebugTag();
        freshTag.setPageContext(pageContext);
        // DebugTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDevModeEnabled_clearTagStateSet() throws Exception {
        setDevMode(true);
        stack.getActionContext().getSession().put("nonce", "r4nd0m");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        String result = writer.toString();

        assertTrue("Nonce value not included", result.contains("nonce=\"r4nd0m\""));
        assertTrue(StringUtils.isNotEmpty(result));
        assertTrue("Property 'checkStackProperty' should be in Debug Tag output", StringUtils.contains(result, "<td>checkStackProperty</td>"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        DebugTag freshTag = new DebugTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDevModeDisabled() throws Exception {
        setDevMode(false);

        tag.doStartTag();
        tag.doEndTag();
        assertTrue("nothing to see here, devMode=false", StringUtils.isEmpty(writer.toString()));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        DebugTag freshTag = new DebugTag();
        freshTag.setPageContext(pageContext);
        // DebugTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testDevModeDisabled_clearTagStateSet() throws Exception {
        setDevMode(false);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertTrue("nothing to see here, devMode=false", StringUtils.isEmpty(writer.toString()));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        DebugTag freshTag = new DebugTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testTagAttributeOverrideDevModeTrue() throws Exception {
        setDevMode(false);

        PrepareOperations.overrideDevMode(true);
        tag.doStartTag();
        tag.doEndTag();
        String result = writer.toString();
        assertTrue(StringUtils.isNotEmpty(result));
        assertTrue("Property 'checkStackProperty' should be in Debug Tag output", StringUtils.contains(result, "<td>checkStackProperty</td>"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        DebugTag freshTag = new DebugTag();
        freshTag.setPageContext(pageContext);
        // DebugTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

        PrepareOperations.clearDevModeOverride();  // Clear DevMode override. Avoid ThreadLocal side-effects if test thread re-used.
    }

    public void testTagAttributeOverrideDevModeTrue_clearTagStateSet() throws Exception {
        setDevMode(false);

        PrepareOperations.overrideDevMode(true);
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        String result = writer.toString();
        assertTrue(StringUtils.isNotEmpty(result));
        assertTrue("Property 'checkStackProperty' should be in Debug Tag output", StringUtils.contains(result, "<td>checkStackProperty</td>"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        DebugTag freshTag = new DebugTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

        PrepareOperations.clearDevModeOverride();  // Clear DevMode override. Avoid ThreadLocal side-effects if test thread re-used.
    }

    public void testTagAttributeOverrideDevModeFalse() throws Exception {
        setDevMode(false);

        PrepareOperations.overrideDevMode(false);
        tag.doStartTag();
        tag.doEndTag();
        assertTrue("nothing to see here, devMode=false and overrideDevMode=false", StringUtils.isEmpty(writer.toString()));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        DebugTag freshTag = new DebugTag();
        freshTag.setPageContext(pageContext);
        // DebugTag has no additional state, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

        PrepareOperations.clearDevModeOverride();  // Clear DevMode override. Avoid ThreadLocal side-effects if test thread re-used.
    }

    public void testTagAttributeOverrideDevModeFalse_clearTagStateSet() throws Exception {
        setDevMode(false);

        PrepareOperations.overrideDevMode(false);
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertTrue("nothing to see here, devMode=false and overrideDevMode=false", StringUtils.isEmpty(writer.toString()));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        DebugTag freshTag = new DebugTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));

        PrepareOperations.clearDevModeOverride();  // Clear DevMode override. Avoid ThreadLocal side-effects if test thread re-used.
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
        stack.getActionContext().withContainer(container);
    }
}