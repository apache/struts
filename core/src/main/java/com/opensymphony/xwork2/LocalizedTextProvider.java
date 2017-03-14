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

    @Deprecated
    void reset();
}
