/*
 * $Id: ServletContextAware.java 651946 2008-04-27 13:41:38Z apetrelli $
 *
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
package org.apache.struts2.util;

import com.opensymphony.xwork2.util.PatternMatcher;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows regular expressions to be used in action names. The regular expressions
 * can be in the form {FIELD_NAME} or {FIELD_NAME:REGULAR_EXPRESSION}. For example:
 * <br/>
 * <pre>
 *  &lt;action name="/{bio:.+}/test/{name}" class="org.apache.struts2.showcase.UITagExample"&gt;
 *       &lt;result>/tags/ui/example.jsp&lt;/result&gt;
 *  &lt;/action&gt;
 * </pre>
 *
 * For this to work it is important to set the following:
 * <pre>
 * &lt;constant name="struts.enable.SlashesInActionNames" value="true"/&gt;
 * &lt;constant name="struts.mapper.alwaysSelectFullNamespace" value="false"/&gt;
 * &lt;constant name="struts.patternMatcher" value="regex" /&gt;
 * </pre>
 */
public class RegexPatternMatcher implements PatternMatcher<RegexPatternMatcherExpression> {
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    public RegexPatternMatcherExpression compilePattern(String data) {
        Map<Integer, String> params = new HashMap<Integer, String>();

        Matcher matcher = PATTERN.matcher(data);
        int count = 0;
        while (matcher.find()) {
            String expression = matcher.group(1);
            //check if it is a regex
            int index = expression.indexOf(':');
            if (index > 0) {
                String paramName = expression.substring(0, index);
                String regex = StringUtils.substring(expression, index + 1);
                if (StringUtils.isBlank(regex)) {
                    throw new IllegalArgumentException("invalid expression [" + expression + "], named parameter regular exression "
                            + "must be in the format {PARAM_NAME:REGEX}");
                }

                params.put(++count, paramName);

            } else {
                params.put(++count, expression);
            }
        }

        //generate a new pattern used to match URIs
        //replace {X:B} by (B)
        String newPattern = data.replaceAll("(\\{[^\\}]*?:(.*?)\\})", "($2)");

        //replace {X} by (.*?)
        newPattern = newPattern.replaceAll("(\\{.*?\\})", "(.*?)");
        return new RegexPatternMatcherExpression(Pattern.compile(newPattern), params);
    }

    public boolean isLiteral(String pattern) {
        return (pattern == null || pattern.indexOf('{') == -1);
    }

    public boolean match(Map<String, String> map, String data, RegexPatternMatcherExpression expr) {
        Matcher matcher = expr.getPattern().matcher(data);
        Map<Integer, String> params = expr.getParams();

        if (matcher.matches()) {
            map.put("0", data);
            
            //extract values and get param names from the expression
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String paramName = params.get(i);
                String value = matcher.group(i);
                
                //by name
                map.put(paramName, value);
                //by index so the old {1} still works
                map.put(String.valueOf(i), value);
            }

            return true;
        } else {
            return false;
        }
    }

}
