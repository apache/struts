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
package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Writer;

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
 *  &lt;a:if test="%{false}"&gt;
 * 	    &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/a:if&gt;
 * 	&lt;a:elseif test="%{true}"&gt;
 * 	    &lt;div&gt;Will Be Executed&lt;/div&gt;
 *  &lt;/a:elseif&gt;
 *  &lt;a:else&gt;
 * 	    &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/a:else&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rick Salsa (rsal@mb.sympatico.ca)
 * @author tmjee
 * @a2.tag name="elseif" tld-body-content="JSP" description="Elseif tag"  tld-tag-class="org.apache.struts.action2.views.jsp.ElseIfTag"
 */
public class ElseIf extends Component {
    public ElseIf(OgnlValueStack stack) {
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

        return answer != null && answer.booleanValue();
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

    /**
     * Expression to determine if body of tag is to be displayed
     * @a2.tagattribute required="true" type="Boolean"
     */
    public void setTest(String test) {
        this.test = test;
    }
}
