package org.apache.struts2.interceptor.httpmethod;

import junit.framework.TestCase;

public class HttpMethodTest extends TestCase {

    public void testConvertHttpRequestMethod() throws Exception {
        // given
        String httpRequestMethod = "post";

        // when
        HttpMethod httpMethod = HttpMethod.parse(httpRequestMethod);

        // then
        assertEquals(HttpMethod.POST, httpMethod);
    }

    public void testValueOfThrowsException() throws Exception {
        // given
        String httpRequestMethod = "post";

        // when
        IllegalArgumentException expected = null;
        try {
            HttpMethod.valueOf(httpRequestMethod);
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // then
        assertNotNull(expected);
        assertEquals(expected.getClass(), IllegalArgumentException.class);
    }

}
