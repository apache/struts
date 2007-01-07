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
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.annotations.StrutsTag;
import org.apache.struts.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders timepicker element.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * @version $Date$ $Id$
 */
public class TimePicker extends TextField {

  	final public static String TEMPLATE = "timepicker";

     protected String useDefaultTime;
     protected String useDefaultMinutes;
     protected String language;

     public TimePicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
  		super(stack, request, response);
  	}

  	protected void evaluateExtraParams() {
  		super.evaluateExtraParams();

 		if(useDefaultTime != null)
             addParameter("useDefaultTime", findValue(useDefaultTime, Boolean.class));
         if(useDefaultMinutes != null)
             addParameter("useDefaultMinutes", findValue(useDefaultMinutes, Boolean.class));
         if(language != null)
             addParameter("language", findString(language));
         if(value != null)
             addParameter("value", findString(value));
     }

     protected String getDefaultTemplate() {
         return TEMPLATE;
     }

     @StrutsTagAttribute(description="Set default minutes to current time", type="Boolean", defaultValue="false")
     public void setUseDefaultMinutes(String useDefaultMinutes) {
         this.useDefaultMinutes = useDefaultMinutes;
     }

     @StrutsTagAttribute(description="Set time to current time", type="Boolean", defaultValue="true")
     public void setUseDefaultTime(String useDefaultTime) {
         this.useDefaultTime = useDefaultTime;
     }

     @StrutsTagAttribute(description="Language to display this widget in", defaultValue="brower's specified preferred language")
     public void setLanguage(String language) {
         this.language = language;
     }

}
