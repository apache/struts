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
package com.opensymphony.xwork2.util;

import org.apache.commons.lang3.StringUtils;

/**
 * OGNL implementation of {@link TextParser}
 */
public class OgnlTextParser implements TextParser {

    public Object evaluate(char[] openChars, String expression, TextParseUtil.ParsedValueEvaluator evaluator, int maxLoopCount) {
        // deal with the "pure" expressions first!
        //expression = expression.trim();
        Object result = expression = (expression == null) ? "" : expression;
        int pos = 0;

        for (char open : openChars) {
            int loopCount = 1;
            //this creates an implicit StringBuffer and shouldn't be used in the inner loop
            final String lookupChars = open + "{";

            while (true) {
                int start = expression.indexOf(lookupChars, pos);
                if (start == -1) {
                    loopCount++;
                    start = expression.indexOf(lookupChars);
                }
                if (loopCount > maxLoopCount) {
                    // translateVariables prevent infinite loop / expression recursive evaluation
                    break;
                }
                int length = expression.length();
                int x = start + 2;
                int end;
                char c;
                int count = 1;
                while (start != -1 && x < length && count != 0) {
                    c = expression.charAt(x++);
                    if (c == '{') {
                        count++;
                    } else if (c == '}') {
                        count--;
                    }
                }
                end = x - 1;

                if ((start != -1) && (end != -1) && (count == 0)) {
                    String var = expression.substring(start + 2, end);

                    Object o = evaluator.evaluate(var);

                    String left = expression.substring(0, start);
                    String right = expression.substring(end + 1);
                    String middle = null;
                    if (o != null) {
                        middle = o.toString();
                        if (StringUtils.isEmpty(left)) {
                            result = o;
                        } else {
                            result = left.concat(middle);
                        }

                        if (StringUtils.isNotEmpty(right)) {
                            result = result.toString().concat(right);
                        }

                        expression = left.concat(middle).concat(right);
                    } else {
                        // the variable doesn't exist, so don't display anything
                        expression = left.concat(right);
                        result = expression;
                    }
                    pos = (left != null && left.length() > 0 ? left.length() - 1: 0) +
                            (middle != null && middle.length() > 0 ? middle.length() - 1: 0) +
                            1;
                    pos = Math.max(pos, 1);
                } else {
                    break;
                }
            }
        }
        return result;
    }
}
