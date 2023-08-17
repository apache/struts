package org.apache.struts2.dispatcher;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestMapTest {

    @Test
    public void shouldAccessRequestAttributes() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("attr", "value");

        // when
        RequestMap rm = new RequestMap(request);
        Object value = rm.get("attr");

        // then
        assertEquals("value", value);
    }

    @Test
    public void shouldReturnNullIfKeyIsNull() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();

        // when
        RequestMap rm = new RequestMap(request);
        Object value = rm.get(null);

        // then
        assertNull(value);
    }

    @Test
    public void shouldRemoveAttributeFromRequest() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("attr", "value");

        // when
        RequestMap rm = new RequestMap(request);
        Object value = rm.remove("attr");

        // then
        assertEquals("value", value);
        assertNull(request.getAttribute("attr"));
    }

    @Test
    public void shouldClearAttributes() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("attr", "value");

        // when
        RequestMap rm = new RequestMap(request);
        Object value = rm.get("attr");

        // then
        assertEquals("value", value);

        // when
        rm.clear();

        // then
        assertNull(request.getAttribute("attr"));
    }

}
