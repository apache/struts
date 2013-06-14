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
 * <p>Push value on stack for simplified usage.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 *      <li>value* (Object) - value to be pushed into the top of the stack</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;s:push value="user"&gt;
 *     &lt;s:propery value="firstName" /&gt;
 *     &lt;s:propery value="lastName" /&gt;
 * &lt;/s:push&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 * <!-- START SNIPPET: example1description -->
 * Pushed user into the stack, and hence property tag could access user's properties
 * (firstName, lastName etc) since user is now at the top of the stack
 * <!-- END SNIPPET: example1description -->
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 *  &lt;s:push value="myObject"&gt;                              ----- (1)
 *       &lt;s:bean name="jp.SomeBean" var="myBean"/&gt;        ----- (2)
 *          &lt;s:param name="myParam" value="top"/&gt;        ----- (3)
 *       &lt;/s:bean&gt;
 *   &lt;/s:push&gt;
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
 * &lt;s:push value="myObject"&gt;                                       ---(A)
 *    &lt;s:bean name="jp.SomeBean" var="myBean"/&gt;                   ---(B)
 *       &lt;s:param name="myParam" value="top.mySomeOtherValue"/&gt;  ---(C)
 *    &lt;/s:bean&gt;
 * &lt;/s:push&gt;
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
 * &lt;s:push value="myObject"&gt;                                 ---- (i)
 *    &lt;s:bean name="jp.SomeBean" var="myBean"/&gt;             ---- (ii)
 *       &lt;s:param name="myParam" value="[1].top"/&gt;         -----(iii)
 *    &lt;/s:bean&gt;
 * &lt;/s:push&gt;
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
 */
@StrutsTag(name="push", tldTagClass="org.apache.struts2.views.jsp.PushTag", description="Push value on stack for simplified usage.")
public class Push extends Component {
    protected String value;
    protected boolean pushed;

    public Push(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        ValueStack stack = getStack();

        if (stack != null) {
            stack.push(findValue(value, "value", "You must specify a value to push on the stack. Example: person"));
            pushed = true;
        } else {
            pushed = false; // need to ensure push is assigned, otherwise we may have a leftover value
        }

        return result;
    }

    public boolean end(Writer writer, String body) {
        ValueStack stack = getStack();

        if (pushed && (stack != null)) {
            stack.pop();
        }

        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="Value to push on stack", required=true)
    public void setValue(String value) {
        this.value = value;
    }
    
}
