/*
 * $Id$
 *
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

package org.apache.struts2.components.template;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for template engines.
 */
public abstract class BaseTemplateEngine implements TemplateEngine {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTemplateEngine.class);

    /**
     * The default theme properties file name. Default is 'theme.properties'
     */
    public static final String DEFAULT_THEME_PROPERTIES_FILE_NAME = "theme.properties";

    private final Map<String, Properties> themeProps = new ConcurrentHashMap<String, Properties>();

    public Map getThemeProps(Template template) {
        Properties props = themeProps.get(template.getTheme());
        if (props == null) {
            synchronized (themeProps) {
                props = readNewProperties(template);
                themeProps.put(template.getTheme(), props);
            }
        }
        return props;
    }

    private Properties readNewProperties(Template template) {
        String propName = buildPropertyFilename(template);
        return loadProperties(propName);
    }

    private Properties loadProperties(String propName) {
        InputStream is = readProperty(propName);
        Properties props = new Properties();
        if (is != null) {
            tryToLoadPropertiesFromStream(props, propName, is);
        }
        return props;
    }

    private InputStream readProperty(String propName) {
        InputStream is = tryReadingPropertyFileFromFileSystem(propName);
        if (is == null) {
            is = readPropertyFromClasspath(propName);
        }
        if (is == null) {
            is = readPropertyUsingServletContext(propName);
        }
        return is;
    }

    private InputStream readPropertyUsingServletContext(String propName) {
        ServletContext servletContext = ServletActionContext.getServletContext();
        String path = propName.startsWith("/") ? propName : "/" + propName;
        if (servletContext != null) {
            return servletContext.getResourceAsStream(path);
        } else {
            LOG.warn("ServletContext is null, cannot obtain #0", path);
            return null;
        }
    }

    /**
     * if its not in filesystem. let's try the classpath
     */
    private InputStream readPropertyFromClasspath(String propName) {
        return ClassLoaderUtil.getResourceAsStream(propName, getClass());
    }

    private void tryToLoadPropertiesFromStream(Properties props, String propName, InputStream is) {
        try {
            props.load(is);
        } catch (IOException e) {
            LOG.error("Could not load " + propName, e);
        } finally {
            tryCloseStream(is);
        }
    }

    private void tryCloseStream(InputStream is) {
        try {
            is.close();
        } catch (IOException io) {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Unable to close input stream", io);
            }
        }
    }

    private String buildPropertyFilename(Template template) {
        return template.getDir() + "/" + template.getTheme() + "/" + getThemePropertiesFileName();
    }

    /**
     * WW-1292 let's try getting it from the filesystem
     */
    private InputStream tryReadingPropertyFileFromFileSystem(String propName) {
        File propFile = new File(propName);
        try {
            return createFileInputStream(propFile);
        } catch (FileNotFoundException e) {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Unable to find file in filesystem [" + propFile.getAbsolutePath() + "]");
            }
            return null;
        }
    }

    private InputStream createFileInputStream(File propFile) throws FileNotFoundException {
        InputStream is = null;
        if (propFile.exists()) {
            is = new FileInputStream(propFile);
        }
        return is;
    }

    protected String getFinalTemplateName(Template template) {
        String t = template.toString();
        if (t.indexOf(".") <= 0) {
            return t + "." + getSuffix();
        }
        return t;
    }

    protected String getThemePropertiesFileName() {
        return DEFAULT_THEME_PROPERTIES_FILE_NAME;
    }

    protected abstract String getSuffix();

}
