/*
 * $Id$
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
package org.apache.struts2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Writer;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <p>Perform basic condition flow. 'If' tag could be used by itself or
 * with 'Else If' Tag and/or single/multiple 'Else' Tag.</p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 *  <li>test* (Boolean) - Logic to determined if body of tag is to be displayed</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *  &lt;a:if test="%{false}"&gt;
 *	    &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/a:if&gt;
 * 	&lt;a:elseif test="%{true}"&gt;
 *	    &lt;div&gt;Will Be Executed&lt;/div&gt;
 *  &lt;/a:elseif&gt;
 *  &lt;a:else&gt;
 *	    &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/a:else&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see Else
 * @see ElseIf
 *
 * @a2.tag name="if" tld-body-content="JSP" description="If tag" tld-tag-class="org.apache.struts2.views.jsp.IfTag"
 */
public class If extends Component {
    public static final String ANSWER = "struts.if.answer";

    Boolean answer;
    String test;

    /**
     * Expression to determine if body of tag is to be displayed
     * @a2.tagattribute required="true" type="Boolean"
     */
    public void setTest(String test) {
        this.test = test;
    }

    public If(OgnlValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        answer = (Boolean) findValue(test, Boolean.class);

        if (answer == null) {
            answer = Boolean.FALSE;
        }

        return answer.booleanValue();
    }

    public boolean end(Writer writer, String body) {
        stack.getContext().put(ANSWER, answer);

        return super.end(writer, body);
    }
}
