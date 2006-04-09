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
package org.apache.struts.action2.dispatcher;

import org.apache.struts.action2.StrutsStatics;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.util.TextParseUtil;


/**
 * A base class for all Struts action execution results.
 * The "location" param is the default parameter, meaning the most common usage of this result would be:
 * <p/>
 * This class provides two common parameters for any subclass:
 * <ul>
 * <li>location - the location to go to after execution (could be a jsp page or another action).
 * It can be parsed as per the rules definied in the
 * {@link TextParseUtil#translateVariables(java.lang.String, com.opensymphony.xwork.util.OgnlValueStack) translateVariables}
 * method</li>
 * <li>parse - true by default. If set to false, the location param will not be parsed for expressions</li>
 * </ul>
 * <p/>
 * In the xwork.xml configuration file, these would be included as:
 * <p/>
 * <pre>
 *  &lt;result name="success" type="redirect"&gt;
 *      &lt;param name="<b>location</b>"&gt;foo.jsp&lt;/param&gt;
 *  &lt;/result&gt;</pre>
 * <p/>
 * or
 * <p/>
 * <pre>
 *  &lt;result name="success" type="redirect" &gt;
 *      &lt;param name="<b>location</b>"&gt;foo.jsp&lt;/param&gt;
 *      &lt;param name="<b>parse</b>"&gt;false&lt;/param&gt;
 *  &lt;/result&gt;</pre>
 * <p/>
 * or when using the default parameter feature
 * <p/>
 * <pre>
 *  &lt;result name="success" type="redirect"&gt;<b>foo.jsp</b>&lt;/result&gt;</pre>
 * <p/>
 * You should subclass this class if you're interested in adding more parameters or functionality
 * to your Result. If you do subclass this class you will need to
 * override {@link #doExecute(String, ActionInvocation)}.<p>
 * <p/>
 * Any custom result can be defined in xwork.xml as:
 * <p/>
 * <pre>
 *  &lt;result-types&gt;
 *      ...
 *      &lt;result-type name="myresult" class="com.foo.MyResult" /&gt;
 *  &lt;/result-types&gt;</pre>
 * <p/>
 * Please see the {@link com.opensymphony.xwork.Result} class for more info on Results in general.
 *
 * @author Jason Carreira
 * @author Bill Lynch (docs)
 * @see com.opensymphony.xwork.Result
 */
public abstract class StrutsResultSupport implements Result, StrutsStatics {
    public static final String DEFAULT_PARAM = "location";

    protected boolean parse = true;
    protected String location;

    /**
     * The location to go to after action execution. This could be a JSP page or another action.
     * The location can contain OGNL expressions which will be evaulated if the <tt>parse</tt>
     * parameter is set to <tt>true</tt>.
     *
     * @param location the location to go to after action execution.
     * @see #setParse(boolean)
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Set parse to <tt>true</tt> to indicate that the location should be parsed as an OGNL expression. This
     * is set to <tt>true</tt> by default.
     *
     * @param parse <tt>true</tt> if the location parameter is an OGNL expression, <tt>false</tt> otherwise.
     */
    public void setParse(boolean parse) {
        this.parse = parse;
    }

    /**
     * Implementation of the <tt>execute</tt> method from the <tt>Result</tt> interface. This will call
     * the abstract method {@link #doExecute(String, ActionInvocation)} after optionally evaluating the
     * location as an OGNL evaluation.
     *
     * @param invocation the execution state of the action.
     * @throws Exception if an error occurs while executing the result.
     */
    public void execute(ActionInvocation invocation) throws Exception {
        doExecute(conditionalParse(location, invocation), invocation);
    }

    protected String conditionalParse(String param, ActionInvocation invocation) {
        if (parse && param != null && invocation != null) {
            return TextParseUtil.translateVariables(param, invocation.getStack());
        } else {
            return param;
        }
    }

    /**
     * Executes the result given a final location (jsp page, action, etc) and the action invocation
     * (the state in which the action was executed). Subclasses must implement this class to handle
     * custom logic for result handling.
     *
     * @param finalLocation the location (jsp page, action, etc) to go to.
     * @param invocation    the execution state of the action.
     * @throws Exception if an error occurs while executing the result.
     */
    protected abstract void doExecute(String finalLocation, ActionInvocation invocation) throws Exception;
}
