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
import org.apache.struts2.junit.StrutsJUnit4TestCase;
import org.apache.struts2.views.jsp.ui.OgnlTool;
import org.apache.velocity.context.Context;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class VelocityManagerTest extends StrutsJUnit4TestCase {

    VelocityManager velocityManager = new VelocityManager();

    @Before
    public void inject() throws Exception {
        container.inject(velocityManager);
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
    public void testCreateContext() {
        velocityManager.init(servletContext);

        Context context = velocityManager.createContext(ActionContext.getContext().getValueStack(), request, response);

        assertNotNull(context);
        assertTrue(context.get("struts") instanceof VelocityStrutsUtil);
        assertTrue(context.get("ognl") instanceof OgnlTool);
        assertTrue(context.get("stack") instanceof ValueStack);
        assertTrue(context.get("request") instanceof HttpServletRequest);
        assertTrue(context.get("response") instanceof HttpServletResponse);
    }

    @Test
    public void testInitFailsWithInvalidToolBoxLocation() {
        velocityManager.setToolBoxLocation("nonexistent");

        Exception e = assertThrows(Exception.class, () -> velocityManager.init(servletContext));
        assertTrue(e.getMessage().contains("Could not find any configuration at nonexistent"));
    }
}
