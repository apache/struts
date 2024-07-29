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
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.junit.StrutsJUnit4TestCase;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.apache.struts2.views.velocity.VelocityManager.KEY_VELOCITY_STRUTS_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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
    public void overridingPropertiesLoaded() {
        var props = new Properties();
        props.setProperty("test", "value");
        velocityManager.setVelocityProperties(props);

        velocityManager.init(servletContext);

        assertEquals("value", velocityManager.getVelocityEngine().getProperty("test"));
        assertEquals(props, velocityManager.getVelocityProperties());
    }

    @Test
    public void initSuccessful() {
        velocityManager.init(servletContext);

        assertNotNull(velocityManager.getVelocityEngine());
    }

    @Test
    public void exceptionThrownOnNoServletContext() {
        assertThrows(IllegalArgumentException.class, () -> velocityManager.init(null));
    }

    @Test
    public void initMethodIdempotent() {
        velocityManager.init(servletContext);

        var engine = velocityManager.getVelocityEngine();

        velocityManager.init(servletContext);

        assertEquals(engine, velocityManager.getVelocityEngine());
    }

    @Test
    public void loadsConfigFromWebInfPath() {
        velocityManager.setCustomConfigFile("webinf-velocity.properties");

        velocityManager.init(servletContext);

        assertEquals("webinf", velocityManager.getVelocityEngine().getProperty("test"));
    }

    @Test
    public void loadsConfigFromClassPath() {
        var servletContext = mock(ServletContext.class);
        doReturn(null).when(servletContext).getRealPath(anyString());
        velocityManager.setCustomConfigFile("test-velocity.properties");

        velocityManager.init(servletContext);

        assertEquals("value", velocityManager.getVelocityEngine().getProperty("test"));
    }

    @Test
    public void initWithToolboxLocation() {
        velocityManager.setToolBoxLocation("tools.xml");

        velocityManager.init(servletContext);

        assertNotNull(velocityManager.getVelocityEngine());
        assertNotNull(velocityManager.getVelocityTools());
    }

    @Test
    public void initFailsWithInvalidToolBoxLocation() {
        velocityManager.setToolBoxLocation("invalid.xml");

        Exception e = assertThrows(Exception.class, () -> velocityManager.init(servletContext));
        assertThat(e).hasMessageContaining("Could not find any configuration at invalid.xml");
    }

    @Test
    public void createContext() {
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
    public void createToolboxContext() {
        velocityManager.setToolBoxLocation("tools.xml");
        velocityManager.init(servletContext);

        Context context = velocityManager.createContext(ActionContext.getContext().getValueStack(), request, response);

        assertNotNull(context);
        assertThat(context).isInstanceOf(ToolContext.class);
        assertEquals(context, request.getAttribute(KEY_VELOCITY_STRUTS_CONTEXT));
    }
}
