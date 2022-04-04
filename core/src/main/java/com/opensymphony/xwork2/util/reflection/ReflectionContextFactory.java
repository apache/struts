package com.opensymphony.xwork2.util.reflection;

import java.util.Map;

public interface ReflectionContextFactory {
    /**
     * Creates and returns a new standard naming context for evaluating an OGNL
     * expression.
     *
     * @param root the root of the object graph
     * @return a new Map with the keys <CODE>root</CODE> and <CODE>context</CODE>
     *         set appropriately
     */
    Map createDefaultContext( Object root );
}
