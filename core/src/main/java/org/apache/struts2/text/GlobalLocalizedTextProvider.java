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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionContext;
import org.apache.struts2.util.ValueStack;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides support for localization in the framework, it can be used to read only default bundles.
 * <p>
 * Note that unlike {@link StrutsLocalizedTextProvider}, this class {@link GlobalLocalizedTextProvider} will
 * <em>only</em> search the default bundles for localized text.
 */
public class GlobalLocalizedTextProvider extends AbstractLocalizedTextProvider {

    private static final Logger LOG = LogManager.getLogger(GlobalLocalizedTextProvider.class);

    public GlobalLocalizedTextProvider() {
        addDefaultResourceBundle(XWORK_MESSAGES_BUNDLE);
        addDefaultResourceBundle(STRUTS_MESSAGES_BUNDLE);
    }

    @Override
    public String findText(Class<?> startClazz, String textKey, Locale locale) {
        return findText(startClazz, textKey, locale, textKey, new Object[0]);
    }

    @Override
    public String findText(Class<?> startClazz, String textKey, Locale locale, String defaultMessage, Object[] args) {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        return findText(startClazz, textKey, locale, defaultMessage, args, valueStack);
    }

    @Override
    public String findText(Class<?> startClazz, String textKey, Locale locale, String defaultMessage, Object[] args, ValueStack valueStack) {
        if (textKey == null) {
            LOG.debug("Key is null, short-circuit to default message");
            return defaultMessage;
        }
        String indexedTextName = extractIndexedName(textKey);

        // get default
        GetDefaultMessageReturnArg result = getDefaultMessageWithAlternateKey(textKey, indexedTextName, locale, valueStack, args, defaultMessage);

        logMissingText(startClazz, textKey, locale, result, indexedTextName);

        return result != null ? result.message : null;
    }

    @Override
    public String findText(ResourceBundle bundle, String textKey, Locale locale) {
        return findText(bundle, textKey, locale, textKey, new Object[0]);
    }

    @Override
    public String findText(ResourceBundle bundle, String textKey, Locale locale, String defaultMessage, Object[] args) {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        return findText(bundle, textKey, locale, defaultMessage, args, valueStack);
    }

}
