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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of a pattern matcher that uses simple named wildcards.  The named wildcards are defined using the
 * <code>{VARIABLE_NAME}</code> syntax and will match any characters that aren't '/'.  Internally, the pattern is
 * converted into a regular expression where the named wildcard will be translated into <code>([^/]+)</code> so that
 * at least one character must match in order for the wildcard to be matched successfully.  Matched values will be
 * available in the variable map, indexed by the name they were given in the pattern.
 *
 * <p>For example, the following patterns will be processed as so:
 * </p>
 * <table>
 * <tr>
 *  <th>Pattern</th>
 *  <th>Example</th>
 *  <th>Variable Map Contents</th>
 * </tr>
 * <tr>
 *  <td><code>/animals/{animal}</code</td>
 *  <td><code>/animals/dog</code></td>
 *  <td>{animal -> dog}</td>
 * </tr>
 * <tr>
 *  <td><code>/animals/{animal}/tag/No{id}</code</td>
 *  <td><code>/animals/dog/tag/No23</code></td>
 *  <td>{animal -> dog, id -> 23}</td>
 * </tr>
 * <tr>
 *  <td><code>/{language}</code</td>
 *  <td><code>/en</code></td>
 *  <td>{language -> en}</td>
 * </tr>
 * </table>
 *
 * <p>
 * Excaping hasn't been implemented since the intended use of these patterns will be in matching URLs.
 * </p>
 *
 * @Since 2.1
 */
public class NamedVariablePatternMatcher implements PatternMatcher<NamedVariablePatternMatcher.CompiledPattern> {

    public boolean isLiteral(String pattern) {
        return (pattern == null || pattern.indexOf('{') == -1);
    }

    /**
     * Compiles the pattern.
     *
     * @param data The pattern, must not be null or empty
     * @return The compiled pattern, null if the pattern was null or empty
     */
    public CompiledPattern compilePattern(String data) {
        StringBuilder regex = new StringBuilder();
        if (data != null && data.length() > 0) {
            List<String> varNames = new ArrayList<String>();
            StringBuilder varName = null;
            for (int x=0; x<data.length(); x++) {
                char c = data.charAt(x);
                switch (c) {
                    case '{' :  varName = new StringBuilder(); break;
                    case '}' :  if (varName == null) {
                                    throw new IllegalArgumentException("Mismatched braces in pattern");
                                }
                                varNames.add(varName.toString());
                                regex.append("([^/]+)");
                                varName = null;
                                break;
                    default  :  if (varName == null) {
                                    regex.append(c);
                                } else {
                                    varName.append(c);
                                }
                }
            }
            return new CompiledPattern(Pattern.compile(regex.toString()), varNames);
        }
        return null;
    }

    /**
     * Tries to process the data against the compiled expression.  If successful, the map will contain
     * the matched data, using the specified variable names in the original pattern.
     *
     * @param map The map of variables
     * @param data The data to match
     * @param expr The compiled pattern
     * @return True if matched, false if not matched, the data was null, or the data was an empty string
     */
    public boolean match(Map<String, String> map, String data, CompiledPattern expr) {

        if (data != null && data.length() > 0) {
            Matcher matcher = expr.getPattern().matcher(data);
            if (matcher.matches()) {
                for (int x=0; x<expr.getVariableNames().size(); x++)  {
                    map.put(expr.getVariableNames().get(x), matcher.group(x+1));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Stores the compiled pattern and the variable names matches will correspond to.
     */
    public static class CompiledPattern {
        private Pattern pattern;
        private List<String> variableNames;


        public CompiledPattern(Pattern pattern, List<String> variableNames) {
            this.pattern = pattern;
            this.variableNames = variableNames;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public List<String> getVariableNames() {
            return variableNames;
        }
    }
}
