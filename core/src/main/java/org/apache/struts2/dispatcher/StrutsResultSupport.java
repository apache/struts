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

package org.apache.struts2.dispatcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A base class for all Struts action execution results.
 * The "location" param is the default parameter, meaning the most common usage of this result would be:
 * <p/>
 * This class provides two common parameters for any subclass:
 * <ul>
 * <li>location - the location to go to after execution (could be a jsp page or another action).
 * It can be parsed as per the rules definied in the
 * {@link TextParseUtil#translateVariables(java.lang.String, com.opensymphony.xwork2.util.ValueStack) translateVariables}
 * method</li>
 * <li>parse - true by default. If set to false, the location param will not be parsed for expressions</li>
 * <li>encode - false by default. If set to false, the location param will not be url encoded. This only have effect when parse is true</li>
 * </ul>
 *
 * <b>NOTE:</b>
 * The encode param will only have effect when parse is true
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/>
 *
 * <!-- START SNIPPET: example -->
 *
 * <p/>
 * In the struts.xml configuration file, these would be included as:
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
 *      &lt;param name="<b>location</b>"&gt;foo.jsp?url=${myUrl}&lt;/param&gt;
 *      &lt;param name="<b>parse</b>"&gt;true&lt;/param&gt;
 *      &lt;param name="<b>encode</b>"&gt;true&lt;/param&gt;
 *  &lt;/result&gt;</pre>
 * <p/>
 * In the above case, myUrl will be parsed against Ognl Value Stack and then
 * URL encoded.
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
 * Any custom result can be defined in struts.xml as:
 * <p/>
 * <pre>
 *  &lt;result-types&gt;
 *      ...
 *      &lt;result-type name="myresult" class="com.foo.MyResult" /&gt;
 *  &lt;/result-types&gt;</pre>
 * <p/>
 * Please see the {@link com.opensymphony.xwork2.Result} class for more info on Results in general.
 *
 * <!-- END SNIPPET: example -->
 *
 * @see com.opensymphony.xwork2.Result
 */
public abstract class StrutsResultSupport implements Result, StrutsStatics {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsResultSupport.class);

    /** The default parameter */
    public static final String DEFAULT_PARAM = "location";

    /** use UTF-8 as this is the recommended encoding by W3C to avoid incompatibilities. */
    public static final String DEFAULT_URL_ENCODING = "UTF-8";

    private boolean parse;
    private boolean encode;
    private String location;
    private String lastFinalLocation;

    protected boolean parseLocation = true;

    public StrutsResultSupport() {
        this(null, true, false);
    }

    public StrutsResultSupport(String location) {
        this(location, true, false);
    }

    public StrutsResultSupport(String location, boolean parse, boolean encode) {
        this.location = location;
        this.parse = parse;
        this.encode = encode;
    }

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
     * Gets the location it was created with, mainly for testing
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the last parsed and encoded location value
     */
    public String getLastFinalLocation() {
        return lastFinalLocation;
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
     * Set encode to <tt>true</tt> to indicate that the location should be url encoded. This is set to
     * <tt>true</tt> by default
     *
     * @param encode <tt>true</tt> if the location parameter should be url encode, <tt>false</tt> otherwise.
     */
    public void setEncode(boolean encode) {
        this.encode = encode;
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
        lastFinalLocation = parseLocation ? conditionalParse(location, invocation) : location;
        doExecute(lastFinalLocation, invocation);
    }

    /**
     * Parses the parameter for OGNL expressions against the valuestack
     *
     * @param param The parameter value
     * @param invocation The action invocation instance
     * @return The resulting string
     */
    protected String conditionalParse(String param, ActionInvocation invocation) {
        if (parse && param != null && invocation != null) {
            return TextParseUtil.translateVariables(
                param, 
                invocation.getStack(),
                new EncodingParsedValueEvaluator());
        } else {
            return param;
        }
    }

    /**
     * As {@link #conditionalParse(String, ActionInvocation)} but does not
     * convert found object into String. If found object is a collection it is
     * returned if found object is not a collection it is wrapped in one.
     * 
     * @param param
     * @param invocation
     * @param excludeEmptyElements
     * @return
     */
    protected Collection<String> conditionalParseCollection(String param, ActionInvocation invocation, boolean excludeEmptyElements) {
        if (parse && param != null && invocation != null) {
            return TextParseUtil.translateVariablesCollection(
                param, 
                invocation.getStack(),
                excludeEmptyElements,
                new EncodingParsedValueEvaluator());
        } else {
            Collection<String> collection = new ArrayList<String>(1);
            collection.add(param);
            return collection;
        }
    }

    /**
     * {@link com.opensymphony.xwork2.util.TextParseUtil.ParsedValueEvaluator} to do URL encoding for found values. To be
     * used for single strings or collections.
     * 
     */
    private final class EncodingParsedValueEvaluator implements TextParseUtil.ParsedValueEvaluator {
        public Object evaluate(String parsedValue) {
            if (encode) {
                if (parsedValue != null) {
                    try {
                        return URLEncoder.encode(parsedValue, DEFAULT_URL_ENCODING);
                    }
                    catch(UnsupportedEncodingException e) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("error while trying to encode ["+parsedValue+"]", e);
                        }
                    }
                }
            }
            return parsedValue;
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
