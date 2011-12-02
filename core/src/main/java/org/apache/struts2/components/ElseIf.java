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

import java.io.Writer;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <p>Perform basic condition flow. 'If' tag could be used by itself or with 'Else If' Tag and/or single/multiple 'Else'
 * Tag.</p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li>test* (Boolean) - Logic to determined if body of tag is to be displayed</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *  &lt;s:if test="%{false}"&gt;
 *      &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/s:if&gt;
 *  &lt;s:elseif test="%{true}"&gt;
 *      &lt;div&gt;Will Be Executed&lt;/div&gt;
 *  &lt;/s:elseif&gt;
 *  &lt;s:else&gt;
 *      &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/s:else&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="elseif", tldTagClass="org.apache.struts2.views.jsp.ElseIfTag", description="Elseif tag")
public class ElseIf extends Component {
    public ElseIf(ValueStack stack) {
        super(stack);
    }

    protected Boolean answer;
    protected String test;

    public boolean start(Writer writer) {
        Boolean ifResult = (Boolean) stack.getContext().get(If.ANSWER);

        if ((ifResult == null) || (ifResult.booleanValue())) {
            return false;
        }

        //make the comparision
        answer = (Boolean) findValue(test, Boolean.class);

        if (answer == null) {
            answer = Boolean.FALSE;
        }
        if (answer.booleanValue()) {
            stack.getContext().put(If.ANSWER, answer);
        }
        return answer.booleanValue();
    }

    public boolean end(Writer writer, String body) {
        if (answer == null) {
            answer = Boolean.FALSE;
        }
        if (answer.booleanValue()) {
            stack.getContext().put(If.ANSWER, answer);
        }
        return super.end(writer, "");
    }

    @StrutsTagAttribute(description="Expression to determine if body of tag is to be displayed", type="Boolean", required=true)
    public void setTest(String test) {
        this.test = test;
    }
}
