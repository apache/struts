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
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ModelDriven;
import org.apache.struts2.conversion.impl.XWorkConverter;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.reflection.ReflectionProvider;

import java.beans.PropertyDescriptor;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides support for localization in the framework, it can be used to read only default bundles,
 * or it can search the class hierarchy to find proper bundles.
 */
public class StrutsLocalizedTextProvider extends AbstractLocalizedTextProvider {

    private static final Logger LOG = LogManager.getLogger(StrutsLocalizedTextProvider.class);
    private transient ReflectionProvider reflectionProvider;

    public StrutsLocalizedTextProvider() {
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
    public String findText(Class<?> startClazz, String textKey, Locale locale, String defaultMessage, Object[] args,
                           ValueStack valueStack) {
        if (textKey == null) {
            LOG.debug("Key is null, short-circuit to default message");
            return defaultMessage;
        }
        String indexedTextName = extractIndexedName(textKey);

        // Allow for and track an early lookup for the message in the default resource bundles first, before searching the class hierarchy.
        // The early lookup is only performed when the text provider has been configured to do so, otherwise follow the standard processing order.
        boolean performedInitialDefaultBundlesMessageLookup = false;
        GetDefaultMessageReturnArg result = null;

        // If search default bundles first is set true, call alternative logic first.
        if (searchDefaultBundlesFirst) {
            result = getDefaultMessageWithAlternateKey(textKey, indexedTextName, locale, valueStack, args, defaultMessage);
            performedInitialDefaultBundlesMessageLookup = true;
            if (!unableToFindTextForKey(result)) {
                return result.message;  // Found a message in the default resource bundles for textKey or indexedTextName.
            }
        }

        // search up class hierarchy
        String msg = findMessage(startClazz, textKey, indexedTextName, locale, args, null, valueStack);

        if (msg != null) {
            return msg;
        }

        if (ModelDriven.class.isAssignableFrom(startClazz)) {
            ActionContext context = ActionContext.getContext();
            // search up model's class hierarchy
            ActionInvocation actionInvocation = context.getActionInvocation();

            // ActionInvocation may be null if we're being run from a Sitemesh filter, so we won't get model texts if this is null
            if (actionInvocation != null) {
                Object action = actionInvocation.getAction();
                if (action instanceof ModelDriven) {
                    Object model = ((ModelDriven<?>) action).getModel();
                    if (model != null) {
                        msg = findMessage(model.getClass(), textKey, indexedTextName, locale, args, null, valueStack);
                        if (msg != null) {
                            return msg;
                        }
                    }
                }
            }
        }

        // nothing still? alright, search the package hierarchy now
        for (Class<?> clazz = startClazz;
             (clazz != null) && !clazz.equals(Object.class);
             clazz = clazz.getSuperclass()) {

            String basePackageName = clazz.getName();
            while (basePackageName.lastIndexOf('.') != -1) {
                basePackageName = basePackageName.substring(0, basePackageName.lastIndexOf('.'));
                String packageName = basePackageName + ".package";
                msg = getMessage(packageName, locale, textKey, valueStack, args);

                if (msg != null) {
                    return msg;
                }

                if (indexedTextName != null) {
                    msg = getMessage(packageName, locale, indexedTextName, valueStack, args);

                    if (msg != null) {
                        return msg;
                    }
                }
            }
        }

        // see if it's a child property
        int idx = textKey.indexOf('.');

        if (idx != -1) {
            String newKey = null;
            String prop = null;

            if (textKey.startsWith(XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX)) {
                idx = textKey.indexOf('.', XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX.length());

                if (idx != -1) {
                    prop = textKey.substring(XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX.length(), idx);
                    newKey = XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX + textKey.substring(idx + 1);
                }
            } else {
                prop = textKey.substring(0, idx);
                newKey = textKey.substring(idx + 1);
            }

            if (prop != null) {
                Object obj = valueStack.findValue(prop);
                try {
                    Object actionObj = reflectionProvider.getRealTarget(prop, valueStack.getContext(), valueStack.getRoot());
                    if (actionObj != null) {
                        PropertyDescriptor propertyDescriptor = reflectionProvider.getPropertyDescriptor(actionObj.getClass(), prop);

                        if (propertyDescriptor != null) {
                            Class<?> clazz = propertyDescriptor.getPropertyType();

                            if (clazz != null) {
                                if (obj != null) {
                                    valueStack.push(obj);
                                }
                                msg = findText(clazz, newKey, locale, null, args);
                                if (obj != null) {
                                    valueStack.pop();
                                }
                                if (msg != null) {
                                    return msg;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.debug("unable to find property {}", prop, e);
                }
            }
        }

        // get default
        // Note: The default bundles lookup may already have been performed (via alternate early lookup),
        //       so we check first to avoid repeating the same operation twice.
        if (!performedInitialDefaultBundlesMessageLookup) {
            result = getDefaultMessageWithAlternateKey(textKey, indexedTextName, locale, valueStack, args, defaultMessage);
        }

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

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

}
