/*
 * $Id$
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
package org.apache.struts2.json;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.WildcardUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <p>Isolate the process of cleaning JSON data from the Interceptor class
 * itself.</p>
 * 
 * <p>The allowed and blocked wildcard patterns, combined with
 * defaultBlock, let you filter out values that should not be injected, in
 * the same way that ParameterFilterInterceptor does.  Note that you can
 * only remove values from a Map.  Removing values from a List is dangerous
 * because it could change the meaning of the data!</p>
 */
public abstract class JSONCleaner {

    private static final Logger LOG = LoggerFactory.getLogger(JSONCleaner.class);

    public static class Filter
    {
        public Pattern pattern;
        public boolean allow;

        public Filter(String pattern, boolean allow)
        {
            this.pattern = WildcardUtil.compileWildcardPattern(pattern);
            this.allow = allow;
        }
    }

    private boolean defaultBlock = false;
    private Collection<String> allowed;
    private Collection<String> blocked;
    private Map<String, Filter> includesExcludesMap;

    public Object clean(String ognlPrefix, Object data) throws JSONException {
        if (data == null)
            return null;
        else if (data instanceof List)
            return cleanList(ognlPrefix, data);
        else if (data instanceof Map)
            return cleanMap(ognlPrefix, data);
        else
            return cleanValue(ognlPrefix, data);
    }

    protected Object cleanList(String ognlPrefix, Object data) throws JSONException {
        List list = (List) data;
        int count = list.size();
        for (int i = 0; i < count; i++) {
            list.set(i, clean(ognlPrefix + "[" + i + "]", list.get(i)));
        }
        return list;
    }

    protected Object cleanMap(String ognlPrefix, Object data) throws JSONException {
        Map map = (Map) data;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry e = (Map.Entry) iter.next();
            String key = (ognlPrefix.length() > 0 ? ognlPrefix + "." : "") + e.getKey();
            if (allow(key)) {
                e.setValue(clean(key, e.getValue()));
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("blocked: " + key);
                }
                iter.remove();
            }
        }
        return map;
    }

    protected abstract Object cleanValue(String ognlName, Object data) throws JSONException;

    private boolean allow(String ognl) {
        Map<String, Filter> includesExcludesMap = getIncludesExcludesMap();

        boolean allow = !isDefaultBlock();

        if (includesExcludesMap != null) {
            for (String currRule : includesExcludesMap.keySet()) {
                Filter f = includesExcludesMap.get(currRule);
                if (f.pattern.matcher(ognl).matches()) {
                    allow = f.allow;
                }
            }
        }

        return allow;
    }

    /**
     * @return the compiled list of includes and excludes
     */
    public Map<String, Filter> getIncludesExcludesMap() {
        if (allowed == null && blocked == null) {
            return includesExcludesMap;
        }

        if (includesExcludesMap == null) {
            includesExcludesMap = new TreeMap<String, Filter>();

            Map<String, Boolean> existingExpr = new HashMap<String, Boolean>();

            Map<String, Map<String, String>> includePatternData = JSONUtil.getIncludePatternData();
            String splitPattern = includePatternData.get(JSONUtil.SPLIT_PATTERN).get(JSONUtil.WILDCARD_PATTERN);
            String joinString = includePatternData.get(JSONUtil.JOIN_STRING).get(JSONUtil.WILDCARD_PATTERN);
            String arrayBegin = includePatternData.get(JSONUtil.ARRAY_BEGIN_STRING).get(JSONUtil.WILDCARD_PATTERN);
            String arrayEnd = includePatternData.get(JSONUtil.ARRAY_END_STRING).get(JSONUtil.WILDCARD_PATTERN);

            if (allowed != null) {
                for (String a : allowed) {
                    // Compile a pattern for each level of the object hierarchy
                    // so cleanMap() won't short-circuit too early.

                    String expr = "";
                    for (String piece : a.split(splitPattern)) {
                        if (expr.length() > 0) {
                            expr += joinString;
                        }
                        expr += piece;

                        if (!existingExpr.containsKey(expr)) {
                            existingExpr.put(expr, Boolean.TRUE);

                            String s = expr;
                            if (piece.endsWith(arrayEnd)) {
                                s = expr.substring(0, expr.lastIndexOf(arrayBegin));
                            }

                            if (s.length() > 0) {
                                includesExcludesMap.put(s, new Filter(s, true));

                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Adding include wildcard expression: " + s);
                                }
                            }
                        }
                    }
                }
            }
            if (blocked != null) {
                for (String b : blocked) {
                    includesExcludesMap.put(b, new Filter(b, false));
                }
            }
        }

        return includesExcludesMap;
    }

    /**
     * Allow external caching of the compiled result.
     *
     * @param map the compiled list of includes and excludes
     */
    public void setIncludesExcludesMap(Map<String, Filter> map) {
        includesExcludesMap = map;
    }

    /**
     * @return value of defaultBlock
     */
    public boolean isDefaultBlock() {
        return defaultBlock;
    }

    /**
     * @param defaultExclude The defaultExclude to set.
     */
    public void setDefaultBlock(boolean defaultExclude) {
        this.defaultBlock = defaultExclude;
    }

    /**
     * @return list of blocked wildcard patterns
     */
    public Collection<String> getBlockedCollection() {
        return blocked;
    }

    /**
     * @param blocked The blocked to set.
     */
    public void setBlockedCollection(Collection<String> blocked) {
        this.blocked = blocked;
    }

    /**
     * @param blocked The blocked paramters as comma separated String.
     */
    public void setBlocked(String blocked) {
        setBlockedCollection(asCollection(blocked));
    }

    /**
     * @return list of allowed wildcard patterns
     */
    public Collection<String> getAllowedCollection() {
        return allowed;
    }

    /**
     * @param allowed The allowed to set.
     */
    public void setAllowedCollection(Collection<String> allowed) {
        this.allowed = allowed;
    }

    /**
     * @param allowed The allowed paramters as comma separated String.
     */
    public void setAllowed(String allowed) {
        setAllowedCollection(asCollection(allowed));
    }

    /**
     * Return a collection from the comma delimited String.
     *
     * @param commaDelim the comma delimited String.
     * @return A collection from the comma delimited String. Returns <tt>null</tt> if the string is empty.
     */
    private Collection<String> asCollection(String commaDelim) {
        if (commaDelim == null || commaDelim.trim().length() == 0) {
            return null;
        }
        return TextParseUtil.commaDelimitedStringToSet(commaDelim);
    }

}
