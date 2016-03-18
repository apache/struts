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

package org.apache.struts2.views.jsp;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.inject.Container;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.components.URL;
import org.apache.struts2.dispatcher.ApplicationMap;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for {@link URLTag}.
 *
 */
public class URLTagTest extends AbstractUITagTest {

    private URLTag tag;


    /**
     * To test priority of parameter passed in to url component though
     * various way
     *  - current request url
     *  - tag's value attribute
     *  - tag's nested param tag
     *
     * id1
     * ===
     * - found in current request url
     * - found in tag's value attribute
     * - found in tag's param tag
     * CONCLUSION: tag's param tag takes precedence (paramId1)
     *
     * id2
     * ===
     * - found in current request url
     * - found in tag's value attribute
     * CONCLUSION: tag's value attribute take precedence (tagId2)
     *
     * urlParam1
     * =========
     * - found in current request url
     * CONCLUSION: param in current request url will be used (urlValue1)
     *
     * urlParam2
     * =========
     * - found in current request url
     * CONCLUSION: param in current request url will be used. (urlValue2)
     *
     * tagId
     * =====
     * - found in tag's value attribute
     * CONCLUSION: param in tag's value attribute wil; be used. (tagValue)
     *
     * param1
     * ======
     * - found in nested param tag
     * CONCLUSION: param in nested param tag will be used. (param1value)
     *
     * param2
     * ======
     * - found in nested param tag
     * CONCLUSION: param in nested param tag will be used. (param2value)
     */
    public void testParametersPriority() throws Exception {
        request.setQueryString("id1=urlId1&id2=urlId2&urlParam1=urlValue1&urlParam2=urlValue2");

        tag.setValue("testAction.action?id1=tagId1&id2=tagId2&tagId=tagValue");

        ParamTag param1 = new ParamTag();
        param1.setPageContext(pageContext);
        param1.setName("param1");
        param1.setValue("%{'param1value'}");

        ParamTag param2 = new ParamTag();
        param2.setPageContext(pageContext);
        param2.setName("param2");
        param2.setValue("%{'param2value'}");

        ParamTag param3 = new ParamTag();
        param3.setPageContext(pageContext);
        param3.setName("id1");
        param3.setValue("%{'paramId1'}");


        tag.doStartTag();
        param1.doStartTag();
        param1.doEndTag();
        param2.doStartTag();
        param2.doEndTag();
        param3.doStartTag();
        param3.doEndTag();

        URL url = (URL) tag.getComponent();
        Map parameters = url.getParameters();


        assertNotNull(parameters);
        assertEquals(parameters.size(), 7);
        assertEquals(parameters.get("id1"), "paramId1");
        assertEquals(parameters.get("id2"), "tagId2");
        assertEquals(parameters.get("urlParam1"), "urlValue1");
        assertEquals(parameters.get("urlParam2"), "urlValue2");
        assertEquals(parameters.get("tagId"), "tagValue");
        assertEquals(parameters.get("param1"), "param1value");
        assertEquals(parameters.get("param2"), "param2value");
    }

    /**
     * Use Iterable values as the value of the param tags
     * @throws Exception
     */
    public void testIterableParameters() throws Exception {
        tag.setValue("/TestAction.action?p0=z");
        
        tag.doStartTag();
        //Iterable
        List<ValueHolder> list = new ArrayList<ValueHolder>();
        list.add(new ValueHolder("a"));
        list.add(new ValueHolder("b"));
        tag.component.addParameter("p1", list);
        
        //String[]
        tag.component.addParameter("p2", new String[] { "d", "e" });
        //ValueHolder[]
        tag.component.addParameter("p3", new ValueHolder[] {
                new ValueHolder("f"), new ValueHolder("g") });
        
        tag.doEndTag();
        
        assertEquals("/TestAction.action?p0=z&amp;p1=a&amp;p1=b&amp;p2=d&amp;p2=e&amp;p3=f&amp;p3=g", writer.toString());
    }
    
    /**
     * To test priority of parameter passed in to url component though
     * various way, with includeParams="NONE"
     *  - current request url
     *  - tag's value attribute
     *  - tag's nested param tag
     *
     *  In this case only parameters from the tag itself is taken into account.
     *  Those from request will not count, only those in tag's value attribute
     *  and nested param tag.
     *
     * @throws Exception
     */
    public void testParametersPriorityWithIncludeParamsAsNONE() throws Exception {
        request.setQueryString("id1=urlId1&id2=urlId2&urlParam1=urlValue1&urlParam2=urlValue2");

        tag.setValue("testAction.action?id1=tagId1&id2=tagId2&tagId=tagValue");
        tag.setIncludeParams("NONE");

        ParamTag param1 = new ParamTag();
        param1.setPageContext(pageContext);
        param1.setName("param1");
        param1.setValue("%{'param1value'}");

        ParamTag param2 = new ParamTag();
        param2.setPageContext(pageContext);
        param2.setName("param2");
        param2.setValue("%{'param2value'}");

        ParamTag param3 = new ParamTag();
        param3.setPageContext(pageContext);
        param3.setName("id1");
        param3.setValue("%{'paramId1'}");


        tag.doStartTag();
        param1.doStartTag();
        param1.doEndTag();
        param2.doStartTag();
        param2.doEndTag();
        param3.doStartTag();
        param3.doEndTag();

        URL url = (URL) tag.getComponent();
        Map parameters = url.getParameters();

        assertEquals(parameters.size(), 5);
        assertEquals(parameters.get("id1"), "paramId1");
        assertEquals(parameters.get("id2"), "tagId2");
        assertEquals(parameters.get("tagId"), "tagValue");
        assertEquals(parameters.get("param1"), "param1value");
        assertEquals(parameters.get("param2"), "param2value");
    }

    public void testIncludeParamsDefaultToGET() throws Exception {
        request.setQueryString("one=oneVal&two=twoVal&three=threeVal");

        // request parameter map should not have any effect, as includeParams
        // default to GET, which get its param from request.getQueryString()
        Map tmp = new HashMap();
        tmp.put("one", "aaa");
        tmp.put("two", "bbb");
        tmp.put("three", "ccc");
        request.setParameterMap(tmp);

        tag.setValue("TestAction.acton");

        tag.doStartTag();

        URL url = (URL) tag.getComponent();
        Map parameters = url.getParameters();

        tag.doEndTag();

        assertEquals(parameters.get("one"), "oneVal");
        assertEquals(parameters.get("two"), "twoVal");
        assertEquals(parameters.get("three"), "threeVal");
    }

    public void testActionURL() throws Exception {
        tag.setValue("TestAction.action");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals("TestAction.action", writer.toString());
    }

    public void testAddParameters() throws Exception {
        request.setAttribute("struts.request_uri", "/Test.action");

        request.setAttribute("struts.request_uri", "/TestAction.action");
        request.setQueryString("param0=value0");

        tag.doStartTag();
        tag.component.addParameter("param1", "value1");
        tag.component.addParameter("param2", "value2");
        tag.doEndTag();
        assertEquals("/TestAction.action?param0=value0&amp;param1=value1&amp;param2=value2", writer.toString());
    }

    public void testEvaluateValue() throws Exception {
        Foo foo = new Foo();
        foo.setTitle("test");
        stack.push(foo);
        tag.setValue("%{title}");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals("test", writer.toString());
    }

    public void testHttps() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setValue("list-members.action");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals("list-members.action", writer.toString());
    }

    public void testAnchor() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setValue("list-members.action");
        tag.setAnchor("test");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals("list-members.action#test", writer.toString());
    }

    public void testParamPrecedence() throws Exception {
        request.setRequestURI("/context/someAction.action");
        request.setQueryString("id=22&name=John");

        URLTag urlTag = new URLTag();
        urlTag.setPageContext(pageContext);
        urlTag.setIncludeParams("get");
        urlTag.setEncode("%{false}");

        ParamTag paramTag = new ParamTag();
        paramTag.setPageContext(pageContext);
        paramTag.setName("id");
        paramTag.setValue("%{'33'}");

        urlTag.doStartTag();
        paramTag.doStartTag();
        paramTag.doEndTag();
        urlTag.doEndTag();

        assertEquals("/context/someAction.action?id=33&amp;name=John", writer.getBuffer().toString());
    }

    public void testParamPrecedenceWithAnchor() throws Exception {
        request.setRequestURI("/context/someAction.action");
        request.setQueryString("id=22&name=John");

        URLTag urlTag = new URLTag();
        urlTag.setPageContext(pageContext);
        urlTag.setIncludeParams("get");
        urlTag.setEncode("%{false}");
        urlTag.setAnchor("testAnchor");

        ParamTag paramTag = new ParamTag();
        paramTag.setPageContext(pageContext);
        paramTag.setName("id");
        paramTag.setValue("%{'33'}");

        urlTag.doStartTag();
        paramTag.doStartTag();
        paramTag.doEndTag();
        urlTag.doEndTag();

        assertEquals("/context/someAction.action?id=33&amp;name=John#testAnchor", writer.getBuffer().toString());
    }

    public void testPutId() throws Exception {
        tag.setValue("/public/about");
        assertEquals(null, stack.findString("myId")); // nothing in stack
        tag.setId("myId");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("", writer.toString());
        assertEquals("/public/about", stack.findString("myId")); // is in stack now
    }

    public void testUsingValueOnly() throws Exception {
        tag.setValue("/public/about/team.jsp");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("/public/about/team.jsp", writer.toString());
    }

    public void testRequestURIActionIncludeNone() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("none");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/team.action", writer.toString());
    }

    public void testRequestURIActionIncludeGet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/team.action?section=team&amp;company=acme+inc", writer.toString());
    }

    public void testRequestURIActionIncludeGetDoNotEscapeAmp() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("get");
        tag.setEscapeAmp("false");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/team.action?section=team&company=acme+inc", writer.toString());
    }
    
    public void testRequestURINoActionIncludeNone() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("none");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/public/about", writer.toString());
    }

    public void testNoActionIncludeGet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/public/about?section=team&amp;company=acme+inc", writer.toString());
    }

    public void testRequestURIActionIncludeAll() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("all");

        tag.doStartTag();

        // include nested param tag
        ParamTag paramTag = new ParamTag();
        paramTag.setPageContext(pageContext);
        paramTag.setName("year");
        paramTag.setValue("2006");
        paramTag.doStartTag();
        paramTag.doEndTag();

        tag.doEndTag();

        assertEquals("/team.action?section=team&amp;company=acme+inc&amp;year=2006", writer.toString());
    }

    public void testRequestURINoActionIncludeAll() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("all");

        tag.doStartTag();

        // include nested param tag
        ParamTag paramTag = new ParamTag();
        paramTag.setPageContext(pageContext);
        paramTag.setName("year");
        paramTag.setValue("2006");
        paramTag.doStartTag();
        paramTag.doEndTag();

        tag.doEndTag();

        assertEquals("/public/about?section=team&amp;company=acme+inc&amp;year=2006", writer.toString());
    }

    public void testUnknownIncludeParam() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team");

        tag.setIncludeParams("unknown"); // will log at WARN level
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("/public/about", writer.toString()); // should not add any request parameters
    }

    public void testRequestURIWithAnchor() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("company=acme inc#canada");

        tag.setAction("company");
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/company.action?company=acme+inc", writer.toString()); // will always chop anchor if using requestURI
    }

    public void testIncludeContext() throws Exception {
        request.setupGetContext("/myapp");

        tag.setIncludeContext("true");
        tag.setAction("company");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/myapp/company.action", writer.toString());
    }

    public void testForceAddSchemeHostAndPort() throws Exception {
        tag.setForceAddSchemeHostAndPort("true");
        tag.setAction("company");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("http://localhost/company.action", writer.toString());
    }
    
    public void testEmptyActionCustomMapper() throws Exception {
        Map<String,String> props = new HashMap<String, String>();
        props.put("config", "struts-default.xml,struts-plugin.xml,struts.xml,org/apache/struts2/views/jsp/WW3090-struts.xml");
        
        this.tearDown();
        
        Dispatcher du = this.initDispatcher(props);
        
        /**
         * create our standard mock objects
         */
        action = this.getAction();
        stack = ActionContext.getContext().getValueStack();
        context = stack.getContext();
        stack.push(action);

        request = new StrutsMockHttpServletRequest();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
        response = new StrutsMockHttpServletResponse();
        request.setSession(new StrutsMockHttpSession());
        request.setupGetServletPath("/");

        writer = new StringWriter();

        servletContext = new StrutsMockServletContext();
        servletContext.setRealPath(new File("nosuchfile.properties").getAbsolutePath());
        servletContext.setServletInfo("Resin");

        pageContext = new StrutsMockPageContext();
        pageContext.setRequest(request);
        pageContext.setResponse(response);
        pageContext.setServletContext(servletContext);

        mockContainer = new Mock(Container.class);

        du.setConfigurationManager(configurationManager);
        session = new SessionMap(request);
        Map<String, Object> extraContext = du.createContextMap(new RequestMap(request),
                request.getParameterMap(),
                session,
                new ApplicationMap(pageContext.getServletContext()),
                request,
                response);
        // let's not set the locale -- there is a test that checks if Dispatcher actually picks this up...
        // ... but generally we want to just use no locale (let it stay system default)
        extraContext.remove(ActionContext.LOCALE);
        stack.getContext().putAll(extraContext);

        context.put(ServletActionContext.HTTP_REQUEST, request);
        context.put(ServletActionContext.HTTP_RESPONSE, response);
        context.put(ServletActionContext.SERVLET_CONTEXT, servletContext);

        ActionContext.setContext(new ActionContext(context));
        
        // Make sure we have an action invocation available
        ActionContext.getContext().setActionInvocation(new DefaultActionInvocation(null, true));
        DefaultActionProxyFactory apFactory = new DefaultActionProxyFactory();
        apFactory.setContainer(container);
        ActionProxy ap = apFactory.createActionProxy("/", "hello", null);
        ActionContext.getContext().getActionInvocation().init(ap);

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);

        tag = new URLTag();
        tag.setPageContext(pageContext);
        JspWriter jspWriter = new StrutsMockJspWriter(writer);
        pageContext.setJspWriter(jspWriter);
        
        request.setRequestURI("/context/someAction.action");
        
        tag.setAction(null);
        tag.setValue(null);
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("/hello.action-red", writer.toString());
        
        writer = new StringWriter();
        jspWriter = new StrutsMockJspWriter(writer);
        pageContext.setJspWriter(jspWriter);
        
        tag.doStartTag();
        tag.doEndTag();
        
        assertEquals("/hello.action-blue", writer.toString());
        
        writer = new StringWriter();
        jspWriter = new StrutsMockJspWriter(writer);
        pageContext.setJspWriter(jspWriter);
        
        tag.doStartTag();
        tag.doEndTag();
        
        assertEquals("/hello.action-red", writer.toString());
        
        
    }

	public void testEmbeddedParamTagExpressionGetsEvaluatedCorrectly() throws Exception {
		request.setRequestURI("/public/about");
		request.setQueryString("section=team&company=acme inc");

		tag.setAction("team");
		tag.setIncludeParams("all");

		tag.doStartTag();

		Foo foo = new Foo("test");
		stack.push(foo);

		// include nested param tag
		ParamTag paramTag = new ParamTag();
		paramTag.setPageContext(pageContext);
		paramTag.setName("title");
		paramTag.setValue("%{title}");
		paramTag.doStartTag();
		paramTag.doEndTag();

		tag.doEndTag();

		assertEquals("/team.action?section=team&amp;company=acme+inc&amp;title=test", writer.toString());
	}

	public void testAccessToStackInternalsGetsHandledCorrectly() throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("aaa", new String[] {"1${#session[\"foo\"]='true'}"});
		params.put("aab", new String[] {"1${#session[\"bar\"]}"});
		params.put("aac", new String[] {"1${#_memberAccess[\"allowStaticMethodAccess\"]='true'}"});
		params.put("aad", new String[] {"1${#_memberAccess[\"allowStaticMethodAccess\"]}"});

		request.setParameterMap(params);
		request.setRequestURI("/public/about");
		request.setQueryString("aae${%23session[\"bar\"]}=1%24%7B%23session%5B%22bar%22%5D%7D");
		session.put("bar", "rab");

		tag.setAction("team");
		tag.setIncludeParams("all");

		tag.doStartTag();
		tag.doEndTag();

		Object allowMethodAccess = stack.findValue("\u0023_memberAccess['allowStaticMethodAccess']");
		assertNull(allowMethodAccess);

		assertNull(session.get("foo"));

		assertEquals("/team.action?" +
							 "aab=1%24%7B%23session%5B%22bar%22%5D%7D" +
							 "&amp;" +
							 "aac=1%24%7B%23_memberAccess%5B%22allowStaticMethodAccess%22%5D%3D%27true%27%7D" +
							 "&amp;" +
							 "aaa=1%24%7B%23session%5B%22foo%22%5D%3D%27true%27%7D" +
							 "&amp;" +
							 "aad=1%24%7B%23_memberAccess%5B%22allowStaticMethodAccess%22%5D%7D" +
							 "&amp;"+
						     "aae%24%7B%23session%5B%22bar%22%5D%7D=1%24%7B%23session%5B%22bar%22%5D%7D"
				, writer.toString()
		);
	}

    protected void setUp() throws Exception {
        super.setUp();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);

        tag = new URLTag();
        tag.setPageContext(pageContext);
        JspWriter jspWriter = new StrutsMockJspWriter(writer);
        pageContext.setJspWriter(jspWriter);
    }

    public static class Foo {
        private String title;

		public Foo() {
		}

		public Foo( String title ) {
			this.title = title;
		}

		public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String toString() {
            return "Foo is: " + title;
        }
    }
    
    public static class ValueHolder {
        private String value;

        public ValueHolder(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
        
        
    }
    
    public static class RedBlueActionMapper extends DefaultActionMapper {
        
        @Override
        public String getUriFromActionMapping(ActionMapping mapping) {
            String baseUri = super.getUriFromActionMapping(mapping);
            HttpSession session = ServletActionContext.getRequest().getSession();
            if (session.getAttribute("redBlue")==null) {
                // We are red
                session.setAttribute("redBlue", 0);
                return baseUri + "-red";
            } else {
                // We are blue
                session.removeAttribute("redBlue");
                return baseUri + "-blue";
            }
        }
        
    }
}
