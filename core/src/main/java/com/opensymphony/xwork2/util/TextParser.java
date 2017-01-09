package com.opensymphony.xwork2.util;

/**
 * Used to parse expressions like ${foo.bar} or %{bar.foo} but it is up tp the TextParser's
 * implementation what kind of opening char to use (#, $, %, etc)
 */
public interface TextParser {

    int DEFAULT_LOOP_COUNT = 1;

    Object evaluate(char[] openChars, String expression, TextParseUtil.ParsedValueEvaluator evaluator, int maxLoopCount);

}
