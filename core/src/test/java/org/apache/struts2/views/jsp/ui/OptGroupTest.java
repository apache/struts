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
    }

    public void testOptGroupWithValueKey() throws Exception {
        SelectTag selectTag = new SelectTag();
        selectTag.setName("mySelection");
        selectTag.setLabel("My Selection");
        selectTag.setList("selectValues");
        selectTag.setListValueKey("valueKey");

        LocaleTestAction localeTestAction = new LocaleTestAction();
        
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
    }

}
