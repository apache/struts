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
package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.junit.StrutsJUnit4TestCase;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Properties;

import static org.apache.struts2.views.velocity.VelocityManager.KEY_VELOCITY_STRUTS_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

public class VelocityManagerTest extends StrutsJUnit4TestCase {

    VelocityManager velocityManager = new VelocityManager();

    @Before
    public void inject() {
        container.inject(velocityManager);
        ServletActionContext.setServletContext(servletContext);
    }

    @After
    public void reset() {
        ServletActionContext.setServletContext(null);
    }

    @Test
    public void testProperties() {
        Properties props = new Properties();
        velocityManager.setVelocityProperties(props);

        assertEquals(props, velocityManager.getVelocityProperties());
    }

    @Test
    public void testInitSuccess() {
        velocityManager.init(servletContext);

        assertNotNull(velocityManager.getVelocityEngine());
    }

    @Test
    public void testInitWithToolbox() {
        velocityManager.setToolBoxLocation("tools.xml");

        velocityManager.init(servletContext);

        assertNotNull(velocityManager.getVelocityEngine());
        assertNotNull(velocityManager.getToolboxManager());
    }

    @Test
    public void testInitFailsWithInvalidToolBoxLocation() {
        velocityManager.setToolBoxLocation("invalid.xml");

        Exception e = assertThrows(Exception.class, () -> velocityManager.init(servletContext));
        assertThat(e).hasMessageContaining("Could not find any configuration at invalid.xml");
    }

    @Test
    public void testCreateContext() {
        velocityManager.init(servletContext);

        Context context = velocityManager.createContext(ActionContext.getContext().getValueStack(), request, response);

        assertNotNull(context);
        assertThat(context.get("struts")).isInstanceOf(VelocityStrutsUtil.class);
        assertThat(context.get("stack")).isInstanceOf(ValueStack.class);
        assertThat(context.get("request")).isInstanceOf(HttpServletRequest.class);
        assertThat(context.get("response")).isInstanceOf(HttpServletResponse.class);
        assertEquals(context, request.getAttribute(KEY_VELOCITY_STRUTS_CONTEXT));
    }

    @Test
    public void testCreateToolboxContext() {
        velocityManager.setToolBoxLocation("tools.xml");
        velocityManager.init(servletContext);

        Context context = velocityManager.createContext(ActionContext.getContext().getValueStack(), request, response);

        assertNotNull(context);
        assertThat(context).isInstanceOf(ToolContext.class);
        assertEquals(context, request.getAttribute(KEY_VELOCITY_STRUTS_CONTEXT));
    }
}
