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
 * <p>Push value on stack for simplified usage.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 * 		<li>value* (Object) - value to be pushed into the top of the stack</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;a:push value="user"&gt;
 *     &lt;a:propery value="firstName" /&gt;
 *     &lt;a:propery value="lastName" /&gt;
 * &lt;/a:push&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 * 
 * <!-- START SNIPPET: example1description -->
 * Pushed user into the stack, and hence property tag could access user's properties 
 * (firstName, lastName etc) since user is not at the top of the stack
 * <!-- END SNIPPET: example1description -->
 * 
 * <pre>
 * <!-- START SNIPPET: example2 -->
 *  &lt;a:push value="myObject"&gt;                              ----- (1)
 *       &lt;a:bean name="jp.SomeBean" id="myBean"/&gt;        ----- (2)
 * 		    &lt;a:param name="myParam" value="top"/&gt;        ----- (3)
 *       &lt;/a:bean&gt;
 *   &lt;/a:push&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 * 
 * <pre>
 * <!-- START SNIPPET: example2description -->
 * when in (1), myObject is at the top of the stack
 * when in (2), jp.SomeBean is in the top of stack, also in stack's context with key myBean
 * when in (3), top will get the jp.SomeBean instance
 * <!-- END SNIPPET: example2description -->
 * </pre>
 * 
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;a:push value="myObject"&gt;                                       ---(A)
 *    &lt;a:bean name="jp.SomeBean" id="myBean"/&gt;                   ---(B)
 *       &lt;a:param name="myParam" value="top.mySomeOtherValue"/&gt;  ---(C)
 *    &lt;/a:bean&gt;
 * &lt;/a:push&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 * 
 * <pre>
 * <!-- START SNIPPET: example3description -->
 * when in (A), myObject is at the top of the stack
 * when in (B), jp.SomeBean is at the top of the stack, also in context with key myBean
 * when in (C), top refers to jp.SomeBean instance. so top.mySomeOtherValue would invoke SomeBean's mySomeOtherValue() method
 * <!-- END SNIPPET: example3description -->
 * </pre>
 * 
 * <pre>
 * <!-- START SNIPPET: example4 -->       
 * &lt;a:push value="myObject"&gt;                                 ---- (i)
 *    &lt;a:bean name="jp.SomeBean" id="myBean"/&gt;             ---- (ii)
 *       &lt;a:param name="myParam" value="[1].top"/&gt;         -----(iii)
 *    &lt;/a:bean&gt;
 * &lt;/a:push&gt;
 * <!-- END SNIPPET: example4 -->
 * </pre>
 * 
 * <pre>
 * <!-- START SNIPPET: example4description -->
 * when in (i), myObject is at the top of the stack
 * when in (ii), jp.SomeBean is at the top of the stack, followed by myObject
 * when in (iii), [1].top will returned top of the cut of stack starting from myObject, namely myObject itself 
 * <!-- END SNIPPET: example4description -->
 * </pre>
 * 
 * @a2.tag name="push" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.PushTag"
 * description="Push value on stack for simplified usage."
 */
public class Push extends Component {
    protected String value;
    protected boolean pushed;

    public Push(OgnlValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        OgnlValueStack stack = getStack();

        if (stack != null) {
            stack.push(findValue(value, "value", "You must specify a value to push on the stack. Example: person"));
            pushed = true;
        } else {
            pushed = false; // need to ensure push is assigned, otherwise we may have a leftover value
        }

        return result;
    }

    public boolean end(Writer writer, String body) {
        OgnlValueStack stack = getStack();

        if (pushed && (stack != null)) {
            stack.pop();
        }

        return super.end(writer, body);
    }

    /**
     * Value to push on stack
     * @a2.tagattribute required="true"
     */
    public void setValue(String value) {
        this.value = value;
    }
}
