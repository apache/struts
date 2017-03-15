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
    private LocaleProviderFactory localeProviderFactory;
    private LocalizedTextProvider localizedTextProvider;

    @Inject
    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    public TextProvider createInstance(Class clazz) {
        TextProvider instance = getTextProvider(clazz);
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider) instance).setClazz(clazz);
            ((ResourceBundleTextProvider) instance).setLocaleProvider(localeProviderFactory.createLocaleProvider());
        }
        return instance;
    }

    public TextProvider createInstance(ResourceBundle bundle) {
        TextProvider instance = getTextProvider(bundle);
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider) instance).setBundle(bundle);
            ((ResourceBundleTextProvider) instance).setLocaleProvider(localeProviderFactory.createLocaleProvider());
        }
        return instance;
    }

    protected TextProvider getTextProvider(Class clazz) {
        if (this.textProvider == null) {
            return new TextProviderSupport(clazz, localeProviderFactory.createLocaleProvider(), localizedTextProvider);
        } else {
            return textProvider;
        }
    }

    private TextProvider getTextProvider(ResourceBundle bundle) {
        if (this.textProvider == null) {
            return new TextProviderSupport(bundle, localeProviderFactory.createLocaleProvider(), localizedTextProvider);
        }
        return textProvider;
    }

}
