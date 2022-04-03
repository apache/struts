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
 package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.List;
import java.util.ResourceBundle;

public class DummyTextProvider implements TextProvider {
    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public String getText(String key) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, String obj) {
        return null;
    }

    @Override
    public String getText(String key, List<?> args) {
        return null;
    }

    @Override
    public String getText(String key, String[] args) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return null;
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return null;
    }

    @Override
    public ResourceBundle getTexts(String bundleName) {
        return null;
    }

    @Override
    public ResourceBundle getTexts() {
        return null;
    }
}
