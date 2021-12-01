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
package org.apache.struts2.views.jsp.ui;

import java.util.LinkedHashMap;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

public class OptGroupTest extends AbstractUITagTest {

    public void testOptGroupSimple() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupSimple_clearTagStateSet() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        setComponentTagClearTagState(selectTag, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        setComponentTagClearTagState(optGroupTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        setComponentTagClearTagState(optGroupTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPerformClearTagStateForTagPoolingServers(true);
        freshOptGroupTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupWithSingleSelect() throws Exception {

        SelectTag selectTag = new SelectTag();
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");
        selectTag.setValue("%{'EEE'}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-2.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupWithSingleSelect_clearTagStateSet() throws Exception {

        SelectTag selectTag = new SelectTag();
        selectTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");
        selectTag.setValue("%{'EEE'}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        setComponentTagClearTagState(selectTag, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        setComponentTagClearTagState(optGroupTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        setComponentTagClearTagState(optGroupTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-2.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPerformClearTagStateForTagPoolingServers(true);
        freshOptGroupTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupWithMultipleSelect() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setMultiple("true");
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");
        selectTag.setValue("%{{'EEE','BBB','TWO'}}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-3.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupWithMultipleSelect_clearTagStateSet() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        selectTag.setMultiple("true");
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");
        selectTag.setValue("%{{'EEE','BBB','TWO'}}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        setComponentTagClearTagState(selectTag, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        setComponentTagClearTagState(optGroupTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        setComponentTagClearTagState(optGroupTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-3.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPerformClearTagStateForTagPoolingServers(true);
        freshOptGroupTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupWithMultipleSelectIntKey() throws Exception {
      SelectTag selectTag = new SelectTag();
      selectTag.setMultiple("true");
      selectTag.setName("mySelection");
      selectTag.setLabel("My Selection");
      selectTag.setList("%{#{1:'one',2:'two',3:'three'}}");
      selectTag.setValue("%{{22,12,2}}");

      OptGroupTag optGroupTag1 = new OptGroupTag();
      optGroupTag1.setLabel("My Label 1");
      optGroupTag1.setList("%{#{11:'aaa',12:'bbb',13:'ccc'}}");

      OptGroupTag optGroupTag2 = new OptGroupTag();
      optGroupTag2.setLabel("My Label 2");
      optGroupTag2.setList("%{#{21:'ddd',22:'eee',23:'fff'}}");

      selectTag.setPageContext(pageContext);
      selectTag.doStartTag();
      optGroupTag1.setPageContext(pageContext);
      optGroupTag1.doStartTag();
      optGroupTag1.doEndTag();
      optGroupTag2.setPageContext(pageContext);
      optGroupTag2.doStartTag();
      optGroupTag2.doEndTag();
      selectTag.doEndTag();


      //System.out.println(writer.toString());
      verify(SelectTag.class.getResource("OptGroup-7.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
  }

  public void testOptGroupWithMultipleSelectIntKey_clearTagStateSet() throws Exception {
      SelectTag selectTag = new SelectTag();
      selectTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
      selectTag.setMultiple("true");
      selectTag.setName("mySelection");
      selectTag.setLabel("My Selection");
      selectTag.setList("%{#{1:'one',2:'two',3:'three'}}");
      selectTag.setValue("%{{22,12,2}}");

      OptGroupTag optGroupTag1 = new OptGroupTag();
      optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
      optGroupTag1.setLabel("My Label 1");
      optGroupTag1.setList("%{#{11:'aaa',12:'bbb',13:'ccc'}}");

      OptGroupTag optGroupTag2 = new OptGroupTag();
      optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
      optGroupTag2.setLabel("My Label 2");
      optGroupTag2.setList("%{#{21:'ddd',22:'eee',23:'fff'}}");

      selectTag.setPageContext(pageContext);
      selectTag.doStartTag();
      setComponentTagClearTagState(selectTag, true);  // Ensure component tag state clearing is set true (to match tag).
      optGroupTag1.setPageContext(pageContext);
      optGroupTag1.doStartTag();
      setComponentTagClearTagState(optGroupTag1, true);  // Ensure component tag state clearing is set true (to match tag).
      optGroupTag1.doEndTag();
      optGroupTag2.setPageContext(pageContext);
      optGroupTag2.doStartTag();
      setComponentTagClearTagState(optGroupTag2, true);  // Ensure component tag state clearing is set true (to match tag).
      optGroupTag2.doEndTag();
      selectTag.doEndTag();


      //System.out.println(writer.toString());
      verify(SelectTag.class.getResource("OptGroup-7.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPerformClearTagStateForTagPoolingServers(true);
        freshOptGroupTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupNumbers() throws Exception {
    	
    	((TestAction)action).setMap(new LinkedHashMap() {{
    		put("AAA", "aaa");
    		put(111111L, "bbb");
    		put("CCC", "ccc");
    	}});
    	
        SelectTag selectTag = new SelectTag();
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("map");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-4.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupNumbers_clearTagStateSet() throws Exception {

    	((TestAction)action).setMap(new LinkedHashMap() {{
    		put("AAA", "aaa");
    		put(111111L, "bbb");
    		put("CCC", "ccc");
    	}});

        SelectTag selectTag = new SelectTag();
        selectTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("map");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        setComponentTagClearTagState(selectTag, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        setComponentTagClearTagState(optGroupTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        setComponentTagClearTagState(optGroupTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-4.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPerformClearTagStateForTagPoolingServers(true);
        freshOptGroupTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupForHtmlEncoding() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setMultiple("true");
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");
        selectTag.setValue("%{{'EEE','TWO'}}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'&':'aaa','<':'bbb','CCC':'<script'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'<cat>':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-5.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupForHtmlEncoding_clearTagStateSet() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        selectTag.setMultiple("true");
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("%{#{'ONE':'one','TWO':'two','THREE':'three'}}");
        selectTag.setValue("%{{'EEE','TWO'}}");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'&':'aaa','<':'bbb','CCC':'<script'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'<cat>':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        setComponentTagClearTagState(selectTag, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        setComponentTagClearTagState(optGroupTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        setComponentTagClearTagState(optGroupTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-5.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPerformClearTagStateForTagPoolingServers(true);
        freshOptGroupTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupWithValueKey() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("selectValues");
        selectTag.setListValueKey("valueKey");

        LocaleTestAction localeTestAction = new LocaleTestAction();
        container.inject(localeTestAction);
        
        localeTestAction.setText("LocaleKeyValueTest.ONE","Edno");
        localeTestAction.setText("LocaleKeyValueTest.TWO","Dve");
        stack.push(localeTestAction);

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-6.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupWithValueKey_clearTagStateSet() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("selectValues");
        selectTag.setListValueKey("valueKey");

        LocaleTestAction localeTestAction = new LocaleTestAction();
        container.inject(localeTestAction);

        localeTestAction.setText("LocaleKeyValueTest.ONE","Edno");
        localeTestAction.setText("LocaleKeyValueTest.TWO","Dve");
        stack.push(localeTestAction);

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("%{#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}}");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("%{#{'DDD':'ddd','EEE':'eee','FFF':'fff'}}");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        setComponentTagClearTagState(selectTag, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        setComponentTagClearTagState(optGroupTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        setComponentTagClearTagState(optGroupTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag2.doEndTag();
        selectTag.doEndTag();


        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-6.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPerformClearTagStateForTagPoolingServers(true);
        freshOptGroupTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupListAttributes() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("#{'ONE':'one','TWO':'two','THREE':'three'}");
        selectTag.setListCssClass("'option-css-class ' + key");
        selectTag.setListCssStyle("'background-color: green; font-family: ' + key");
        selectTag.setListTitle("'option-title' + key");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}");
        optGroupTag1.setListCssClass("'optgroup-option-css-class ' + key");
        optGroupTag1.setListCssStyle("'background-color: blue; font-family: ' + key");
        optGroupTag1.setListTitle("'optgroup-option-title' + key");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("#{'DDD':'ddd','EEE':'eee','FFF':'fff'}");
        optGroupTag2.setListCssClass("notExistingProperty");
        optGroupTag2.setListCssStyle("notExistingProperty");
        optGroupTag2.setListTitle("notExistingProperty");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        optGroupTag2.doEndTag();
        selectTag.doEndTag();

        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-8.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }

    public void testOptGroupListAttributes_clearTagStateSet() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("#{'ONE':'one','TWO':'two','THREE':'three'}");
        selectTag.setListCssClass("'option-css-class ' + key");
        selectTag.setListCssStyle("'background-color: green; font-family: ' + key");
        selectTag.setListTitle("'option-title' + key");

        OptGroupTag optGroupTag1 = new OptGroupTag();
        optGroupTag1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag1.setLabel("My Label 1");
        optGroupTag1.setList("#{'AAA':'aaa','BBB':'bbb','CCC':'ccc'}");
        optGroupTag1.setListCssClass("'optgroup-option-css-class ' + key");
        optGroupTag1.setListCssStyle("'background-color: blue; font-family: ' + key");
        optGroupTag1.setListTitle("'optgroup-option-title' + key");

        OptGroupTag optGroupTag2 = new OptGroupTag();
        optGroupTag2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        optGroupTag2.setLabel("My Label 2");
        optGroupTag2.setList("#{'DDD':'ddd','EEE':'eee','FFF':'fff'}");
        optGroupTag2.setListCssClass("notExistingProperty");
        optGroupTag2.setListCssStyle("notExistingProperty");
        optGroupTag2.setListTitle("notExistingProperty");

        selectTag.setPageContext(pageContext);
        selectTag.doStartTag();
        setComponentTagClearTagState(selectTag, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.setPageContext(pageContext);
        optGroupTag1.doStartTag();
        setComponentTagClearTagState(optGroupTag1, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag1.doEndTag();
        optGroupTag2.setPageContext(pageContext);
        optGroupTag2.doStartTag();
        setComponentTagClearTagState(optGroupTag2, true);  // Ensure component tag state clearing is set true (to match tag).
        optGroupTag2.doEndTag();
        selectTag.doEndTag();

        //System.out.println(writer.toString());
        verify(SelectTag.class.getResource("OptGroup-8.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        SelectTag freshTag = new SelectTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(selectTag, freshTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        OptGroupTag freshOptGroupTag = new OptGroupTag();
        freshOptGroupTag.setPerformClearTagStateForTagPoolingServers(true);
        freshOptGroupTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag1, freshOptGroupTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(optGroupTag2, freshOptGroupTag));
    }
}
