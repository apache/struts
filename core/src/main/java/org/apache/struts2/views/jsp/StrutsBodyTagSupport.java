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
package org.apache.struts2.views.jsp;

import java.io.PrintWriter;

import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts2.util.FastByteArrayOutputStream;
import org.apache.struts2.views.util.ContextUtil;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * Contains common functonalities for Struts JSP Tags.
 * 
 */
public class StrutsBodyTagSupport extends BodyTagSupport {

    private static final long serialVersionUID = -1201668454354226175L;

    /**
     * @s.tagattribute required="false" type="String"
     * description="The id of the tag element."
     */
    public void setId(String string) {
        super.setId(string);
    }
    
    protected boolean altSyntax() {
        return ContextUtil.isUseAltSyntax(getStack().getContext());
    }

    protected ValueStack getStack() {
        return TagUtils.getStack(pageContext);
    }

    protected String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    protected Object findValue(String expr) {
        if (altSyntax()) {
            // does the expression start with %{ and end with }? if so, just cut it off!
            if (expr.startsWith("%{") && expr.endsWith("}")) {
                expr = expr.substring(2, expr.length() - 1);
            }
        }

        return getStack().findValue(expr);
    }

    protected Object findValue(String expr, Class toType) {
        if (altSyntax() && toType == String.class) {
            return translateVariables(expr, getStack());
        } else {
            if (altSyntax()) {
                // does the expression start with %{ and end with }? if so, just cut it off!
                if (expr.startsWith("%{") && expr.endsWith("}")) {
                    expr = expr.substring(2, expr.length() - 1);
                }
            }

            return getStack().findValue(expr, toType);
        }
    }

    protected String toString(Throwable t) {
        FastByteArrayOutputStream bout = new FastByteArrayOutputStream();
        PrintWriter wrt = new PrintWriter(bout);
        t.printStackTrace(wrt);
        wrt.close();

        return bout.toString();
    }

    protected String getBody() {
        if (bodyContent == null) {
            return "";
        } else {
            return bodyContent.getString().trim();
        }
    }
    
    public static String translateVariables(String expression, ValueStack stack) {
        while (true) {
            int x = expression.indexOf("%{");
            int y = expression.indexOf("}", x);

            if ((x != -1) && (y != -1)) {
                String var = expression.substring(x + 2, y);

                Object o = stack.findValue(var, String.class);

                if (o != null) {
                    expression = expression.substring(0, x) + o + expression.substring(y + 1);
                } else {
                    // the variable doesn't exist, so don't display anything
                    expression = expression.substring(0, x) + expression.substring(y + 1);
                }
            } else {
                break;
            }
        }

        return expression;
    }
}
