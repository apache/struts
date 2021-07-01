/*
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
package org.apache.struts2.views.jsp;

import java.io.PrintWriter;

import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts2.util.ComponentUtils;
import org.apache.struts2.util.FastByteArrayOutputStream;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.jsp.JspException;

/**
 * Contains common functonalities for Struts JSP Tags.
 *
 */
public class StrutsBodyTagSupport extends BodyTagSupport {

    private static final long serialVersionUID = -1201668454354226175L;

    protected ValueStack getStack() {
        return TagUtils.getStack(pageContext);
    }

    protected String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    protected Object findValue(String expr) {
    	expr = ComponentUtils.stripExpressionIfAltSyntax(getStack(), expr);

        return getStack().findValue(expr);
    }

    protected Object findValue(String expr, Class toType) {
        if (ComponentUtils.altSyntax(getStack()) && toType == String.class) {
        	return TextParseUtil.translateVariables('%', expr, getStack());
            //return translateVariables(expr, getStack());
        } else {
        	expr = ComponentUtils.stripExpressionIfAltSyntax(getStack(), expr);

            return getStack().findValue(expr, toType);
        }
    }

    protected String toString(Throwable t) {
        try (FastByteArrayOutputStream bout = new FastByteArrayOutputStream();
                PrintWriter wrt = new PrintWriter(bout)) {
            t.printStackTrace(wrt);

            return bout.toString();
        }
    }

    protected String getBody() {
        if (bodyContent == null) {
            return "";
        } else {
            return bodyContent.getString().trim();
        }
    }

    @Override
    public int doEndTag() throws JspException {
        clearTagStateForTagPoolingServers();
        return super.doEndTag();
    }

    /**
     * Provide a mechanism to clear tag state, to handle servlet container JSP tag pooling
     * behaviour with some servers, such as Glassfish.
     * 
     * Usage: Override this method in descendant classes to clear any state that might cause issues should the 
     *        servlet container re-use a cached instance of the tag object.  If the descendant class does not
     *        declare any new field members then it should not be strictly necessary to call this method there.
     *        Typically that means calling the ancestor's {@link ComponentTagSupport#clearTagStateForTagPoolingServers()}
     *        method first, then resetting instance variables at the current level to their default state.
     * 
     * Note: If the descendant overrides {@link StrutsBodyTagSupport#doEndTag()}, and does not call
     *       super.doEndTag(), then the descendant should call this method in the descendant doEndTag() method
     *       to ensure consistent clearing of tag state.
     */
    protected void clearTagStateForTagPoolingServers() {
        // Default implementation.
        this.setBodyContent(null);  // Always clear the tag body (if any) after tag completion.
        this.setId(null);           // Always clear the tag id (if any) after tag completion.
        // Note: The pageContext and parent Tag state are NOT cleared, only the "user-defined" tag state should be cleared.
        //       Calling setPageContext(null) and setParent(null) appears too dangerous to consider, and the container
        //       should always set them, even if a tag instance from a pool is re-used.
    }

}
