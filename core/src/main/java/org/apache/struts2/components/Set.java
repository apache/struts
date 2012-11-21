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
 * <p>The set tag assigns a value to a variable in a specified scope. It is useful when you wish to assign a variable to a
 * complex expression and then simply reference that variable each time rather than the complex expression. This is
 * useful in both cases: when the complex expression takes time (performance improvement) or is hard to read (code
 * readability improvement).</p>
 * <p>If the tag is used with body content, the evaluation of the value parameter is omitted. Instead, the String to
 * which the body evaluates is set as value for the scoped variable.</p>
 *
 * The scopes available are as follows :-
 * <ul>
 *   <li>application - the value will be set in application scope according to servlet spec. using the name as its key</li>
 *   <li>session - the value will be set in session scope according to servlet spec. using the name as key </li>
 *   <li>request - the value will be set in request scope according to servlet spec. using the name as key </li>
 *   <li>page - the value will be set in page scope according to servlet sepc. using the name as key</li>
 *   <li>action - the value will be set in the request scope and Struts' action context using the name as key</li>
 * </ul>
 *
 * NOTE:<p/>
 * If no scope is specified, it will default to action scope.
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
 * &lt;s:set var="personName" value="person.name"/&gt;
 * Hello, &lt;s:property value="#personName"/&gt;
 *
 * &lt;s:set var="janesName"&gt;Jane Doe&lt;/s:set&gt;
 * &lt;s:property value="#janesName"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="set", tldBodyContent="JSP", tldTagClass="org.apache.struts2.views.jsp.SetTag", description="Assigns a value to a variable in a specified scope")
public class Set extends ContextBean {
    protected String scope;
    protected String value;

    public Set(ValueStack stack) {
        super(stack);
    }

    public boolean end(Writer writer, String body) {
        ValueStack stack = getStack();

        Object o;
        if (value == null) {
            if (body != null && !body.equals("")) {
                o = body;
            } else {
                o = findValue("top");
            }
        } else {
            o = findValue(value);
        }

        body="";

        if ("application".equalsIgnoreCase(scope)) {
            stack.setValue("#application['" + getVar() + "']", o);
        } else if ("session".equalsIgnoreCase(scope)) {
            stack.setValue("#session['" + getVar() + "']", o);
        } else if ("request".equalsIgnoreCase(scope)) {
            stack.setValue("#request['" + getVar() + "']", o);
        } else if ("page".equalsIgnoreCase(scope)) {
            stack.setValue("#attr['" + getVar() + "']", o, false);
        } else {
            stack.getContext().put(getVar(), o);
            stack.setValue("#attr['" + getVar() + "']", o, false);
        }

        return super.end(writer, body);
    }

    /*
     * TODO: set required=true when 'id' is dropped after 2.1
     */
    @StrutsTagAttribute(description="Name used to reference the value pushed into the Value Stack")
    public void setVar(String var) {
       super.setVar(var);
    }

    @StrutsTagAttribute(description="Deprecated. Use 'var' instead")
    public void setName(String name) {
        setVar(name);
    }

    @StrutsTagAttribute(description="The scope in which to assign the variable. Can be <b>application</b>" +
                ", <b>session</b>, <b>request</b>, <b>page</b>, or <b>action</b>.", defaultValue="action")
    public void setScope(String scope) {
        this.scope = scope;
    }

    @StrutsTagAttribute(description="The value that is assigned to the variable named <i>name</i>")
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean usesBody() {
        return true;
    }
}
