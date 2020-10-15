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
package org.apache.struts2.views;

public class TagAttribute {

    public static final TagAttribute NULL = new TagAttribute(null, true);
    public static final TagAttribute EMPTY = new TagAttribute("", true);

    private final String value;
    private final boolean evaluated;

    private TagAttribute(String value, boolean evaluated) {
        this.value = value;
        this.evaluated = evaluated;
    }

    public static TagAttribute raw(String value) {
        return new TagAttribute(value, false);
    }

    public static TagAttribute evaluated(String evaluatedValue) {
        return new TagAttribute(evaluatedValue, true);
    }

    public boolean isExpression() {
        return value != null && value.contains("%{") && value.contains("}");
    }

    public String stripedExpression() {
        if (isExpression()) {
            return value.substring(2, value.length() - 1);
        } else {
            return value;
        }
    }

    public TagAttribute escaped(){
        // escape any possible values that can make the ID painful to work with in JavaScript
        if (value != null) {
            return TagAttribute.evaluated(value.replaceAll("[\\/\\.\\[\\]\'\"]", "_"));
        } else {
            return null;
        }
    }

    public String getValue() {
        return value;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public boolean isNull() {
        return value == null;
    }

    public TagAttribute append(String appendString) {
        return TagAttribute.evaluated(value + appendString);
    }

}
