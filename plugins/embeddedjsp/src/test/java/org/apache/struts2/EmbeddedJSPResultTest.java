/*
 * $Id$
 *
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
package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.OgnlTextParser;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.jasper.runtime.InstanceHelper;
import org.apache.struts2.views.util.DefaultUrlHelper;
import org.apache.struts2.views.util.UrlHelper;
import org.apache.tomcat.InstanceManager;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class EmbeddedJSPResultTest extends TestCase {
    private HttpServletRequest request;
    private MockHttpServletResponse response;
    private MockServletContext context;
    private MockServletConfig config;
    private EmbeddedJSPResult result;

    public void testScriptlet() throws Exception {
        result.setLocation("org/apache/struts2/scriptlet.jsp");
        result.execute(null);

        assertEquals("Saynotoscriptlets", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testEmbedded() throws Exception {
        //the jsp is inside jsps.jar
        result.setLocation("dir/all.jsp");
        result.execute(null);

        assertEquals("helloJGWhoamI?XXXXXXXXXXXYThissessionisnotsecure.", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testFilesAreReadOnlyOnce() throws Exception {
        //make sure that files are not read multiple times
        String jsp = "org/apache/struts2/dont-use.jsp";

        CountingClassLoaderInterface classLoaderInterface = new CountingClassLoaderInterface(this.getClass().getClassLoader());
        context.setAttribute(ClassLoaderInterface.CLASS_LOADER_INTERFACE, classLoaderInterface);
        result.setLocation(jsp);

        result.execute(null);
        Integer counter0 = classLoaderInterface.counters.get(jsp);
        assertNotNull(counter0);

        result.execute(null);
        Integer counter1 = classLoaderInterface.counters.get(jsp);
        assertNotNull(counter1);

        assertEquals(counter0, counter1);
    }

    public void testEmbeddedAbsolutePath() throws Exception {
        //the jsp is inside jsps.jar
        result.setLocation("/dir/all.jsp");
        result.execute(null);

        assertEquals("helloJGWhoamI?XXXXXXXXXXXYThissessionisnotsecure.", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testSimple() throws Exception {
        result.setLocation("org/apache/struts2/simple0.jsp");
        result.execute(null);

        assertEquals("hello", response.getContentAsString().trim());
    }

    //ok i give up..i don't know why this doesn't work from maven
   /* public void testKeyInContext() throws Exception {
        result.setLocation("org/apache/struts2/scriptlet.jsp");
        result.execute(null);

        String key = JspApplicationContextImpl.class.getName() + "@" + this.getClass().getClassLoader().hashCode();
        assertNotNull(context.getAttribute(key));
    }*/

    public void testEL() throws Exception {
        result.setLocation("org/apache/struts2/el.jsp");
        result.execute(null);

        assertEquals("somethingelseText", response.getContentAsString().trim());
    }

    public void testAbsolutePath() throws Exception {
        result.setLocation("/org/apache/struts2/simple0.jsp");
        result.execute(null);

        assertEquals("hello", response.getContentAsString().trim());
    }

    public void testTag0() throws Exception {
        result.setLocation("org/apache/struts2/tag0.jsp");
        result.execute(null);

        assertEquals("Thissessionisnotsecure.OtherText", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testIncludeSimple() throws Exception {
        result.setLocation("org/apache/struts2/includes0.jsp");
        result.execute(null);

        assertEquals("helloTest", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testIncludeSimpleWithDirective() throws Exception {
        result.setLocation("org/apache/struts2/includes3.jsp");
        result.execute(null);

        assertEquals("helloTest", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testIncludeWithSubdir() throws Exception {
        result.setLocation("org/apache/struts2/includes1.jsp");
        result.execute(null);

        assertEquals("subTest", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testIncludeWithParam() throws Exception {
        result.setLocation("org/apache/struts2/includes2.jsp");
        result.execute(null);

        assertEquals("JGTest", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testBroken0() throws Exception {
        try {
            result.setLocation("org/apache/struts2/broken0.jsp");
            result.execute(null);
            fail("should have failed with broken jsp");
        } catch (IllegalStateException ex) {
            //ok
        }
    }

    public void testJSTL() throws Exception {
        result.setLocation("org/apache/struts2/jstl.jsp");
        result.execute(null);

        assertEquals("XXXXXXXXXXXY", StringUtils.deleteWhitespace(response.getContentAsString()));
    }


    public void testCachedInstances() throws InterruptedException {
        ServletCache cache = new ServletCache();
        Servlet servlet1 = cache.get("org/apache/struts2/simple0.jsp");
        Servlet servlet2 = cache.get("org/apache/struts2/simple0.jsp");

        assertSame(servlet1, servlet2);
    }

    public void testCacheInstanceWithManyThreads() throws BrokenBarrierException, InterruptedException {
        //start a bunch of thread at the same time using CyclicBarrier and hit the cache
        //then wait for all the threads to end and check that they all got a reference to the same object
        //and the cache size should be 1

        DummyServletCache cache = new DummyServletCache();
        int numThreads = 70;

        CyclicBarrier startBarrier = new CyclicBarrier(numThreads + 1);
        CyclicBarrier endBarrier = new CyclicBarrier(numThreads + 1);

        List<ServletGetRunnable> runnables = new ArrayList<>(numThreads);

        //create the threads
        for (int i = 0; i < numThreads; i++) {
            ServletGetRunnable runnable = new ServletGetRunnable(cache, startBarrier, endBarrier, ActionContext.getContext());
            Thread thread = new Thread(runnable);
            runnables.add(runnable);
            thread.start();
        }

        startBarrier.await();
        endBarrier.await();
        Object servlet = cache.get("org/apache/struts2/simple0.jsp");
        assertEquals(1, cache.size());

        for (ServletGetRunnable runnable : runnables) {
            assertSame(servlet, runnable.getObject());
        }
    }

    public void testBeans() throws Exception {
        result.setLocation("org/apache/struts2/beans.jsp");
        result.execute(null);

        assertEquals("WhoamI?", StringUtils.deleteWhitespace(response.getContentAsString()));
    }

    public void testNotURLClassLoader() throws Exception {
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        NotURLClassLoader loader = new NotURLClassLoader(parentClassLoader);
        Thread.currentThread().setContextClassLoader(loader);

        try {
            result.setLocation("org/apache/struts2/tag0.jsp");
            result.execute(null);

            assertEquals("Thissessionisnotsecure.OtherText", StringUtils.deleteWhitespace(response.getContentAsString()));
        } finally {
            Thread.currentThread().setContextClassLoader(parentClassLoader);
        }
    }

    public void testComplex() throws Exception {
        result.setLocation("org/apache/struts2/complex0.jsp");
        result.execute(null);

        String responseString = response.getContentAsString();
        assertNotNull("result is null?", responseString);
        int titleIndex = responseString.indexOf("<title>Struts2 Embedded JSP Plugin - Complex Test Page</title>");
        int responseLength = responseString.length();
        int testValue1Index = responseString.indexOf("testvalue1 set/if worked.");
        int testValue5Index = responseString.indexOf("testvalue5 set/if worked.");
        int lastGroupIndex = responseString.indexOf("End include tests<br/>");
        int lastHtmlIndex = responseString.indexOf("</html>");
        assertTrue("Did not find title index (" + titleIndex + ") ?", titleIndex > 0);
        assertTrue("Test value 1 not present or index (" + testValue1Index + ") not > title index (" + titleIndex + ") ?",
            testValue1Index > titleIndex);
        assertTrue("Test value 5 not present or index (" + testValue5Index + ") not > test value 1 index (" + testValue1Index + ") ?",
            testValue5Index > testValue1Index);
        assertTrue("Last group index not present or index (" + lastGroupIndex + ") not > test value 5 index (" + testValue5Index + ") ?",
            lastGroupIndex > testValue5Index);
        assertTrue("Last html index not present or index (" + lastHtmlIndex + ") not > last group index (" + lastGroupIndex + ") ?",
            lastHtmlIndex > lastGroupIndex);
        // complex0.jsp length 3439 in Windows and estimated 3221 in Linux/Unix (with 218 lines, Windows has around 218 additional
        //   characters (crlf vs. lf).  Test length larger than the min(Windows,Linux)), rounded down to the nearest 100.
        assertTrue("Response length (" + responseLength + ") not at least length: 3200 ?", responseLength > 3200);
    }

    public void testInstanceHelper() throws Exception {
        InstanceManager instanceManagerServlet = InstanceHelper.getServletInstanceManager(config);
        InstanceManager instanceManagerClassLoader = InstanceHelper.getClassLoaderInstanceManager(context.getClassLoader());
        assertNotNull("instanceManager (servlet) is null ?", instanceManagerServlet);
        assertNotNull("instanceManager (classloader) is null ?", instanceManagerClassLoader);
        assertEquals("instanceManager (servlet) is not equal to instanceManager (classloader) ?", instanceManagerServlet, instanceManagerClassLoader);
        final Double instanceDouble = (double) 0;
        final Long instanceLong = 0L;
        final Object instanceObject = new Object();
        final String instanceString = "test string";
        final MockHttpServletRequest intanceMockHttpServletRequest = new MockHttpServletRequest();
        intanceMockHttpServletRequest.setContextPath("context path");
        InstanceHelper.postConstruct(instanceManagerServlet, instanceDouble);
        InstanceHelper.postConstruct(instanceManagerServlet, instanceLong);
        InstanceHelper.postConstruct(instanceManagerServlet, instanceObject);
        InstanceHelper.postConstruct(instanceManagerServlet, instanceString);
        InstanceHelper.postConstruct(instanceManagerServlet, intanceMockHttpServletRequest);
        assertEquals("test string value changed after postConstruct ?", instanceString, "test string");
        assertEquals("mock servlet request context path value changed after postConstruct ?",
            intanceMockHttpServletRequest.getContextPath(), "context path");
        InstanceHelper.preDestroy(instanceManagerServlet, instanceDouble);
        InstanceHelper.preDestroy(instanceManagerServlet, instanceLong);
        InstanceHelper.preDestroy(instanceManagerServlet, instanceObject);
        InstanceHelper.preDestroy(instanceManagerServlet, instanceString);
        InstanceHelper.preDestroy(instanceManagerServlet, intanceMockHttpServletRequest);
        assertEquals("test string value changed after preDestroy ?", instanceString, "test string");
        assertEquals("mock servlet request context path value changed after preDestroy ?",
            intanceMockHttpServletRequest.getContextPath(), "context path");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        result = new EmbeddedJSPResult();

        request = EasyMock.createNiceMock(HttpServletRequest.class);
        response = new MockHttpServletResponse();
        context = new MockServletContext();
        config = new MockServletConfig(context);

        final Map params = new HashMap();

        HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        EasyMock.replay(session);

        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(request.getParameterMap()).andReturn(params).anyTimes();
        EasyMock.expect(request.getParameter("username")).andAnswer(() -> ActionContext.getContext().getParameters().get("username").getValue());
        EasyMock.expect(request.getAttribute("something")).andReturn("somethingelse").anyTimes();

        EasyMock.replay(request);

        //mock value stack
        ValueStack valueStack = EasyMock.createNiceMock(ValueStack.class);
        EasyMock.expect(valueStack.getActionContext()).andReturn(ActionContext.getContext()).anyTimes();
        EasyMock.replay(valueStack);

        //mock converter
        XWorkConverter converter = new DummyConverter();

        DefaultFileManager fileManager = new DefaultFileManager();
        fileManager.setReloadingConfigs(false);

        //mock container
        Container container = EasyMock.createNiceMock(Container.class);
        EasyMock.expect(container.getInstance(XWorkConverter.class)).andReturn(converter).anyTimes();
        TextParser parser = new OgnlTextParser();
        EasyMock.expect(container.getInstance(TextParser.class)).andReturn(parser).anyTimes();
        EasyMock.expect(container.getInstanceNames(FileManager.class)).andReturn(new HashSet<>()).anyTimes();
        EasyMock.expect(container.getInstance(FileManager.class)).andReturn(fileManager).anyTimes();

        UrlHelper urlHelper = new DefaultUrlHelper();
        EasyMock.expect(container.getInstance(UrlHelper.class)).andReturn(urlHelper).anyTimes();
        FileManagerFactory fileManagerFactory = new DummyFileManagerFactory();
        EasyMock.expect(container.getInstance(FileManagerFactory.class)).andReturn(fileManagerFactory).anyTimes();

        EasyMock.replay(container);

        ActionContext.of(new HashMap<>())
            .withParameters(HttpParameters.create(params).build())
            .withServletRequest(request)
            .withServletResponse(response)
            .withServletContext(context)
            .withContainer(container)
            .withValueStack(valueStack)
            .bind();
    }

}

//converter has a protected default constructor...meh
class DummyConverter extends XWorkConverter {

}

class DummyFileManagerFactory implements FileManagerFactory {

    public void setReloadingConfigs(String reloadingConfigs) {
    }

    public FileManager getFileManager() {
        return new DefaultFileManager();
    }

}

class DummyServletCache extends ServletCache {
    public int size() {
        return cache.size();
    }
}

class ServletGetRunnable implements Runnable {
    private ServletCache servletCache;
    private Object object;
    private CyclicBarrier startBarrier;
    private ActionContext actionContext;
    private CyclicBarrier endBarrier;

    ServletGetRunnable(ServletCache servletCache, CyclicBarrier startBarrier, CyclicBarrier endBarrier, ActionContext actionContext) {
        this.servletCache = servletCache;
        this.startBarrier = startBarrier;
        this.endBarrier = endBarrier;
        this.actionContext = actionContext;
    }

    public void run() {
        actionContext = ActionContext.bind(actionContext);
        //wait to start all threads at once..or try at least
        try {
            startBarrier.await();
            object = servletCache.get("org/apache/struts2/simple0.jsp");

            for (int i = 0; i < 10; i++) {
                Object object2 = servletCache.get("org/apache/struts2/simple0.jsp");
                if (object2 != object)
                    throw new RuntimeException("got different object from cache");
            }

            endBarrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getObject() {
        return object;
    }
}

class CountingClassLoaderInterface extends ClassLoaderInterfaceDelegate {
    public Map<String, Integer> counters = new HashMap<>();

    public CountingClassLoaderInterface(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        Integer counter = counters.get(name);
        counter = counter == null ? 1 : counter + 1;
        counters.put(name, counter);

        return super.getResourceAsStream(name);
    }
}

class NotURLClassLoader extends ClassLoader {

    NotURLClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }
}
