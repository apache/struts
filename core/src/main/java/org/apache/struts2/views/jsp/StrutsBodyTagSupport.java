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

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.PrintWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.struts2.util.ComponentUtils;
import org.apache.struts2.util.FastByteArrayOutputStream;


/**
 * Contains common functonalities for Struts JSP Tags.
 */
public class StrutsBodyTagSupport extends BodyTagSupport {

    private static final long serialVersionUID = -1201668454354226175L;

    private boolean performClearTagStateForTagPoolingServers = false;

    protected ValueStack getStack() {
        return TagUtils.getStack(pageContext);
    }

    protected String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    protected Object findValue(String expr) {
        expr = ComponentUtils.stripExpression(expr);

        return getStack().findValue(expr);
    }

    protected Object findValue(String expr, Class<?> toType) {
        if (toType == String.class) {
            return TextParseUtil.translateVariables('%', expr, getStack());
        } else {
            expr = ComponentUtils.stripExpression(expr);
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
     * Release state for a Struts JSP Tag handler.
     * 
     * According to the JSP API documentation, the page compiler guarantees that the release() method
     * will be invoked on the Tag handler before releasing it to the GC (garbage collector).  It does
     * not specify <em>when</em> the release() call will be made, though, and timing likely depends
     * on the implementation of the JSP/servlet engine being used.
     */
    @Override
    public void release() {
        // Ensure release() performs the clearTagStateForTagPoolingServers tag state clearing processing with
        // the clear state flag forced to true (if not already set to true), to ensure cleanup.
        // The performClearTagStateForTagPoolingServers flag state is preserved for consistency, in case 
        // release() is called by framework code.
        final boolean originalPerformClearTagState = getPerformClearTagStateForTagPoolingServers();
        if (originalPerformClearTagState == true) {
            clearTagStateForTagPoolingServers();
        } else {
            setPerformClearTagStateForTagPoolingServers(true);
            clearTagStateForTagPoolingServers();
            setPerformClearTagStateForTagPoolingServers(originalPerformClearTagState);
        }
        super.release();
    }

    /**
     * Request that the tag state be cleared during {@link StrutsBodyTagSupport#doEndTag()} processing,
     * which may help with certain edge cases with tag logic running on servers that implement JSP Tag Pooling.
     * 
     * <em>Note:</em> Even though the Tag classes extend this class {@link StrutsBodyTagSupport}, and this method
     * {@link StrutsBodyTagSupport#setPerformClearTagStateForTagPoolingServers(boolean)} exists in the method hierarchy,
     * the JSP processing requires us to explicitly override it in <em>every Tag class<em> in order for the Tag handler
     * method to be visible to the JSP processing. 
     * Defining a setter in the superclass alone is insufficient (results in "Cannot find a setter method for the attribute").
     * 
     * See {@link StrutsBodyTagSupport#clearTagStateForTagPoolingServers()} for additional details.
     * 
     * <em>Warning:</em> Setting this value to true may allow for the desired behaviour, but doing so
     * may violate the JSP specification.  <em>Set to true at your own risk</em>.
     * 
     * @param performClearTagStateForTagPoolingServers true if tag state should be cleared, false otherwise.
     */
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        this.performClearTagStateForTagPoolingServers = performClearTagStateForTagPoolingServers;
    }

    /**
     * Allow descendant tags to check if the tag state should be cleared during {@link StrutsBodyTagSupport#doEndTag()} processing,
     * 
     * @return true if tag state should be cleared, false (default) otherwise.
     */
    protected boolean getPerformClearTagStateForTagPoolingServers() {
        return this.performClearTagStateForTagPoolingServers;
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
        if (getPerformClearTagStateForTagPoolingServers() == false) {
            return;  // If flag is false (default setting), do not perform any state clearing.
        }
        this.setBodyContent(null);  // Always clear the tag body (if any) after tag completion.
        this.setId(null);           // Always clear the tag id (if any) after tag completion.
        // Note: The pageContext and parent Tag state are NOT cleared, only the "user-defined" tag state should be cleared.
        //       Calling setPageContext(null) and setParent(null) appears too dangerous to consider, and the container
        //       should always set them, even if a tag instance from a pool is re-used.  Also, clearing those two
        //       values likely violates the JSP specification.
        // Note: We intentionally do NOT reset performClearTagStateForTagPoolingServers to false here, for two reasons.
        //       Firstly, if a tag pool re-uses the instance, in order to qualify/match the tag parameters should be
        //       the same, including performClearTagStateForTagPoolingServers.  Secondly, if we change the state of
        //       the control flag during clearing, it makes unit testing virtually impossible.
    }

}
