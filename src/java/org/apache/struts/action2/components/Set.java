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
 * <p>The set tag assigns a value to a variable in a specified scope. It is useful when you wish to assign a variable to a
 * complex expression and then simply reference that variable each time rather than the complex expression. This is
 * useful in both cases: when the complex expression takes time (performance improvement) or is hard to read (code
 * readability improvement).</P>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Parameters</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li>name* (String): The name of the new variable that is assigned the value of <i>value</i></li>
 *
 * <li>value (Object): The value that is assigned to the variable named <i>name</i></li>
 *
 * <li>scope (String): The scope in which to assign the variable. Can be <b>application</b>, <b>session</b>,
 * <b>request</b>, <b>page</b>, or <b>action</b>. By default it is <b>action</b>.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:set name="personName" value="person.name"/&gt;
 * Hello, &lt;a:property value="#personName"/&gt;. How are you?
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision$
 * @since 2.2
 *
 * @a2.tag name="set" tld-body-content="empty" tld-tag-class="org.apache.struts.action2.views.jsp.SetTag"
 * description="Assigns a value to a variable in a specified scope"
 */
public class Set extends Component {
    protected String name;
    protected String scope;
    protected String value;

    public Set(OgnlValueStack stack) {
        super(stack);
    }

    public boolean end(Writer writer, String body) {
        OgnlValueStack stack = getStack();

        if (value == null) {
            value = "top";
        }

        Object o = findValue(value);

        String name;
        if (altSyntax()) {
            name = findString(this.name, "name", "Name is required");
        } else {
            name = this.name;

            if (this.name == null) {
                throw fieldError("name", "Name is required", null);
            }
        }

        if ("application".equalsIgnoreCase(scope)) {
            stack.setValue("#application['" + name + "']", o);
        } else if ("session".equalsIgnoreCase(scope)) {
            stack.setValue("#session['" + name + "']", o);
        } else if ("request".equalsIgnoreCase(scope)) {
            stack.setValue("#request['" + name + "']", o);
        } else if ("page".equalsIgnoreCase(scope)) {
            stack.setValue("#attr['" + name + "']", o, false);
        } else {
            stack.getContext().put(name, o);
        }

        return super.end(writer, body);
    }

    /**
     * The name of the new variable that is assigned the value of <i>value</i>
     * @a2.tagattribute required="true" type="String"
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The scope in which to assign the variable. Can be <b>application</b>, <b>session</b>, <b>request</b>, <b>page</b>, or <b>action</b>.
     * @a2.tagattribute required="false" type="String" default="action"
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * The value that is assigned to the variable named <i>name</i>
     * @a2.tagattribute required="false"
     */
    public void setValue(String value) {
        this.value = value;
    }
}
