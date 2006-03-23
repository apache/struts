/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.util.FastByteArrayOutputStream;
import com.opensymphony.webwork.views.util.ContextUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.PrintWriter;


/**
 * Contains common functonalities for WebWork JSP Tags.
 * 
 * @author plightbo
 * @author tm_jee
 * Date: Oct 17, 2003
 * Time: 7:09:15 AM
 */
public class WebWorkBodyTagSupport extends BodyTagSupport {

    private static final long serialVersionUID = -1201668454354226175L;

    /**
     * @ww.tagattribute required="false" type="String"
     * description="The id of the tag element."
     */
    public void setId(String string) {
        super.setId(string);
    }
    
    protected boolean altSyntax() {
        return ContextUtil.isUseAltSyntax(getStack().getContext());
    }

    protected OgnlValueStack getStack() {
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
    
    public static String translateVariables(String expression, OgnlValueStack stack) {
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
