/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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

import com.opensymphony.xwork2.inject.Inject;

import java.util.ResourceBundle;

/**
 * This factory enables users to provide and correctly initialize a custom TextProvider.
 *
 * @author Oleg Gorobets
 * @author Rene Gielen
 */
public class TextProviderFactory {

    private TextProvider textProvider;

    @Inject
    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    public TextProvider createInstance(Class clazz, LocaleProvider provider) {
        TextProvider instance = getTextProvider(clazz, provider);
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider) instance).setClazz(clazz);
            ((ResourceBundleTextProvider) instance).setLocaleProvider(provider);
        }
        return instance;
    }

    public TextProvider createInstance(ResourceBundle bundle, LocaleProvider provider) {
        TextProvider instance = getTextProvider(bundle, provider);
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider) instance).setBundle(bundle);
            ((ResourceBundleTextProvider) instance).setLocaleProvider(provider);
        }
        return instance;
    }

    protected TextProvider getTextProvider(Class clazz, LocaleProvider provider) {
        if (this.textProvider == null) {
            return new TextProviderSupport(clazz, provider);
        } else {
            return textProvider;
        }
    }

    private TextProvider getTextProvider(ResourceBundle bundle, LocaleProvider provider) {
        if (this.textProvider == null) {
            return new TextProviderSupport(bundle, provider);
        } else {
            return textProvider;
        }
    }

}
