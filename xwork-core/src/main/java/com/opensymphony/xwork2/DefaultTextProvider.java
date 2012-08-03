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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * DefaultTextProvider gets texts from only the default resource bundles associated with the
 * LocalizedTextUtil.
 *
 * @author Jason Carreira <jcarreira@gmail.com>
 * @author Rainer Hermanns
 * @see LocalizedTextUtil#addDefaultResourceBundle(String)
 */
public class DefaultTextProvider implements TextProvider, Serializable, Unchainable {

    private static final Object[] EMPTY_ARGS = new Object[0];

    public DefaultTextProvider() {
    }

    public boolean hasKey(String key) {
        return getText(key) != null;
    }

    public String getText(String key) {
        return LocalizedTextUtil.findDefaultText(key, ActionContext.getContext().getLocale());
    }

    public String getText(String key, String defaultValue) {
        String text = getText(key);
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    public String getText(String key, List<?> args) {
        Object[] params;
        if (args != null) {
            params = args.toArray();
        } else {
            params = EMPTY_ARGS;
        }

        return LocalizedTextUtil.findDefaultText(key, ActionContext.getContext().getLocale(), params);
    }

    public String getText(String key, String[] args) {
        Object[] params;
        if (args != null) {
            params = args;
        } else {
            params = EMPTY_ARGS;
        }

        return LocalizedTextUtil.findDefaultText(key, ActionContext.getContext().getLocale(), params);
    }

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

    public String getText(String key, String defaultValue, String[] args) {
        String text = getText(key, args);
        if (text == null) {
            MessageFormat format = new MessageFormat(defaultValue);
            format.setLocale(ActionContext.getContext().getLocale());
            format.applyPattern(defaultValue);

            if (args == null) {
                return format.format(EMPTY_ARGS);
            }

            return format.format(args);
        }
        return text;
    }


    public String getText(String key, String defaultValue, String obj) {
        List<Object> args = new ArrayList<Object>(1);
        args.add(obj);
        return getText(key, defaultValue, args);
    }

    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        //we're not using the value stack here
        return getText(key, defaultValue, args);
    }

    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        //we're not using the value stack here
        List<Object> values = new ArrayList<Object>(Arrays.asList(args));
        return getText(key, defaultValue, values);
    }

    public ResourceBundle getTexts(String bundleName) {
        return LocalizedTextUtil.findResourceBundle(bundleName, ActionContext.getContext().getLocale());
    }

    public ResourceBundle getTexts() {
        return null;
    }

}
