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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testActionURL_clearTagStateSet() {
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setHref("TestAction.action");
        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
            tag.doEndTag();
            assertTrue(writer.toString().indexOf("href=\"TestAction.action\"") > -1);
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testAddParameters_clearTagStateSet() {
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setHref("/TestAction.action");
        String bodyText = "<img src=\"#\"/>";
        try {
            StrutsBodyContent bodyContent = new StrutsBodyContent(null);
            bodyContent.print(bodyText);
            tag.setBodyContent(bodyContent);

            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
            tag.doEndTag();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * To test priority of parameter passed in to url component though
     * various way
     * - current request url
     * - tag's value attribute
     * - tag's nested param tag
     * <br>
     * id1
     * ===
     * - found in current request url
     * - found in tag's value attribute
     * - found in tag's param tag
     * CONCLUSION: tag's param tag takes precedence (paramId1)
     * <br>
     * id2
     * ===
     * - found in current request url
     * - found in tag's value attribute
     * CONCLUSION: tag's value attribute take precedence (tagId2)
     * <br>
     * urlParam1
     * =========
     * - found in current request url
     * CONCLUSION: param in current request url will be used (urlValue1)
     * <br>
     * urlParam2
     * =========
     * - found in current request url
     * CONCLUSION: param in current request url will be used. (urlValue2)
     * <br>
     * tagId
     * =====
     * - found in tag's value attribute
     * CONCLUSION: param in tag's value attribute wil; be used. (tagValue)
     * <br>
     * param1
     * ======
     * - found in nested param tag
     * CONCLUSION: param in nested param tag will be used. (param1value)
     * <br>
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * To test priority of parameter passed in to url component though
     * various way
     * - current request url
     * - tag's value attribute
     * - tag's nested param tag
     * <br>
     * id1
     * ===
     * - found in current request url
     * - found in tag's value attribute
     * - found in tag's param tag
     * CONCLUSION: tag's param tag takes precedence (paramId1)
     * <br>
     * id2
     * ===
     * - found in current request url
     * - found in tag's value attribute
     * CONCLUSION: tag's value attribute take precedence (tagId2)
     * <br>
     * urlParam1
     * =========
     * - found in current request url
     * CONCLUSION: param in current request url will be used (urlValue1)
     * <br>
     * urlParam2
     * =========
     * - found in current request url
     * CONCLUSION: param in current request url will be used. (urlValue2)
     * <br>
     * tagId
     * =====
     * - found in tag's value attribute
     * CONCLUSION: param in tag's value attribute wil; be used. (tagValue)
     * <br>
     * param1
     * ======
     * - found in nested param tag
     * CONCLUSION: param in nested param tag will be used. (param1value)
     * <br>
     * param2
     * ======
     * - found in nested param tag
     * CONCLUSION: param in nested param tag will be used. (param2value)
     */
    public void testParametersPriority_clearTagStateSet() throws Exception {
        request.setQueryString("id1=urlId1&id2=urlId2&urlParam1=urlValue1&urlParam2=urlValue2");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
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
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        param1.doStartTag();
        setComponentTagClearTagState(param1, true);  // Ensure component tag state clearing is set true (to match tag).
        param1.doEndTag();
        param2.doStartTag();
        setComponentTagClearTagState(param2, true);  // Ensure component tag state clearing is set true (to match tag).
        param2.doEndTag();
        param3.doStartTag();
        setComponentTagClearTagState(param3, true);  // Ensure component tag state clearing is set true (to match tag).
        param3.doEndTag();

        tag.doEndTag();

        assertEquals(wrapWithAnchor("testAction.action?id1=paramId1&amp;id2=tagId2&" +
                "amp;tagId=tagValue&amp;urlParam1=urlValue1&amp;urlParam2=urlValue2&amp;param1=param1value&amp;param2=param2value"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param1, freshParamTag));

        //String[]
        stack.getContext().put("param2value", new String[]{"d", "e"});
        ParamTag param2 = new ParamTag();
        param2.setPageContext(pageContext);
        param2.setName("p2");
        param2.setValue("%{#param2value}");
        param2.doStartTag();
        param2.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param2, freshParamTag));

        //ValueHolder[]
        stack.getContext().put("param3value", new URLTagTest.ValueHolder[]{
                new URLTagTest.ValueHolder("f"), new URLTagTest.ValueHolder("g")});
        ParamTag param3 = new ParamTag();
        param3.setPageContext(pageContext);
        param3.setName("p3");
        param3.setValue("%{#param3value}");
        param3.doStartTag();
        param3.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param3, freshParamTag));

        tag.doEndTag();
        
        String result =  writer.toString();
        assertTrue(result.contains("p1=a"));
        assertTrue(result.contains("p1=b"));
        assertTrue(result.contains("p2=d"));
        assertTrue(result.contains("p2=e"));
        assertTrue(result.contains("p3=f"));
        assertTrue(result.contains("p3=g"));
        assertTrue(result.contains("p0=z"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * Use Iterable values as the value of the param tags
     *
     * @throws Exception
     */
    public void testIterableParameters_clearTagStateSet() throws Exception {
        tag.setValue("/TestAction.action?p0=z");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        //Iterable
        List<URLTagTest.ValueHolder> list = new ArrayList<URLTagTest.ValueHolder>();
        list.add(new URLTagTest.ValueHolder("a"));
        list.add(new URLTagTest.ValueHolder("b"));
        stack.getContext().put("param1value", list);

        ParamTag param1 = new ParamTag();
        param1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        param1.setPageContext(pageContext);
        param1.setName("p1");
        param1.setValue("%{#param1value}");
        param1.doStartTag();
        setComponentTagClearTagState(param1, true);  // Ensure component tag state clearing is set true (to match tag).
        param1.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param1, freshParamTag));

        //String[]
        stack.getContext().put("param2value", new String[]{"d", "e"});
        ParamTag param2 = new ParamTag();
        param2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        param2.setPageContext(pageContext);
        param2.setName("p2");
        param2.setValue("%{#param2value}");
        param2.doStartTag();
        setComponentTagClearTagState(param2, true);  // Ensure component tag state clearing is set true (to match tag).
        param2.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param2, freshParamTag));

        //ValueHolder[]
        stack.getContext().put("param3value", new URLTagTest.ValueHolder[]{
                new URLTagTest.ValueHolder("f"), new URLTagTest.ValueHolder("g")});
        ParamTag param3 = new ParamTag();
        param3.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        param3.setPageContext(pageContext);
        param3.setName("p3");
        param3.setValue("%{#param3value}");
        param3.doStartTag();
        setComponentTagClearTagState(param3, true);  // Ensure component tag state clearing is set true (to match tag).
        param3.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        assertTrue("Tag state after doEndTag() inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param3, freshParamTag));

        tag.doEndTag();

        String result =  writer.toString();
        assertTrue(result.contains("p1=a"));
        assertTrue(result.contains("p1=b"));
        assertTrue(result.contains("p2=d"));
        assertTrue(result.contains("p2=e"));
        assertTrue(result.contains("p3=f"));
        assertTrue(result.contains("p3=g"));
        assertTrue(result.contains("p0=z"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * <p>
     * To test priority of parameter passed in to url component though
     * various way, with includeParams="NONE"
     * </p>
     *
     * - current request url<br>
     * - tag's value attribute<br>
     * - tag's nested param tag<br>
     *
     * <p>
     * In this case only parameters from the tag itself is taken into account.
     * Those from request will not count, only those in tag's value attribute
     * and nested param tag.
     * </p>
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param1, freshParamTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param2, freshParamTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param3, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

   /**
     * <p>
     * To test priority of parameter passed in to url component though
     * various way, with includeParams="NONE"
     * </p>
     *
     * - current request url<br>
     * - tag's value attribute<br>
     * - tag's nested param tag<br>
     *
     * <p>
     * In this case only parameters from the tag itself is taken into account.
     * Those from request will not count, only those in tag's value attribute
     * and nested param tag.
     * </p>
     * @throws Exception
     */
    public void testParametersPriorityWithIncludeParamsAsNONE_clearTagStateSet() throws Exception {
        request.setQueryString("id1=urlId1&id2=urlId2&urlParam1=urlValue1&urlParam2=urlValue2");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("testAction.action?id1=tagId1&id2=tagId2&tagId=tagValue");
        tag.setIncludeParams("NONE");

        ParamTag param1 = new ParamTag();
        param1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        param1.setPageContext(pageContext);
        param1.setName("param1");
        param1.setValue("%{'param1value'}");

        ParamTag param2 = new ParamTag();
        param2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        param2.setPageContext(pageContext);
        param2.setName("param2");
        param2.setValue("%{'param2value'}");

        ParamTag param3 = new ParamTag();
        param3.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        param3.setPageContext(pageContext);
        param3.setName("id1");
        param3.setValue("%{'paramId1'}");


        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        param1.doStartTag();
        setComponentTagClearTagState(param1, true);  // Ensure component tag state clearing is set true (to match tag).
        param1.doEndTag();
        param2.doStartTag();
        setComponentTagClearTagState(param2, true);  // Ensure component tag state clearing is set true (to match tag).
        param2.doEndTag();
        param3.doStartTag();
        setComponentTagClearTagState(param3, true);  // Ensure component tag state clearing is set true (to match tag).
        param3.doEndTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("testAction.action?id1=paramId1&amp;id2=tagId2&amp;tagId=tagValue&amp;param1=param1value&amp;param2=param2value"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param1, freshParamTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param2, freshParamTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param3, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeParamsDefaultToGET_clearTagStateSet() throws Exception {
        request.setQueryString("one=oneVal&two=twoVal&three=threeVal");

        // request parameter map should not have any effect, as includeParams
        // default to GET, which get its param from request.getQueryString()
        Map tmp = new HashMap();
        tmp.put("one", "aaa");
        tmp.put("two", "bbb");
        tmp.put("three", "ccc");
        request.setParameterMap(tmp);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("TestAction.acton");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("TestAction.acton?one=oneVal&amp;two=twoVal&amp;three=threeVal"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param1, freshParamTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param2, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "For this test, tag only has pageContext set, so it should still be equal.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testAddParameters2_clearTagStateSet() throws Exception {
        request.setAttribute("struts.request_uri", "/TestAction.action");
        request.setQueryString("param0=value0");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        ParamTag param1 = new ParamTag();
        param1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        param1.setPageContext(pageContext);
        param1.setName("param1");
        param1.setValue("%{'value1'}");
        param1.doStartTag();
        setComponentTagClearTagState(param1, true);  // Ensure component tag state clearing is set true (to match tag).
        param1.doEndTag();

        ParamTag param2 = new ParamTag();
        param2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        param2.setPageContext(pageContext);
        param2.setName("param2");
        param2.setValue("%{'value2'}");
        param2.doStartTag();
        setComponentTagClearTagState(param2, true);  // Ensure component tag state clearing is set true (to match tag).
        param2.doEndTag();

        tag.doEndTag();
        String result = writer.toString();
        assertTrue(result.contains("param0=value0"));
        assertTrue(result.contains("param1=value1"));
        assertTrue(result.contains("param2=value2"));
        assertTrue(result.contains("/TestAction.action"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param1, freshParamTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(param2, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEvaluateValue() throws Exception {
        URLTagTest.Foo foo = new URLTagTest.Foo();
        foo.setTitle("test");
        stack.push(foo);
        tag.setValue("%{title}");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("test"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testEvaluateValue_clearTagStateSet() throws Exception {
        URLTagTest.Foo foo = new URLTagTest.Foo();
        foo.setTitle("test");
        stack.push(foo);
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("%{title}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(wrapWithAnchor("test"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testHttps() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setValue("list-members.action");

        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("list-members.action"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testHttps_clearTagStateSet() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("list-members.action");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(wrapWithAnchor("list-members.action"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testAnchor_clearTagStateSet() throws Exception {
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(443);

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("list-members.action");
        tag.setAnchor("test");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(wrapWithAnchor("list-members.action#test"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(paramTag, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(anchor, freshTag));
    }

    public void testParamPrecedence_clearTagStateSet() throws Exception {
        request.setRequestURI("/context/someAction.action");
        request.setQueryString("id=22&name=John");

        AnchorTag anchor = new AnchorTag();
        anchor.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        anchor.setPageContext(pageContext);
        anchor.setIncludeParams("get");
        anchor.setEncode("%{false}");

        ParamTag paramTag = new ParamTag();
        paramTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        paramTag.setPageContext(pageContext);
        paramTag.setName("id");
        paramTag.setValue("%{'33'}");

        anchor.doStartTag();
        setComponentTagClearTagState(anchor, true);  // Ensure component tag state clearing is set true (to match tag).
        paramTag.doStartTag();
        setComponentTagClearTagState(paramTag, true);  // Ensure component tag state clearing is set true (to match tag).
        paramTag.doEndTag();
        anchor.doEndTag();

        assertEquals(wrapWithAnchor("/context/someAction.action?id=33&amp;name=John"), writer.getBuffer().toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(paramTag, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(anchor, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(paramTag, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(anchorTag, freshTag));
    }

    public void testParamPrecedenceWithAnchor_clearTagStateSet() throws Exception {
        request.setRequestURI("/context/someAction.action");
        request.setQueryString("id=22&name=John");

        AnchorTag anchorTag = new AnchorTag();
        anchorTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        anchorTag.setPageContext(pageContext);
        anchorTag.setIncludeParams("get");
        anchorTag.setEncode("%{false}");
        anchorTag.setAnchor("testAnchor");

        ParamTag paramTag = new ParamTag();
        paramTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        paramTag.setPageContext(pageContext);
        paramTag.setName("id");
        paramTag.setValue("%{'33'}");

        anchorTag.doStartTag();
        setComponentTagClearTagState(anchorTag, true);  // Ensure component tag state clearing is set true (to match tag).
        paramTag.doStartTag();
        setComponentTagClearTagState(paramTag, true);  // Ensure component tag state clearing is set true (to match tag).
        paramTag.doEndTag();
        anchorTag.doEndTag();

        assertEquals(wrapWithAnchor("/context/someAction.action?id=33&amp;name=John#testAnchor"), writer.getBuffer().toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(paramTag, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(anchorTag, freshTag));
    }

    public void testUsingValueOnly() throws Exception {
        tag.setValue("/public/about/team.jsp");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("/public/about/team.jsp"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testUsingValueOnly_clearTagStateSet() throws Exception {
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setValue("/public/about/team.jsp");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(wrapWithAnchor("/public/about/team.jsp"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURIActionIncludeNone() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("none");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/team.action"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURIActionIncludeNone_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setAction("team");
        tag.setIncludeParams("none");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/team.action"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURIActionIncludeGet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction("team");
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/team.action?section=team&amp;company=acme+inc"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURIActionIncludeGet_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setAction("team");
        tag.setIncludeParams("get");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/team.action?section=team&amp;company=acme+inc"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURIActionIncludeGetDoNotEscapeAmp_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setAction("team");
        tag.setIncludeParams("get");
        tag.setEscapeAmp("false");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/team.action?section=team&company=acme+inc"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURINoActionIncludeNone() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("none");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/public/about"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURINoActionIncludeNone_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setAction(null);
        tag.setIncludeParams("none");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/public/about"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testNoActionIncludeGet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setAction(null);
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/public/about?section=team&amp;company=acme+inc"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testNoActionIncludeGet_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setAction(null);
        tag.setIncludeParams("get");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/public/about?section=team&amp;company=acme+inc"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(paramTag, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURIActionIncludeAll_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setAction("team");
        tag.setIncludeParams("all");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        // include nested param tag
        ParamTag paramTag = new ParamTag();
        paramTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        paramTag.setPageContext(pageContext);
        paramTag.setName("year");
        paramTag.setValue("2006");
        paramTag.doStartTag();
        setComponentTagClearTagState(paramTag, true);  // Ensure component tag state clearing is set true (to match tag).
        paramTag.doEndTag();

        tag.doEndTag();

        String result = writer.toString();
        assertTrue(result.contains("/team.action?"));
        assertTrue(result.contains("section=team"));
        assertTrue(result.contains("company=acme+inc"));
        assertTrue(result.contains("year=2006"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true); 
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(paramTag, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(paramTag, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURINoActionIncludeAll_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team&company=acme inc");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setAction(null);
        tag.setIncludeParams("all");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        // include nested param tag
        ParamTag paramTag = new ParamTag();
        paramTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        paramTag.setPageContext(pageContext);
        paramTag.setName("year");
        paramTag.setValue("2006");
        paramTag.doStartTag();
        setComponentTagClearTagState(paramTag, true);  // Ensure component tag state clearing is set true (to match tag).
        paramTag.doEndTag();

        tag.doEndTag();

        String result = writer.toString();
        assertTrue(result.contains("/public/about?"));
        assertTrue(result.contains("section=team"));
        assertTrue(result.contains("company=acme+inc"));
        assertTrue(result.contains("year=2006"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ParamTag freshParamTag = new ParamTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(paramTag, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testUnknownIncludeParam() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team");

        tag.setIncludeParams("unknown"); // will log at WARN level
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(wrapWithAnchor("/public/about"), writer.toString()); // should not add any request parameters

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testUnknownIncludeParam_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("section=team");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setIncludeParams("unknown"); // will log at WARN level
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        assertEquals(wrapWithAnchor("/public/about"), writer.toString()); // should not add any request parameters

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testRequestURIWithAnchor() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("company=acme inc#canada");

        tag.setAction("company");
        tag.setIncludeParams("get");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/company.action?company=acme+inc"), writer.toString()); // will always chop anchor if using requestURI

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

        public void testRequestURIWithAnchor_clearTagStateSet() throws Exception {
        request.setRequestURI("/public/about");
        request.setQueryString("company=acme inc#canada");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setAction("company");
        tag.setIncludeParams("get");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/company.action?company=acme+inc"), writer.toString()); // will always chop anchor if using requestURI

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeContext() throws Exception {
        request.setupGetContext("/myapp");

        tag.setIncludeContext("true");
        tag.setAction("company");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/myapp/company.action"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testIncludeContext_clearTagStateSet() throws Exception {
        request.setupGetContext("/myapp");

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setIncludeContext("true");
        tag.setAction("company");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("/myapp/company.action"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testForceAddSchemeHostAndPort() throws Exception {
        tag.setForceAddSchemeHostAndPort("true");
        tag.setAction("company");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(wrapWithAnchor("http://localhost/company.action"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testForceAddSchemeHostAndPort_clearTagStateSet() throws Exception {
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setForceAddSchemeHostAndPort("true");
        tag.setAction("company");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        assertEquals(wrapWithAnchor("http://localhost/company.action"), writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        AnchorTag freshTag = new AnchorTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    private String wrapWithAnchor(String href) {
        return MessageFormat.format("<a href=\"{0}\"></a>", href);
    }

    @Override
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
