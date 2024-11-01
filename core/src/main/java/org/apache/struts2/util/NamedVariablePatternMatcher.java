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
package org.apache.struts2.util;

import org.apache.commons.lang3.StringUtils;

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
 * <p>For example, the following patterns will be processed as so:</p>
 *
 * <table summary="">
 * <tr>
 *  <th>Pattern</th>
 *  <th>Example</th>
 *  <th>Variable Map Contents</th>
 * </tr>
 * <tr>
 *  <td><code>/animals/{animal}</code></td>
 *  <td><code>/animals/dog</code></td>
 *  <td>{animal -&gt; dog}</td>
 * </tr>
 * <tr>
 *  <td><code>/animals/{animal}/tag/No{id}</code></td>
 *  <td><code>/animals/dog/tag/No23</code></td>
 *  <td>{animal -&gt; dog, id -&gt; 23}</td>
 * </tr>
 * <tr>
 *  <td><code>/{language}</code></td>
 *  <td><code>/en</code></td>
 *  <td>{language -&gt; en}</td>
 * </tr>
 * </table>
 *
 * <p>
 * Escaping hasn't been implemented since the intended use of these patterns will be in matching URLs.
 * </p>
 *
 * @since 2.1
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
        if (StringUtils.isEmpty(data)) {
            return null;
        }

        int len = data.length();
        StringBuilder regex = new StringBuilder();
        List<String> varNames = new ArrayList<>();
        int s = 0;
        while (s < len) {
            int e = data.indexOf('{', s);
            if (e < 0 && data.indexOf('}', s) > -1) {
                throw new IllegalArgumentException("Missing opening '{' in [" + data + "]!");
            }
            if (e < 0) {
                regex.append(Pattern.quote(data.substring(s)));
                break;
            }
            if (e > s) {
                regex.append(Pattern.quote(data.substring(s, e)));
            }
            s = e + 1;
            e = data.indexOf('}', s);
            if (e < 0) {
                return null;
            }
            String varName = data.substring(s, e);
            if (StringUtils.isEmpty(varName)) {
                throw new IllegalArgumentException("Missing variable name in [" + data + "]!");
            }
            varNames.add(varName);
            regex.append("([^/]+)");
            s = e + 1;
        }
        return new CompiledPattern(Pattern.compile(regex.toString()), varNames);
    }

    /**
     * Tries to process the data against the compiled expression.  If successful, the map will contain
     * the matched data, using the specified variable names in the original pattern.
     *
     * @param map  The map of variables
     * @param data The data to match
     * @param expr The compiled pattern
     * @return True if matched, false if not matched, the data was null, or the data was an empty string
     */
    public boolean match(Map<String, String> map, String data, CompiledPattern expr) {

        if (data != null && !data.isEmpty()) {
            Matcher matcher = expr.getPattern().matcher(data);
            if (matcher.matches()) {
                for (int x = 0; x < expr.getVariableNames().size(); x++) {
                    map.put(expr.getVariableNames().get(x), matcher.group(x + 1));
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
        private final Pattern pattern;
        private final List<String> variableNames;


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
