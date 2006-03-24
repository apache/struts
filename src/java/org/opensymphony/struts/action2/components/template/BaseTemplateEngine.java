/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.components.template;

import com.opensymphony.util.ClassLoaderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Base class for template engines.
 *
 * @author jcarreira
 */
public abstract class BaseTemplateEngine implements TemplateEngine {
    private static final Log LOG = LogFactory.getLog(BaseTemplateEngine.class);

    final Map themeProps = new HashMap();

    public Map getThemeProps(Template template) {
        synchronized (themeProps) {
            Properties props = (Properties) themeProps.get(template.getTheme());
            if (props == null) {
                String propName = template.getDir() + "/" + template.getTheme() + "/theme.properties";
                InputStream is = ClassLoaderUtil.getResourceAsStream(propName, getClass());
                props = new Properties();

                if (is != null) {
                    try {
                        props.load(is);
                    } catch (IOException e) {
                        LOG.error("Could not load " + propName, e);
                    }
                }

                themeProps.put(template.getTheme(), props);
            }

            return props;
        }
    }

    protected String getFinalTemplateName(Template template) {
        String t = template.toString();
        if (t.indexOf(".") <= 0) {
            return t + "." + getSuffix();
        }

        return t;
    }

    protected abstract String getSuffix();
}
