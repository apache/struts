package com.opensymphony.xwork2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

}
