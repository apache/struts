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
package org.apache.struts2.text;

import org.apache.struts2.ActionContext;
import org.apache.struts2.Unchainable;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ValueStack;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNullElse;

/**
 * DefaultTextProvider gets texts from only the default resource bundles associated with the default bundles.
 */
public class DefaultTextProvider implements TextProvider, Serializable, Unchainable {

    private static final Object[] EMPTY_ARGS = new Object[0];

    protected LocalizedTextProvider localizedTextProvider;

    public DefaultTextProvider() {
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    @Override
    public boolean hasKey(String key) {
        return getText(key) != null;
    }

    @Override
    public String getText(String key) {
        return localizedTextProvider.findDefaultText(key, ActionContext.getContext().getLocale());
    }

    @Override
    public String getText(String key, String defaultValue) {
        String text = getText(key);
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    @Override
    public String getText(String key, List<?> args) {
        Object[] params;
        if (args != null) {
            params = args.toArray();
        } else {
            params = EMPTY_ARGS;
        }

        return localizedTextProvider.findDefaultText(key, ActionContext.getContext().getLocale(), params);
    }

    @Override
    public String getText(String key, String[] args) {
        Object[] params;
        params = requireNonNullElse(args, EMPTY_ARGS);

        return localizedTextProvider.findDefaultText(key, ActionContext.getContext().getLocale(), params);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args) {
        String text = getText(key, args);
        if(text == null && defaultValue == null) {
            defaultValue = key;
        }
        if (text == null && defaultValue != null) {

            MessageFormat format = new MessageFormat(defaultValue);
            format.setLocale(ActionContext.getContext().getLocale());
            format.applyPattern(defaultValue);

            Object[] params;
            if (args != null) {
                params = args.toArray();
            } else {
                params = EMPTY_ARGS;
            }

            return format.format(params);
        }
        return text;
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        String text = getText(key, args);
        if (text == null) {
            MessageFormat format = new MessageFormat(defaultValue);
            format.setLocale(ActionContext.getContext().getLocale());
            format.applyPattern(defaultValue);

            return format.format(requireNonNullElse(args, EMPTY_ARGS));

        }
        return text;
    }

    @Override
    public String getText(String key, String defaultValue, String obj) {
        List<Object> args = new ArrayList<>(1);
        args.add(obj);
        return getText(key, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        //we're not using the value stack here
        return getText(key, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        //we're not using the value stack here
        List<Object> values = new ArrayList<>(Arrays.asList(args));
        return getText(key, defaultValue, values);
    }

    @Override
    public ResourceBundle getTexts(String bundleName) {
        return localizedTextProvider.findResourceBundle(bundleName, ActionContext.getContext().getLocale());
    }

    @Override
    public ResourceBundle getTexts() {
        return null;
    }

}
