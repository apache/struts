package org.apache.struts2.components;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.views.jsp.AbstractTagTest;

/**
 * Verifies correct operation of parameter merging.
 * 
 * Contributed by: Daniel Uribe
 */
public class URLTest extends AbstractTagTest {
    public void testIncludeGetDuplicateRequestParams() throws Exception {
        String body = "";

        Map parameterMap = new HashMap();
        parameterMap.put("param", new String[] { "1", "2", "3" });

        request.setQueryString("param=1&param=2&param=3");
        request.setScheme("http");
        request.setParameterMap(parameterMap);
        URL url = new URL(stack, request, response);
        url.setIncludeParams(URL.GET);
        url.setIncludeContext(false);
        url.setValue("myAction.action");
        url.setNamespace("");

        url.start(writer);
        url.end(writer, body);

        assertEquals("myAction.action?param=1&amp;param=2&amp;param=3",
            writer.toString());
    }

    public void testIncludeAllDuplicateRequestParams() throws Exception {
        String body = "";

        Map parameterMap = new HashMap();
        parameterMap.put("param", new String[] { "1", "2", "3" });

        request.setQueryString("param=1&param=2&param=3");
        request.setScheme("http");
        request.setParameterMap(parameterMap);
        URL url = new URL(stack, request, response);
        url.setIncludeParams(URL.ALL);
        url.setIncludeContext(false);
        url.setValue("myAction.action");
        url.setNamespace("");

        url.start(writer);
        url.end(writer, body);

        assertEquals("myAction.action?param=1&amp;param=2&amp;param=3",
            writer.toString());
    }
}
