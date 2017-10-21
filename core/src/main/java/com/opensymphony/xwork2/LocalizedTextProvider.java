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

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

public interface LocalizedTextProvider extends Serializable {

    String findDefaultText(String aTextName, Locale locale);

    String findDefaultText(String aTextName, Locale locale, Object[] params);

    ResourceBundle findResourceBundle(String aBundleName, Locale locale);

    String findText(Class aClass, String aTextName, Locale locale);

    String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args);

    String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args, ValueStack valueStack);

    String findText(ResourceBundle bundle, String aTextName, Locale locale);

    String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args);

    String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args, ValueStack valueStack);

    void addDefaultResourceBundle(String resourceBundleName);

}
