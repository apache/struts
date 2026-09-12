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
package org.apache.struts2.rest;

import com.mockobjects.dynamic.AnyConstraintMatcher;
import com.mockobjects.dynamic.Mock;
import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionSupport;
import junit.framework.TestCase;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.rest.handler.ContentTypeHandler;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizer;
import org.springframework.mock.web.MockHttpServletRequest;

public class ContentTypeInterceptorTest extends TestCase {

    public void testRequestWithoutEncoding() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);

        ActionSupport action = new ActionSupport();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        });
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(new byte[] {1});

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
        mockContentTypeHandler.verify();
    }

    public void testRequestWithEncodingLatin1() throws Exception {
        final Charset charset = StandardCharsets.ISO_8859_1;

        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);

        ActionSupport action = new ActionSupport();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return "caf\u00e9".equals(readFully((Reader) args[1]));
            }
        });
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("caf\u00e9".getBytes(charset));
        request.setCharacterEncoding(charset.name());

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
        mockContentTypeHandler.verify();
    }

    public void testRequestWithEncodingUtf8() throws Exception {
        final Charset charset = StandardCharsets.UTF_8;

        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);

        ActionSupport action = new ActionSupport();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return "caf\u00e9".equals(readFully((Reader) args[1]));
            }
        });
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("caf\u00e9".getBytes(charset));
        request.setCharacterEncoding(charset.name());

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
        mockContentTypeHandler.verify();
    }

    public void testRequireAnnotationsEnabled_twoPhaseDeserialization() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> false);
        interceptor.setRequireAnnotations(Boolean.TRUE.toString());

        ActionSupport action = new ActionSupport();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        });
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(new byte[] {1});

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
        mockContentTypeHandler.verify();
    }

    public void testRequireAnnotationsEnabled_selectiveFilter() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> "name".equals(parameterName));
        interceptor.setRequireAnnotations(Boolean.TRUE.toString());

        ActionSupport action = new ActionSupport();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        });
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(new byte[] {1});

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
        mockContentTypeHandler.verify();
    }

    public void testBodyOverLimitIsRejectedBeforeActionRuns() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength("8");

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", new ActionSupport());
        interceptor.setContentTypeHandlerSelector(selectorReturning(readingHandler()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("123456789".getBytes(StandardCharsets.US_ASCII));

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        try {
            interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
            fail("expected " + RequestBodyTooLargeException.class.getSimpleName());
        } catch (RequestBodyTooLargeException expected) {
            assertTrue(expected.getMessage().contains(RestConstants.REST_CONTENT_MAX_LENGTH));
        }
        mockActionInvocation.verify();
    }

    public void testBodyAtLimitIsPassedToHandlerInFull() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength("8");

        assertEquals("12345678", interceptAndCaptureBody(interceptor, new MockHttpServletRequest(),
                "12345678".getBytes(StandardCharsets.US_ASCII)));
    }

    public void testBodyOverLimitIsNotReadToTheEnd() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength("8");

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", new ActionSupport());
        interceptor.setContentTypeHandlerSelector(selectorReturning(readingHandler()));

        byte[] body = new byte[1024 * 1024];
        Arrays.fill(body, (byte) 'x');
        CountingRequest request = new CountingRequest();
        request.setContent(body);

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        try {
            interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
            fail("expected " + RequestBodyTooLargeException.class.getSimpleName());
        } catch (RequestBodyTooLargeException expected) {
            assertTrue("read " + request.bytesRead + " of " + body.length + " bytes",
                    request.bytesRead < body.length);
        }
    }

    public void testNonNumericMaxLengthKeepsDefault() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength("lots");

        byte[] body = new byte[64 * 1024];
        Arrays.fill(body, (byte) 'x');
        assertEquals(body.length, interceptAndCaptureBody(interceptor, new MockHttpServletRequest(), body).length());
    }

    public void testMaxLengthBelowOneKeepsDefault() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength("0");

        assertEquals("abc", interceptAndCaptureBody(interceptor, new MockHttpServletRequest(),
                "abc".getBytes(StandardCharsets.US_ASCII)));
    }

    public void testHandlerThatIgnoresTheReaderLeavesBodyUnread() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", new ActionSupport());
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        });
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        CountingRequest request = new CountingRequest();
        request.setContent("raw body the action may want to read itself".getBytes(StandardCharsets.US_ASCII));

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        assertEquals(0, request.bytesRead);
        mockActionInvocation.verify();
    }

    public void testBlankMaxLengthKeepsDefault() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength(" ");

        byte[] body = new byte[64 * 1024];
        Arrays.fill(body, (byte) 'x');
        assertEquals(body.length, interceptAndCaptureBody(interceptor, new MockHttpServletRequest(), body).length());
    }

    public void testHandlerThatSwallowsTheLimitIsStillRejected() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength("8");

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", new ActionSupport());
        Mock swallowingHandler = new Mock(ContentTypeHandler.class);
        swallowingHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                try {
                    readFully((Reader) args[1]);
                } catch (RuntimeException swallowed) {
                    // a handler that hides the reader's failure must not let the action run
                }
                return true;
            }
        });
        interceptor.setContentTypeHandlerSelector(selectorReturning((ContentTypeHandler) swallowingHandler.proxy()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("123456789".getBytes(StandardCharsets.US_ASCII));
        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        try {
            interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
            fail("expected " + RequestBodyTooLargeException.class.getSimpleName());
        } catch (RequestBodyTooLargeException expected) {
            // action never invoked: no "invoke" expectation was set
        }
        mockActionInvocation.verify();
    }

    public void testHandlerFailureUnderTheLimitPropagatesUnchanged() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength("8");

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", new ActionSupport());
        IllegalStateException handlerFailure = new IllegalStateException("malformed");
        Mock failingHandler = new Mock(ContentTypeHandler.class);
        failingHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                throw handlerFailure;
            }
        });
        interceptor.setContentTypeHandlerSelector(selectorReturning((ContentTypeHandler) failingHandler.proxy()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("abc".getBytes(StandardCharsets.US_ASCII));
        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        try {
            interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
            fail("expected the handler's own exception");
        } catch (IllegalStateException e) {
            assertSame(handlerFailure, e);
        }
    }

    public void testSkippingPastTheLimitIsRejected() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setMaxLength("8");

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", new ActionSupport());
        Mock skippingHandler = new Mock(ContentTypeHandler.class);
        skippingHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                try {
                    ((Reader) args[1]).skip(Long.MAX_VALUE);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
                return true;
            }
        });
        interceptor.setContentTypeHandlerSelector(selectorReturning((ContentTypeHandler) skippingHandler.proxy()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("123456789".getBytes(StandardCharsets.US_ASCII));
        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        try {
            interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
            fail("expected " + RequestBodyTooLargeException.class.getSimpleName());
        } catch (RequestBodyTooLargeException expected) {
            // skipped input counts against the limit like read input
        }
        mockActionInvocation.verify();
    }

    /**
     * A handler that reads the body the way the real ones do, and surfaces the reader's failure in its
     * own exception type as Jackson, XStream and Juneau each do.
     */
    private static ContentTypeHandler readingHandler() {
        Mock handler = new Mock(ContentTypeHandler.class);
        handler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                readFully((Reader) args[1]);
                return true;
            }
        });
        return (ContentTypeHandler) handler.proxy();
    }

    private static ContentTypeHandlerManager selectorReturning(ContentTypeHandler handler) {
        Mock selector = new Mock(ContentTypeHandlerManager.class);
        selector.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, handler);
        return (ContentTypeHandlerManager) selector.proxy();
    }

    private static String interceptAndCaptureBody(ContentTypeInterceptor interceptor, MockHttpServletRequest request,
                                                  byte[] body) throws Exception {
        String[] captured = new String[1];
        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", new ActionSupport());
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                captured[0] = readFully((Reader) args[1]);
                return true;
            }
        });
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        request.setContent(body);
        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandler.verify();
        mockActionInvocation.verify();
        return captured[0];
    }

    /** Counts the bytes the interceptor actually pulls from the request stream. */
    private static final class CountingRequest extends MockHttpServletRequest {
        long bytesRead;

        @Override
        public ServletInputStream getInputStream() {
            ServletInputStream delegate = super.getInputStream();
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    int b = delegate.read();
                    if (b != -1) {
                        bytesRead++;
                    }
                    return b;
                }

                @Override
                public int read(byte[] buf, int off, int len) throws IOException {
                    int n = delegate.read(buf, off, len);
                    if (n > 0) {
                        bytesRead += n;
                    }
                    return n;
                }

                @Override
                public boolean isFinished() {
                    return delegate.isFinished();
                }

                @Override
                public boolean isReady() {
                    return delegate.isReady();
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    delegate.setReadListener(readListener);
                }
            };
        }
    }

    private static String readFully(Reader reader) {
        try {
            StringBuilder out = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                out.append((char) c);
            }
            return out.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
