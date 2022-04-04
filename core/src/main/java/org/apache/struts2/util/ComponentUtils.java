package org.apache.struts2.util;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.util.ContextUtil;

/**
 * Various static methods used with components
 */
public class ComponentUtils {

    /**
     * If altSyntax (%{...}) is applied, simply strip the "%{" and "}" off.
     *
     * @param stack the ValueStack where the context value is searched for.
     * @param expr  the expression (must be not null)
     * @return the stripped expression if altSyntax is enabled. Otherwise
     *         the parameter expression is returned as is.
     */
    public static String stripExpressionIfAltSyntax(ValueStack stack, String expr) {
        if (altSyntax(stack)) {
            // does the expression start with %{ and end with }? if so, just cut it off!
            if (isExpression(expr)) {
                return expr.substring(2, expr.length() - 1);
            }
        }
        return expr;
    }

    /**
     * Is the altSyntax enabled? [TRUE]
     *
     * @param stack the ValueStack where the context value is searched for.
     * @return true if altSyntax is activated. False otherwise.
     *         See <code>struts.properties</code> where the altSyntax flag is defined.
     */
    public static boolean altSyntax(ValueStack stack) {
        return ContextUtil.isUseAltSyntax(stack.getContext());
    }

    /**
     * Check if object is expression base on altSyntax
     *
     * @param expr to treat as an expression
     * @return true if it is an expression
     */
    public static boolean isExpression(String expr) {
        return expr.startsWith("%{") && expr.endsWith("}");
    }

    public static boolean containsExpression(String expr) {
        return expr.contains("%{") && expr.contains("}");
    }

}
