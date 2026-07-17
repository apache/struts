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
package org.apache.struts2.result.xslt;

import junit.framework.TestCase;
import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.junit.StrutsTestCase;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.result.Result;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.ValueStack;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit test for {@link XSLTResult}.
 *
 */
public class XSLTResultTest extends StrutsTestCase {

    private XSLTResult result;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private MockServletContext servletContext;
    private MockActionInvocation mai;
    private ValueStack stack;

    public void testNoLocation() throws Exception {
        try {
            result.setParse(false);
            result.setStylesheetLocation(null);
            result.execute(mai);
            TestCase.fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNoFileFound() throws Exception {
        try {
            result.setParse(false);
            result.setStylesheetLocation("nofile.xsl");
            result.execute(mai);
            TestCase.fail("Should have thrown a TransformerException");
        } catch (TransformerException e) {
            // success
        }
    }

    public void testSimpleTransform() throws Exception {
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        TestCase.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        TestCase.assertTrue(out.contains("<result xmlns=\"http://www.w3.org/TR/xhtml1/strict\""));
    }

    public void testSimpleTransform5() throws Exception {
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest6.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        TestCase.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        TestCase.assertTrue(out.contains("<title>WebWork in Action</title>"));
        TestCase.assertTrue(out.contains("<author>Patrick and Jason</author>"));
        TestCase.assertTrue(out.contains("<editions><edition value=\"I\">I</edition><edition value=\"IV\">IV</edition></editions>"));
        TestCase.assertTrue(out.contains("<book><title/><author/><editions/></book>"));
        TestCase.assertTrue(out.contains("<title>XWork not in Action</title>"));
        TestCase.assertTrue(out.contains("<author>Superman</author>"));
        TestCase.assertTrue(out.contains("<editions><edition value=\"1234\">1234</edition><edition value=\"345\">345</edition><edition value=\"6667\">6667</edition></editions>"));
    }

    public void testSimpleTransformParse() throws Exception {
        result.setParse(true);
        result.setStylesheetLocation("${top.myLocation}");
        result.execute(mai);

        String out = response.getContentAsString();
        TestCase.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        TestCase.assertTrue(out.contains("<result xmlns=\"http://www.w3.org/TR/xhtml1/strict\""));
    }

    public void testTransform2() throws Exception {
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest2.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        TestCase.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        TestCase.assertTrue(out.contains("<html xmlns=\"http://www.w3.org/TR/xhtml1/strict\""));
        TestCase.assertTrue(out.contains("Hello Santa Claus how are you?"));
    }

    public void testTransform3() throws Exception {
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest3.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        TestCase.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        TestCase.assertTrue(out.contains("<html xmlns=\"http://www.w3.org/TR/xhtml1/strict\""));
        TestCase.assertTrue(out.contains("Hello Santa Claus how are you?"));
        TestCase.assertTrue(out.contains("WebWork in Action by Patrick and Jason"));
        TestCase.assertTrue(out.contains("XWork not in Action by Superman"));
    }

    public void testTransformWithBoolean() throws Exception {
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest5.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        TestCase.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        TestCase.assertTrue(out.contains("<html xmlns=\"http://www.w3.org/TR/xhtml1/strict\""));
        TestCase.assertTrue(out.contains("Hello Santa Claus how are you?"));
        TestCase.assertTrue(out.contains("You are active: true"));
    }

    public void testTransform4WithDocumentInclude() throws Exception {
        result = new XSLTResult(){
            protected URIResolver getURIResolver() {
                return new URIResolver() {
                    public Source resolve(String href, String base) throws TransformerException {
                        return new StreamSource(ClassLoaderUtil.getResourceAsStream(href, this.getClass()));
                    }

                };
            }

        };
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest4.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        TestCase.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        TestCase.assertTrue(out.contains("<validators>"));
    }

    public void testConcurrentGetTemplatesCompilesOnce() throws Exception {
        final AtomicInteger compileCount = new AtomicInteger(0);
        final CountDownLatch compileStarted = new CountDownLatch(1);
        final CountDownLatch releaseCompile = new CountDownLatch(1);

        result = new XSLTResult() {
            protected TransformerFactory createTransformerFactory() {
                final TransformerFactory delegate = super.createTransformerFactory();
                return new TransformerFactory() {
                    public Transformer newTransformer(Source source) throws TransformerConfigurationException {
                        return delegate.newTransformer(source);
                    }

                    public Transformer newTransformer() throws TransformerConfigurationException {
                        return delegate.newTransformer();
                    }

                    public Templates newTemplates(Source source) throws TransformerConfigurationException {
                        compileCount.incrementAndGet();
                        compileStarted.countDown();
                        try {
                            releaseCompile.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return delegate.newTemplates(source);
                    }

                    public Source getAssociatedStylesheet(Source source, String media, String title, String charset) throws TransformerConfigurationException {
                        return delegate.getAssociatedStylesheet(source, media, title, charset);
                    }

                    public void setURIResolver(URIResolver resolver) {
                        delegate.setURIResolver(resolver);
                    }

                    public URIResolver getURIResolver() {
                        return delegate.getURIResolver();
                    }

                    public void setFeature(String name, boolean value) throws TransformerConfigurationException {
                        delegate.setFeature(name, value);
                    }

                    public boolean getFeature(String name) {
                        return delegate.getFeature(name);
                    }

                    public void setAttribute(String name, Object value) {
                        delegate.setAttribute(name, value);
                    }

                    public Object getAttribute(String name) {
                        return delegate.getAttribute(name);
                    }

                    public void setErrorListener(ErrorListener listener) {
                        delegate.setErrorListener(listener);
                    }

                    public ErrorListener getErrorListener() {
                        return delegate.getErrorListener();
                    }
                };
            }
        };

        final String path = "XSLTResultTestDedup.xsl";
        final ActionContext context = ActionContext.getContext();
        final Templates[] compiled = new Templates[2];
        final Exception[] errors = new Exception[2];

        Thread first = new Thread(() -> {
            ActionContext.bind(context);
            try {
                compiled[0] = result.getTemplates(path);
            } catch (Exception e) {
                errors[0] = e;
            } finally {
                ActionContext.clear();
            }
        });
        first.start();

        TestCase.assertTrue("First thread should have started compiling",
                compileStarted.await(5, TimeUnit.SECONDS));

        Thread second = new Thread(() -> {
            ActionContext.bind(context);
            try {
                compiled[1] = result.getTemplates(path);
            } catch (Exception e) {
                errors[1] = e;
            } finally {
                ActionContext.clear();
            }
        });
        second.start();

        // Give the second thread time to reach the synchronized block and park on
        // the lock held by the first thread, so it hits the concurrent-miss race
        // rather than a plain cache hit.
        Thread.sleep(200);

        releaseCompile.countDown();

        first.join(5000);
        second.join(5000);

        TestCase.assertNull(errors[0]);
        TestCase.assertNull(errors[1]);
        TestCase.assertEquals("Templates should be compiled exactly once despite the concurrent miss",
                1, compileCount.get());
        TestCase.assertSame("Both callers should observe the same cached Templates instance",
                compiled[0], compiled[1]);
    }

    public void testNoCacheDoesNotPollutePersistentCache() throws Exception {
        final String path = "XSLTResultTestNoCachePollution.xsl";

        // First, a normal (cached) compile populates the shared static cache.
        result.setNoCache("false");
        Templates cached = result.getTemplates(path);
        TestCase.assertNotNull(cached);

        // A noCache=true caller must still get a fresh compile...
        result.setNoCache("true");
        Templates fresh = result.getTemplates(path);
        TestCase.assertNotNull(fresh);
        TestCase.assertNotSame("noCache=true should always recompile rather than reuse the cache",
                cached, fresh);

        // ...but must NOT overwrite the shared cache entry other, cached callers rely on.
        XSLTResult cachedCaller = new XSLTResult();
        cachedCaller.setNoCache("false");
        Templates stillCached = cachedCaller.getTemplates(path);
        TestCase.assertSame("A noCache=true call must not pollute the shared cache for cached callers",
                cached, stillCached);
    }
   
    public void testTransform4WithBadDocumentInclude() {
        result = new XSLTResult(){
            protected URIResolver getURIResolver() {
                return new URIResolver() {
                    public Source resolve(String href, String base) throws TransformerException {
                        return new StreamSource(ClassLoaderUtil.getResourceAsStream(href, this.getClass()));
                    }

                };
            }

        };
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest4.badinclude.xsl");
        try {
            result.execute(mai);
            TestCase.fail("Should have thrown an exception");
        } catch (Exception ex) {
            TestCase.assertEquals("Error transforming result", ex.getMessage());
        }
    }

    public void testTransformWithError() {
        result = new XSLTResult(){
            protected URIResolver getURIResolver() {
                return (href, base) -> {
                    throw new TransformerException("Some random error");
                };
            }
        };
        result.setStylesheetLocation("XSLTResultTest4.xsl");
        try {
            result.execute(mai);
            TestCase.fail("Should have thrown an exception");
        } catch (Exception ex) {
            TestCase.assertEquals("Error transforming result", ex.getMessage());
        }
    }

    public void testTransformWithBadCharacter() throws Exception {
        result = new XSLTResult();
        result.setStylesheetLocation("XSLTResultTest.bad.character.xsl");
        try {
            result.execute(mai);
            TestCase.fail("Should have thrown an exception");
        } catch (Exception ex) {
            TestCase.assertEquals("Error transforming result", ex.getMessage());
        }
    }

    public void testStatusCode() throws Exception {
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest.xsl");
        result.setStatus("302");
        result.execute(mai);

        String out = response.getContentAsString();

        TestCase.assertEquals(302, response.getStatus());
        TestCase.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        TestCase.assertTrue(out.contains("<result xmlns=\"http://www.w3.org/TR/xhtml1/strict\""));
    }

    public void testEncoding() throws Exception {
        result.setParse(false);
        result.setStylesheetLocation("XSLTResultTest.xsl");
        result.setEncoding("ISO-8859-1");
        result.execute(mai);

        String actual = response.getCharacterEncoding();

        TestCase.assertEquals(actual, "ISO-8859-1");
    }

    public void testPassingNullInvocation() throws Exception{
        Result result = new XSLTResult();
        try {
            result.execute(null);
            TestCase.fail("Exception should be thrown!");
        } catch (IllegalArgumentException e) {
            TestCase.assertEquals("Invocation cannot be null!", e.getMessage());
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servletContext = new MockServletContext();

        result = new XSLTResult();
        stack = ActionContext.getContext().getValueStack();

        MyAction action = new MyAction();

        mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setStack(stack);
        mai.setInvocationContext(ActionContext.getContext());
        stack.push(action);

        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, request);
        ActionContext.getContext().put(ServletActionContext.HTTP_RESPONSE, response);
        ActionContext.getContext().put(ServletActionContext.SERVLET_CONTEXT, servletContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        request = null;
        response = null;
        servletContext = null;
        result = null;
        stack = null;
        mai = null;
    }

    private class MyAction implements Action {

        public String execute() throws Exception {
            return SUCCESS;
        }

        public String getMyLocation() {
            return ("XSLTResultTest.xsl");
        }

        public String getUsername() {
            return "Santa Claus";
        }

        public boolean isActive() {
            return true;
        }

        public List<Book> getBooks() {
            List<Book> list = new ArrayList<>();
            list.add(new Book("WebWork in Action", "Patrick and Jason", Arrays.asList("I", "IV")));
            list.add(null);
            list.add(new Book("XWork not in Action", "Superman", Arrays.asList("1234", "345", "6667")));
            return list;
        }

    }

    public static class Book {

        private final String title;
        private final String author;
        private final List<String> editions;

        public Book(String title, String author, List<String> editions) {
            this.title = title;
            this.author = author;
            this.editions = editions;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public List<String> getEditions() {
            return editions;
        }
    }
}
