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
