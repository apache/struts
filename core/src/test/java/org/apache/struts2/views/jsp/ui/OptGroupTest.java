/*
 * $Id: StrutsModels.java 418521 2006-07-01 23:36:50Z tmjee $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.views.jsp.AbstractUITagTest;

/**
 * 
 */
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
}
