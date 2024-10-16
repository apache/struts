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
package org.apache.struts2.ognl;

import org.apache.struts2.ognl.OgnlUtil;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * Guards all expressions parsed by Struts Core. It is evaluated by {@link OgnlUtil} immediately after parsing any
 * expression.
 *
 * @since 6.4.0
 */
public interface OgnlGuard {

    String EXPR_BLOCKED = "_ognl_guard_blocked";

    /**
     * Determines whether an OGNL expression should be blocked based on validation done on both the raw expression and
     * the parsed tree.
     *
     * @param expr OGNL expression
     * @return whether the expression should be blocked
     */
    default boolean isBlocked(String expr) throws OgnlException {
        return EXPR_BLOCKED.equals(parseExpression(expr));
    }

    /**
     * Parses an OGNL expression and returns the resulting tree only if the expression is not blocked as per defined
     * validation rules in {@link #isRawExpressionBlocked} and {@link #isParsedTreeBlocked}.
     *
     * @param expr OGNL expression
     * @return parsed expression or {@link #EXPR_BLOCKED} if the expression should be blocked
     */
    default Object parseExpression(String expr) throws OgnlException {
        if (isRawExpressionBlocked(expr)) {
            return EXPR_BLOCKED;
        }
        Object tree = Ognl.parseExpression(expr);
        if (isParsedTreeBlocked(tree)) {
            return EXPR_BLOCKED;
        }
        return tree;
    }

    /**
     * Determines whether an OGNL expression should be blocked based on validation done on only the raw expression,
     * without parsing the tree.
     *
     * @param expr OGNL expression
     * @return whether the expression should be blocked
     */
    boolean isRawExpressionBlocked(String expr);

    /**
     * Determines whether a parsed OGNL tree should be blocked based on some validation rules.
     *
     * @param tree parsed OGNL tree
     * @return whether the parsed tree should be blocked
     */
    boolean isParsedTreeBlocked(Object tree);
}
