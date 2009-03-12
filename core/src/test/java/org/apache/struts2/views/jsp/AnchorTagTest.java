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

import java.io.StringWriter;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.text.MessageFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.struts2.views.jsp.ui.AnchorTag;
import org.apache.struts2.views.jsp.ui.StrutsBodyContent;
import org.apache.struts2.components.URL;
import org.apache.struts2.components.Anchor;


/**
 *
 */
public class AnchorTagTest extends AbstractUITagTest {
    private StringWriter writer = new StringWriter();
    private AnchorTag tag;

    public void testActionURL() {
        tag.setHref("TestAction.action");
        try {
            tag.doStartTag();
            tag.doEndTag();
            assertTrue(writer.toString().indexOf("href=\"TestAction.action\"") > -1);
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }
    }

    public void testAddParameters() {
        tag.setHref("/TestAction.action");
        String bodyText = "<img src=\"#\"/>";
        try {
            StrutsBodyContent bodyContent = new StrutsBodyContent(null);
            bodyContent.print(bodyText);
            tag.setBodyContent(bodyContent);

            tag.doStartTag();
            tag.doEndTag();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * To test priority of parameter passed in to url component though
     * various way
     * - current request url
     * - tag's value attribute
     * - tag's nested param tag
     * <p/>
     * id1
     * ===
     * - found in current request url
     * - found in tag's value attribute
     * - found in tag's param tag
     * CONCLUSION: tag's param tag takes precedence (paramId1)
     * <p/>
     * id2
     * ===
     * - found in current request url
     * - found in tag's value attribute
     * CONCLUSION: tag's value attribute take precedence (tagId2)
     * <p/>
     * urlParam1
     * =========
     * - found in current request url
     * CONCLUSION: param in current request url will be used (urlValue1)
     * <p/>
     * urlParam2
     * =========
     * - found in current request url
     * CONCLUSION: param in current request url will be used. (urlValue2)
     * <p/>
     * tagId
     * =====
     * - found in tag's value attribute
     * CONCLUSION: param in tag's value attribute wil; be used. (tagValue)
     * <p/>
     * param1
     * ======
     * - found in nested param tag
     * CONCLUSION: param in nested param tag will be used. (param1value)
     * <p/>
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

        tag.doEndTag();

        assertEquals(wrapWithAnchor("testAction.action?id1=paramId1&amp;id2=tagId2&" +
                "amp;tagId=tagValue&amp;urlParam1=urlValue1&amp;urlParam2=urlValue2&amp;param1=param1value&amp;param2=param2value"), writer.toString());
    }

    /**
     * Use Iterable values as the value of the param tags
     *
     * @throws Exception
     */
    public void testIterableParameters() throws Exception {
        tag.setValue("/TestAction.action?p0=z");

        tag.doStartTag();
        //Iterable
        List<URLTagTest.ValueHolder> list = new ArrayList<URLTagTest.ValueHolder>();
        list.add(new URLTagTest.ValueHolder("a"));
        list.add(new URLTagTest.ValueHolder("b"));
        stack.getContext().put("param1value", list);

        ParamTag param1 = new ParamTag();
        param1.setPageContext(pageContext);
        param1.setName("p1");
        param1.setValue("%{#param1value}");
        param1.doStartTag();
        param1.doEndTag();


        //String[]
        stack.getContext().put("param2value", new String[]{"d", "e"});
        ParamTag param2 = new ParamTag();
        param2.setPageContext(pageContext);
        param2.setName("p2");
        param2.setValue("%{#param2value}");
        param2.doStartTag();
        param2.doEndTag();


        //ValueHolder[]
        stack.getContext().put("param3value", new URLTagTest.ValueHolder[]{
                new URLTagTest.ValueHolder("f"), new URLTagTest.ValueHolder("g")});
        ParamTag param3 = new ParamTag();
        param3.setPageContext(pageContext);
        param3.setName("p3");
        param3.setValue("%{#param3value}");
        param3.doStartTag();
        param3.doEndTag();

        tag.doEndTag();
        
        String result =  writer.toString();
        assertTrue(result.contains("p1=a"));
        assertTrue(result.contains("p1=b"));
        assertTrue(result.contains("p2=d"));
        assertTrue(result.contains("p2=e"));
        assertTrue(result.contains("p3=f"));
        assertTrue(result.contains("p3=g"));
        assertTrue(result.contains("p0=z"));
    }

    /**
     * To test priority of parameter passed in to url component though
     * various way, with includeParams="NONE"
     * - current request url
     * - tag's value attribute
     * - tag's nested param tag
     * <p/>
     * In this case only parameters from the tag itself is taken into account.
     * Those from request will not count, only those in tag's value attribute
     * and nested param tag.
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
        tag.doEndTag();

        assertEquals(wrapWithAnchor("testAction.action?id1=paramId1&amp;id2=tagId2&amp;tagId=tagValue&amp;param1=param1value&amp;param2=param2value"), writer.toString());
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
        tag.doEndTag();

        assertEquals(wrapWithAnchor("TestAction.acton?one=oneVal&amp;two=twoVal&amp;three=threeVal"), writer.toString());
    }

    public void testActionURL2() throws Exception {
        tag.setValue("TestAction.action");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("TestAction.action"), writer.toString());
    }

    public void testAddParameters2() throws Exception {
        request.setAttribute("struts.request_uri", "/TestAction.action");
        request.setQueryString("param0=value0");

        tag.doStartTag();
        ParamTag param1 = new ParamTag();
        param1.setPageContext(pageContext);
        param1.setName("param1");
        param1.setValue("%{'value1'}");
        param1.doStartTag();
        param1.doEndTag();

        ParamTag param2 = new ParamTag();
        param2.setPageContext(pageContext);
        param2.setName("param2");
        param2.setValue("%{'value2'}");
        param2.doStartTag();
        param2.doEndTag();

        tag.doEndTag();
        String result = writer.toString();
        assertTrue(result.contains("param0=value0"));
        assertTrue(result.contains("param1=value1"));
        assertTrue(result.contains("param2=value2"));
        assertTrue(result.contains("/TestAction.action"));        
    }

    public void testEvaluateValue() throws Exception {
        URLTagTest.Foo foo = new URLTagTest.Foo();
        foo.setTitle("test");
        stack.push(foo);
        tag.setValue("%{title}");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("test"), writer.toString());
    }

    public void testHttps() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setValue("list-members.action");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("list-members.action"), writer.toString());
    }

    public void testAnchor() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setValue("list-members.action");
        tag.setAnchor("test");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("list-members.action#test"), writer.toString());
    }

    public void testParamPrecedence() throws Exception {
        request.setRequestURI("/context/someAction.action");
        request.setQueryString("id=22&name=John");

        AnchorTag anchor = new AnchorTag();
        anchor.setPageContext(pageContext);
        anchor.setIncludeParams("get");
        anchor.setEncode("%{false}");

        ParamTag paramTag = new ParamTag();
        paramTag.setPageContext(pageContext);
        paramTag.setName("id");
        paramTag.setValue("%{'33'}");

        anchor.doStartTag();
        paramTag.doStartTag();
        paramTag.doEndTag();
        anchor.doEndTag();

        assertEquals(wrapWithAnchor("/context/someAction.action?id=33&amp;name=John"), writer.getBuffer().toString());
    }

    public void testParamPrecedenceWithAnchor() throws Exception {
        request.setRequestURI("/context/someAction.action");
        request.setQueryString("id=22&name=John");

        AnchorTag anchorTag = new AnchorTag();
        anchorTag.setPageContext(pageContext);
        anchorTag.setIncludeParams("get");
        anchorTag.setEncode("%{false}");
        anchorTag.setAnchor("testAnchor");

        ParamTag paramTag = new ParamTag();
        paramTag.setPageContext(pageContext);
        paramTag.setName("id");
        paramTag.setValue("%{'33'}");

        anchorTag.doStartTag();
        paramTag.doStartTag();
        paramTag.doEndTag();
        anchorTag.doEndTag();

        assertEquals(wrapWithAnchor("/context/someAction.action?id=33&amp;name=John#testAnchor"), writer.getBuffer().toString());
    }


    public void testUsingValueOnly() throws Exception {
        tag.setValue("/public/about/team.jsp");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("/public/about/team.jsp"), writer.toString());
    }

    public void testRequestURIActionIncludeNone() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("none");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/team.action"), writer.toString());
    }

    public void testRequestURIActionIncludeGet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/team.action?section=team&amp;company=acme+inc"), writer.toString());
    }

    public void testRequestURIActionIncludeGetDoNotEscapeAmp() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("get");
        tag.setEscapeAmp("false");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/team.action?section=team&company=acme+inc"), writer.toString());
    }

    public void testRequestURINoActionIncludeNone() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("none");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/public/about"), writer.toString());
    }

    public void testNoActionIncludeGet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/public/about?section=team&amp;company=acme+inc"), writer.toString());
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

        String result = writer.toString();
        assertTrue(result.contains("/team.action?"));
        assertTrue(result.contains("section=team"));
        assertTrue(result.contains("company=acme+inc"));
        assertTrue(result.contains("year=2006"));
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

        String result = writer.toString();
        assertTrue(result.contains("/public/about?"));
        assertTrue(result.contains("section=team"));
        assertTrue(result.contains("company=acme+inc"));
        assertTrue(result.contains("year=2006"));
    }

    public void testUnknownIncludeParam() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team");

        tag.setIncludeParams("unknown"); // will log at WARN level
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("/public/about"), writer.toString()); // should not add any request parameters
    }

    public void testRequestURIWithAnchor() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("company=acme inc#canada");

        tag.setAction("company");
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/company.action?company=acme+inc"), writer.toString()); // will always chop anchor if using requestURI
    }

    public void testIncludeContext() throws Exception {
        request.setupGetContext("/myapp");

        tag.setIncludeContext("true");
        tag.setAction("company");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/myapp/company.action"), writer.toString());
    }

    public void testForceAddSchemeHostAndPort() throws Exception {
        tag.setForceAddSchemeHostAndPort("true");
        tag.setAction("company");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("http://localhost/company.action"), writer.toString());
    }

    private String wrapWithAnchor(String href) {
        return MessageFormat.format("<a href=\"{0}\"></a>", href);
    }

    protected void setUp() throws Exception {
        super.setUp();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);

        tag = new AnchorTag();
        tag.setPageContext(pageContext);
        JspWriter jspWriter = new StrutsMockJspWriter(writer);
        pageContext.setJspWriter(jspWriter);
    }

}
