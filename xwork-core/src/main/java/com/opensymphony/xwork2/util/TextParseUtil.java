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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
    public static Object translateVariables(char[] openChars, String expression, final ValueStack stack, final Class asType, final ParsedValueEvaluator evaluator, int maxLoopCount) {

        ParsedValueEvaluator ognlEval = new ParsedValueEvaluator() {
            public Object evaluate(String parsedValue) {
                Object o = stack.findValue(parsedValue, asType);
                if (evaluator != null && o != null) {
                    o = evaluator.evaluate(o.toString());
                }
                return o;
            }
        };

        TextParser parser = ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(TextParser.class);

        return parser.evaluate(openChars, expression, ognlEval, maxLoopCount);
    }

    /**
     * @see #translateVariablesCollection(char[], String, ValueStack, boolean, ParsedValueEvaluator, int)
     *
     * @param expression
     * @param stack
     * @param excludeEmptyElements
     * @param evaluator
     * @return
     */
    public static Collection<String>  translateVariablesCollection(String expression, ValueStack stack, boolean excludeEmptyElements, ParsedValueEvaluator evaluator) {
        return translateVariablesCollection(new char[]{'$', '%'}, expression, stack, excludeEmptyElements, evaluator, MAX_RECURSION);
    }

    /**
     * Resolves given expression on given ValueStack. If found element is a
     * collection each element will be converted to String. If just a single
     * object is found it is converted to String and wrapped in a collection.
     * 
     * @param openChars
     * @param expression
     * @param stack
     * @param excludeEmptyElements
     * @param evaluator
     * @param maxLoopCount
     * @return
     */
    public static Collection<String> translateVariablesCollection(
            char[] openChars, String expression, final ValueStack stack, boolean excludeEmptyElements,
            final ParsedValueEvaluator evaluator, int maxLoopCount) {

        ParsedValueEvaluator ognlEval = new ParsedValueEvaluator() {
            public Object evaluate(String parsedValue) {
                return stack.findValue(parsedValue); // no asType !!!
            }
        };

        Map<String, Object> context = stack.getContext();
        TextParser parser = ((Container)context.get(ActionContext.CONTAINER)).getInstance(TextParser.class);

        Object result = parser.evaluate(openChars, expression, ognlEval, maxLoopCount);

        Collection<String> resultCol;
        if (result instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<Object> casted = (Collection<Object>)result;
            resultCol = new ArrayList<String>();

            XWorkConverter conv = ((Container)context.get(ActionContext.CONTAINER)).getInstance(XWorkConverter.class);

            for (Object element : casted) {
                String stringElement = (String)conv.convertValue(context, element, String.class);
                if (shallBeIncluded(stringElement, excludeEmptyElements)) {
                    if (evaluator != null) {
                        stringElement = evaluator.evaluate(stringElement).toString();
                    }
                    resultCol.add(stringElement);
                }
            }
        } else {
            resultCol = new ArrayList<String>();
            String resultStr = translateVariables(expression, stack, evaluator);
            if (shallBeIncluded(resultStr, excludeEmptyElements)) {
                resultCol.add(resultStr);
            }
        }

        return resultCol;
    }

    /**
     * Tests if given string is not null and not empty when excluding of empty
     * elements is requested.
     * 
     * @param str String to check.
     * @param excludeEmptyElements Whether empty elements shall be excluded.
     * @return True if given string can be included in collection.
     */
    private static boolean shallBeIncluded(String str, boolean excludeEmptyElements) {
        return !excludeEmptyElements || ((str != null) && (str.length() > 0));
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
    	Object evaluate(String parsedValue);
    }
}
