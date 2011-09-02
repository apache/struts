/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;


/**
 * Utility class for text parsing.
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 * @author tm_jee
 *
 * @version $Date$ $Id$
 */
public class TextParseUtil {

    private static final int MAX_RECURSION = 1;

    /**
     * Converts all instances of ${...}, and %{...} in <code>expression</code> to the value returned
     * by a call to {@link ValueStack#findValue(java.lang.String)}. If an item cannot
     * be found on the stack (null is returned), then the entire variable ${...} is not
     * displayed, just as if the item was on the stack but returned an empty string.
     *
     * @param expression an expression that hasn't yet been translated
     * @return the parsed expression
     */
    public static String translateVariables(String expression, ValueStack stack) {
        return translateVariables(new char[]{'$', '%'}, expression, stack, String.class, null).toString();
    }


    /**
     * Function similarly as {@link #translateVariables(char, String, ValueStack)}
     * except for the introduction of an additional <code>evaluator</code> that allows
     * the parsed value to be evaluated by the <code>evaluator</code>. The <code>evaluator</code>
     * could be null, if it is it will just be skipped as if it is just calling
     * {@link #translateVariables(char, String, ValueStack)}.
     *
     * <p/>
     *
     * A typical use-case would be when we need to URL Encode the parsed value. To do so
     * we could just supply a URLEncodingEvaluator for example.
     *
     * @param expression
     * @param stack
     * @param evaluator The parsed Value evaluator (could be null).
     * @return the parsed (and possibly evaluated) variable String.
     */
    public static String translateVariables(String expression, ValueStack stack, ParsedValueEvaluator evaluator) {
    	return translateVariables(new char[]{'$', '%'}, expression, stack, String.class, evaluator).toString();
    }

    /**
     * Converts all instances of ${...} in <code>expression</code> to the value returned
     * by a call to {@link ValueStack#findValue(java.lang.String)}. If an item cannot
     * be found on the stack (null is returned), then the entire variable ${...} is not
     * displayed, just as if the item was on the stack but returned an empty string.
     *
     * @param open
     * @param expression
     * @param stack
     * @return Translated variable String
     */
    public static String translateVariables(char open, String expression, ValueStack stack) {
        return translateVariables(open, expression, stack, String.class, null).toString();
    }

    /**
     * Converted object from variable translation.
     *
     * @param open
     * @param expression
     * @param stack
     * @param asType
     * @return Converted object from variable translation.
     */
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType) {
    	return translateVariables(open, expression, stack, asType, null);
    }

    /**
     * Converted object from variable translation.
     *
     * @param open
     * @param expression
     * @param stack
     * @param asType
     * @param evaluator
     * @return Converted object from variable translation.
     */
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator) {
        return translateVariables(new char[]{open} , expression, stack, asType, evaluator, MAX_RECURSION);
    }

    /**
     * Converted object from variable translation.
     *
     * @param open
     * @param expression
     * @param stack
     * @param asType
     * @param evaluator
     * @return Converted object from variable translation.
     */
    public static Object translateVariables(char[] openChars, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator) {
        return translateVariables(openChars, expression, stack, asType, evaluator, MAX_RECURSION);
    }

    /**
     * Converted object from variable translation.
     *
     * @param open
     * @param expression
     * @param stack
     * @param asType
     * @param evaluator
     * @return Converted object from variable translation.
     */
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator, int maxLoopCount) {
        return translateVariables(new char[]{open}, expression, stack, asType, evaluator, maxLoopCount);
    }

    /**
     * Converted object from variable translation.
     *
     * @param open
     * @param expression
     * @param stack
     * @param asType
     * @param evaluator
     * @return Converted object from variable translation.
     */
    public static Object translateVariables(char[] openChars, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator, int maxLoopCount) {
        // deal with the "pure" expressions first!
        //expression = expression.trim();
        Object result = expression;
        for (char open : openChars) {
            int loopCount = 1;
            int pos = 0;

            //this creates an implicit StringBuffer and shouldn't be used in the inner loop
            final String lookupChars = open + "{";

            while (true) {
                int start = expression.indexOf(lookupChars, pos);
                if (start == -1) {
                    pos = 0;
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

                    Object o = stack.findValue(var, asType);
                    if (evaluator != null) {
                    	o = evaluator.evaluate(o);
                    }


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

        XWorkConverter conv = ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(XWorkConverter.class);
        return conv.convertValue(stack.getContext(), result, asType);
    }

    /**
     * Returns a set from comma delimted Strings.
     * @param s The String to parse.
     * @return A set from comma delimted Strings.
     */
    public static Set<String> commaDelimitedStringToSet(String s) {
        Set<String> set = new HashSet<String>();
        String[] split = s.split(",");
        for (String aSplit : split) {
            String trimmed = aSplit.trim();
            if (trimmed.length() > 0)
                set.add(trimmed);
        }
        return set;
    }


    /**
     * A parsed value evaluator for {@link TextParseUtil}. It could be supplied by
     * calling {@link TextParseUtil#translateVariables(char, String, ValueStack, Class, ParsedValueEvaluator)}.
     *
     * <p/>
     *
     * By supplying this <code>ParsedValueEvaluator</code>, the parsed value
     * (parsed against the value stack) value will be
     * given to <code>ParsedValueEvaluator</code> to be evaluated before the
     * translateVariable process goes on.
     *
     * <p/>
     *
     * A typical use-case would be to have a custom <code>ParseValueEvaluator</code>
     * to URL Encode the parsed value.
     *
     * @author tm_jee
     *
     * @version $Date$ $Id$
     */
    public static interface ParsedValueEvaluator {

    	/**
    	 * Evaluated the value parsed by Ognl value stack.
    	 *
    	 * @param parsedValue - value parsed by ognl value stack
    	 * @return return the evaluted value.
    	 */
    	Object evaluate(Object parsedValue);
    }
}
