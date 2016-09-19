package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Locale;

/**
 * Default implementation of {@link LocaleProvider}
 */
public class DefaultLocaleProvider implements LocaleProvider {

    private final static Logger LOG = LogManager.getLogger(DefaultLocaleProvider.class);

    public Locale getLocale() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.getLocale();
        } else {
            LOG.debug("Action context not initialized");
            return null;
        }
    }

    @Override
    public boolean isValidLocaleString(String localeStr) {
        return isValidLocale(LocalizedTextUtil.localeFromString(localeStr, getLocale()));
    }

    @Override
    public boolean isValidLocale(Locale locale) {
        return locale != null && Arrays.asList(Locale.getAvailableLocales()).contains(locale);
    }
}
