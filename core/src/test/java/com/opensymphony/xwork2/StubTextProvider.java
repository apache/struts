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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StubTextProvider implements TextProvider {

    private final Map<String, String> map;

    public StubTextProvider(final Map<String, String> map) {
        this.map = map;
    }

    @Override
    public boolean hasKey(final String key) {
        return map.containsKey(key);
    }

    @Override
    public String getText(final String key) {
        return map.get(key);
    }

    @Override
    public String getText(final String key, final String defaultValue) {
        final String text = this.getText(key);
        return text == null? defaultValue : text;
    }

    @Override
    public String getText(final String key, final String defaultValue, final String obj) {
        return this.getText(key, defaultValue);
    }

    @Override
    public String getText(final String key, final List<?> args) {
        return this.getText(key);
    }

    @Override
    public String getText(final String key, final String[] args) {
        return this.getText(key);
    }

    @Override
    public String getText(final String key, final String defaultValue, final List<?> args) {
        return this.getText(key);
    }

    @Override
    public String getText(final String key, final String defaultValue, final String[] args) {
        return this.getText(key);
    }

    @Override
    public String getText(final String key, final String defaultValue, final List<?> args, final ValueStack stack) {
        return this.getText(key, defaultValue);
    }

    @Override
    public String getText(final String key, final String defaultValue, final String[] args, final ValueStack stack) {
        return this.getText(key, defaultValue);
    }

    @Override
    public ResourceBundle getTexts(final String bundleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceBundle getTexts() {
        throw new UnsupportedOperationException();
    }

}
