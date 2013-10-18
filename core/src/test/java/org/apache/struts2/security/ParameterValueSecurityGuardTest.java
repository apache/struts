package org.apache.struts2.security;

import com.mockobjects.servlet.MockHttpServletRequest;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ParameterValueSecurityGuardTest {

    @Test
    public void shouldPass() throws Exception {
        // given
        SecurityGuard guard = new ParameterValueSecurityGuard();

        HttpServletRequest request = new MockHttpServletRequest();

        // when
        SecurityPass pass = guard.accept(request);

        // then
        assertTrue(pass.isAccepted());
        assertNull(pass.getGuardMessage());
    }

}
