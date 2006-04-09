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
package org.apache.struts.action2.views.freemarker;

import com.opensymphony.util.FileManager;
import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.views.JspSupportServlet;
import org.apache.struts.action2.views.freemarker.tags.StrutsModels;
import org.apache.struts.action2.views.util.ContextUtil;
import org.apache.struts.action2.StrutsConstants;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.util.OgnlValueStack;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


/**
 * Static Configuration Manager for the FreemarkerResult's configuration
 *
 * @author CameronBraid
 * @version $Date$ $Id$
 */
public class FreemarkerManager {

    private static final Log log = LogFactory.getLog(FreemarkerManager.class);
    public static final String CONFIG_SERVLET_CONTEXT_KEY = "freemarker.Configuration";
    public static final String KEY_EXCEPTION = "exception";

    // coppied from freemarker servlet - since they are private
    private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
    private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
    private static final String ATTR_SESSION_MODEL = ".freemarker.Session";
    private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";

    // coppied from freemarker servlet - so that there is no dependency on it
    public static final String KEY_APPLICATION = "Application";
    public static final String KEY_REQUEST_MODEL = "Request";
    public static final String KEY_SESSION_MODEL = "Session";
    public static final String KEY_JSP_TAGLIBS = "JspTaglibs";
    private static FreemarkerManager instance = null;


    /**
     * To allow for custom configuration of freemarker, sublcass this class "ConfigManager" and
     * set the Struts configuration property
     * <b>struts.freemarker.configmanager.classname</b> to the fully qualified classname.
     * <p/>
     * This allows you to override the protected methods in the ConfigMangaer
     * to programatically create your own Configuration instance
     */
    public final static synchronized FreemarkerManager getInstance() {
        if (instance == null) {
            String classname = FreemarkerManager.class.getName();

            if (Configuration.isSet(StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME)) {
                classname = Configuration.getString(StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME).trim();
            }

            try {
                log.info("Instantiating Freemarker ConfigManager!, " + classname);
                // singleton instances shouldn't be built accessing request or session-specific context data
                instance = (FreemarkerManager) ObjectFactory.getObjectFactory().buildBean(Class.forName(classname), null);
            } catch (Exception e) {
                log.fatal("Fatal exception occurred while trying to instantiate a Freemarker ConfigManager instance, " + classname, e);
            }
        }

        // if the instance creation failed, make sure there is a default instance
        if (instance == null) {
            instance = new FreemarkerManager();
        }

        return instance;
    }

    public final synchronized freemarker.template.Configuration getConfiguration(ServletContext servletContext) throws TemplateException {
        freemarker.template.Configuration config = (freemarker.template.Configuration) servletContext.getAttribute(CONFIG_SERVLET_CONTEXT_KEY);

        if (config == null) {
            config = createConfiguration(servletContext);

            // store this configuration in the servlet context
            servletContext.setAttribute(CONFIG_SERVLET_CONTEXT_KEY, config);
        }

        config.setWhitespaceStripping(true);

        return config;
    }

    public ScopesHashModel buildScopesHashModel(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper, OgnlValueStack stack) {
        ScopesHashModel model = new ScopesHashModel(wrapper, servletContext, request, stack);

        // Create hash model wrapper for servlet context (the application)
        // only need one thread to do this once, per servlet context
        synchronized (servletContext) {
            ServletContextHashModel servletContextModel = (ServletContextHashModel) servletContext.getAttribute(ATTR_APPLICATION_MODEL);

            if (servletContextModel == null) {

                GenericServlet servlet = JspSupportServlet.jspSupportServlet;
                // TODO if the jsp support  servlet isn't load-on-startup then it won't exist
                // if it hasn't been accessed, and a JSP page is accessed
                if (servlet != null) {
                    servletContextModel = new ServletContextHashModel(servlet, wrapper);
                    servletContext.setAttribute(ATTR_APPLICATION_MODEL, servletContextModel);
                    TaglibFactory taglibs = new TaglibFactory(servletContext);
                    servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, taglibs);
                }

            }

            model.put(KEY_APPLICATION, servletContextModel);
            model.put(KEY_JSP_TAGLIBS, (TemplateModel) servletContext.getAttribute(ATTR_JSP_TAGLIBS_MODEL));
        }

        // Create hash model wrapper for session
        TemplateHashModel sessionModel;

        HttpSession session = request.getSession(false);
        if (session != null) {
            model.put(KEY_SESSION_MODEL, new HttpSessionHashModel(session, wrapper));
        } else {
            // no session means no attributes ???
            //            model.put(KEY_SESSION_MODEL, new SimpleHash());
        }

        // Create hash model wrapper for the request
        HttpRequestHashModel requestModel = (HttpRequestHashModel) request.getAttribute(ATTR_REQUEST_MODEL);

        if ((requestModel == null) || (requestModel.getRequest() != request)) {
            requestModel = new HttpRequestHashModel(request, response, wrapper);
            request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
        }

        model.put(KEY_REQUEST_MODEL, requestModel);

        return model;
    }

    public void populateContext(ScopesHashModel model, OgnlValueStack stack, Object action, HttpServletRequest request, HttpServletResponse response) {
        // put the same objects into the context that the velocity result uses
        Map standard = ContextUtil.getStandardContext(stack, request, response);
        model.putAll(standard);

        // support for JSP exception pages, exposing the servlet or JSP exception
        Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");

        if (exception == null) {
            exception = (Throwable) request.getAttribute("javax.servlet.error.JspException");
        }

        if (exception != null) {
            model.put(KEY_EXCEPTION, exception);
        }
    }

    protected BeansWrapper getObjectWrapper() {
        return new StrutsBeanWrapper();
    }

    /**
     * The default template loader is a MultiTemplateLoader which includes
     * a ClassTemplateLoader and a WebappTemplateLoader (and a FileTemplateLoader depending on
     * the init-parameter 'TemplatePath').
     * <p/>
     * The ClassTemplateLoader will resolve fully qualified template includes
     * that begin with a slash. for example /com/company/template/common.ftl
     * <p/>
     * The WebappTemplateLoader attempts to resolve templates relative to the web root folder
     */
    protected TemplateLoader getTemplateLoader(ServletContext servletContext) {
        // construct a FileTemplateLoader for the init-param 'TemplatePath'
        FileTemplateLoader templatePathLoader = null;

        String templatePath = servletContext.getInitParameter("TemplatePath");
        if (templatePath == null) {
            templatePath = servletContext.getInitParameter("templatePath");
        }

        if (templatePath != null) {
            try {
                templatePathLoader = new FileTemplateLoader(new File(templatePath));
            } catch (IOException e) {
                log.error("Invalid template path specified: " + e.getMessage(), e);
            }
        }

        // presume that most apps will require the class and webapp template loader
        // if people wish to
        return templatePathLoader != null ?
                new MultiTemplateLoader(new TemplateLoader[]{
                        templatePathLoader,
                        new WebappTemplateLoader(servletContext),
                        new StrutsClassTemplateLoader()
                })
                : new MultiTemplateLoader(new TemplateLoader[]{
                new WebappTemplateLoader(servletContext),
                new StrutsClassTemplateLoader()
        });
    }

    /**
     * Create the instance of the freemarker Configuration object.
     * <p/>
     * this implementation
     * <ul>
     * <li>obtains the default configuration from Configuration.getDefaultConfiguration()
     * <li>sets up template loading from a ClassTemplateLoader and a WebappTemplateLoader
     * <li>sets up the object wrapper to be the BeansWrapper
     * <li>loads settings from the classpath file /freemarker.properties
     * </ul>
     *
     * @param servletContext
     */
    protected freemarker.template.Configuration createConfiguration(ServletContext servletContext) throws TemplateException {
    	freemarker.template.Configuration configuration = new freemarker.template.Configuration();

        configuration.setTemplateLoader(getTemplateLoader(servletContext));

        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        configuration.setObjectWrapper(getObjectWrapper());
        
        if (Configuration.isSet(StrutsConstants.STRUTS_I18N_ENCODING)) {
        	configuration.setDefaultEncoding(Configuration.getString(StrutsConstants.STRUTS_I18N_ENCODING));
        }

        loadSettings(servletContext, configuration);

        return configuration;
    }

    /**
     * Load the settings from the /freemarker.properties file on the classpath
     *
     * @see freemarker.template.Configuration#setSettings for the definition of valid settings
     */
    protected void loadSettings(ServletContext servletContext, freemarker.template.Configuration configuration) {
        try {
            InputStream in = FileManager.loadFile("freemarker.properties", FreemarkerManager.class);

            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                configuration.setSettings(p);
            }
        } catch (IOException e) {
            log.error("Error while loading freemarker settings from /freemarker.properties", e);
        } catch (TemplateException e) {
            log.error("Error while loading freemarker settings from /freemarker.properties", e);
        }
    }

    public SimpleHash buildTemplateModel(OgnlValueStack stack, Object action, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
        ScopesHashModel model = buildScopesHashModel(servletContext, request, response, wrapper, stack);
        populateContext(model, stack, action, request, response);
        model.put("ww", new StrutsModels(stack, request, response));
        return model;
    }
}
