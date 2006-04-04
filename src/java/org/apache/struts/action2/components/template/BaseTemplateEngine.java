/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.components.template;

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
