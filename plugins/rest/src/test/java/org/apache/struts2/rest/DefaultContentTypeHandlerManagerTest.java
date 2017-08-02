package org.apache.struts2.rest;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;
import org.apache.struts2.rest.handler.AbstractContentTypeHandler;
import org.apache.struts2.rest.handler.ContentTypeHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public class DefaultContentTypeHandlerManagerTest extends XWorkTestCase {

    public void testObtainingHandlerForRequestWithEncoding() throws Exception {
        // given
        DefaultContentTypeHandlerManager handlerManager = new DefaultContentTypeHandlerManager();
        handlerManager.setContainer(new DummyContainer("application/json;charset=UTF-8", null));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json;charset=UTF-8");

        // when
        ContentTypeHandler handler = handlerManager.getHandlerForRequest(request);

        // then
        assertNotNull(handler);
        assertEquals("application/json;charset=UTF-8", handler.getContentType());
    }

    public void testObtainingHandlerForRequestWithoutEncoding() throws Exception {
        // given
        DefaultContentTypeHandlerManager handlerManager = new DefaultContentTypeHandlerManager();
        handlerManager.setContainer(new DummyContainer("application/json", null));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json;charset=UTF-8");

        // when
        ContentTypeHandler handler = handlerManager.getHandlerForRequest(request);

        // then
        assertNotNull(handler);
        assertEquals("application/json", handler.getContentType());
    }

    public void testObtainingHandlerForRequestByExtension() throws Exception {
        // given
        DefaultContentTypeHandlerManager handlerManager = new DefaultContentTypeHandlerManager();
        handlerManager.setContainer(new DummyContainer("text/html", "json"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json;charset=UTF-8");
        request.setRequestURI("/index.json");

        // when
        ContentTypeHandler handler = handlerManager.getHandlerForRequest(request);

        // then
        assertNotNull(handler);
        assertEquals("text/html", handler.getContentType());
        assertEquals("json", handler.getExtension());
    }

    public void testObtainingHandlerForRequestByContentType() throws Exception {
        // given
        DefaultContentTypeHandlerManager handlerManager = new DefaultContentTypeHandlerManager();
        handlerManager.setContainer(new DummyContainer("application/json", ""));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json;charset=UTF-8");
        request.setRequestURI("/index");

        // when
        ContentTypeHandler handler = handlerManager.getHandlerForRequest(request);

        // then
        assertNotNull(handler);
        assertEquals("application/json", handler.getContentType());
        assertEquals("", handler.getExtension());
    }

    public void testObtainingHandlerForResponseByAcceptHeader() throws Exception {

        // given
        final DefaultContentTypeHandlerManager handlerManager = new DefaultContentTypeHandlerManager();
        handlerManager.setContainer(new DummyContainer("application/json", "json"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json;charset=UTF-8");
        request.addHeader("accept","application/json;charset=UTF-8");
        request.setRequestURI("/index");

        final MockHttpServletResponse response = new MockHttpServletResponse();
        response.setContentType("application/json;charset=UTF-8");

        // when
        ContentTypeHandler handler = handlerManager.getHandlerForResponse(request,response);

        // then
        assertNotNull(handler);
        assertEquals("application/json", handler.getContentType());
        assertEquals("json", handler.getExtension());
    }

}

class DummyContainer implements Container {

    private ContentTypeHandler handler;

    DummyContainer(final String contentType, final String extension) {
        handler = new AbstractContentTypeHandler() {

            public void toObject(ActionInvocation invocation, Reader in, Object target) throws IOException {

            }

            public String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer stream) throws IOException {
                return null;
            }

            public String getContentType() {
                return contentType;
            }

            public String getExtension() {
                return extension;
            }
        };
    }

    public void inject(Object o) {

    }

    public <T> T inject(Class<T> implementation) {
        return null;
    }

    public <T> T getInstance(Class<T> type, String name) {
        if (name.startsWith(DefaultContentTypeHandlerManager.STRUTS_REST_HANDLER_OVERRIDE_PREFIX)) {
            return null;
        }
        return (T) handler;
    }

    public <T> T getInstance(Class<T> type) {
        return null;
    }

    public Set<String> getInstanceNames(Class<?> type) {
        Set<String> handlers = new HashSet<String>();
        handlers.add("handler");
        return handlers;
    }

    public void setScopeStrategy(Scope.Strategy scopeStrategy) {

    }

    public void removeScopeStrategy() {

    }
}
