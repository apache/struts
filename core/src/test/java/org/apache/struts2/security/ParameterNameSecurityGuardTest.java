package org.apache.struts2.security;

import com.mockobjects.servlet.MockHttpServletRequest;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ParameterNameSecurityGuardTest {

    @Test
    public void shouldPass() throws Exception {
        // given
        SecurityGuard guard = new ParameterNameSecurityGuard();

        HttpServletRequest request = new MockHttpServletRequest();

        // when
        SecurityPass pass = guard.accept(request);

        // then
        assertTrue(pass.isAccepted());
        assertNull(pass.getGuardMessage());
    }

}
