package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Locale;

/**
 * Default implementation of {@link LocaleProvider}
 */
public class DefaultLocaleProvider implements LocaleProvider {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultLocaleProvider.class);

    public Locale getLocale() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.getLocale();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action context not initialized");
            }
            return null;
        }
    }

}
