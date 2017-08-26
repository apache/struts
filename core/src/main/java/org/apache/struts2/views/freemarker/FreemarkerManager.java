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

package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core.TemplateClassResolver;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.utility.StringUtil;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.JspSupportServlet;
import org.apache.struts2.views.TagLibrary;
import org.apache.struts2.views.TagLibraryModelProvider;
import org.apache.struts2.views.util.ContextUtil;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Static Configuration Manager for the FreemarkerResult's configuration
 *
 * <p/>
 *
 * Possible extension points are :-
 * <ul>
 *   <li>createConfiguration method</li>
 *   <li>loadSettings method</li>
 *   <li>createTemplateLoader method</li>
 *   <li>populateContext method</li>
 * </ul>
 *
 * <p/>
 * <b> createConfiguration method </b><br/>
 * Create a freemarker Configuration.
 * <p/>
 *
 * <b> loadSettings method </b><br/>
 * Load freemarker settings, default to freemarker.properties (if found in classpath)
 * <p/>
 *
 * <b> createTemplateLoader method</b><br/>
 * create a freemarker TemplateLoader that loads freemarker template in the following order :-
 * <ol>
 *   <li>path defined in ServletContext init parameter named 'templatePath' or 'TemplatePath' (must be an absolute path)</li>
 *   <li>webapp classpath</li>
 *   <li>struts's static folder (under [STRUT2_SOURCE]/org/apache/struts2/static/</li>
 * </ol>
 * <p/>
 *
 * <b> populateContext method</b><br/>
 * populate the created model.
 *
 */
public class FreemarkerManager {

    // coppied from freemarker servlet - so that there is no dependency on it
     public static final String INITPARAM_TEMPLATE_PATH = "TemplatePath";
     public static final String INITPARAM_NOCACHE = "NoCache";
     public static final String INITPARAM_CONTENT_TYPE = "ContentType";
     public static final String DEFAULT_CONTENT_TYPE = "text/html";
     public static final String INITPARAM_DEBUG = "Debug";

     public static final String KEY_REQUEST = "Request";
     public static final String KEY_INCLUDE = "include_page";
     public static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
     public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
     public static final String KEY_SESSION = "Session";
     public static final String KEY_APPLICATION = "Application";
     public static final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
     public static final String KEY_JSP_TAGLIBS = "JspTaglibs";

     // Note these names start with dot, so they're essentially invisible from  a freemarker script.
     private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
     private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
     private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
     private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";

    // for sitemesh
    public static final String ATTR_TEMPLATE_MODEL = ".freemarker.TemplateModel";  // template model stored in request for siteMesh

    // for Struts
    public static final String KEY_REQUEST_PARAMETERS_STRUTS = "Parameters";

    public static final String KEY_HASHMODEL_PRIVATE = "__FreeMarkerManager.Request__";

    public static final String EXPIRATION_DATE;

    /**
     * Adds individual settings.
     *
     * @see freemarker.template.Configuration#setSettings for the definition of valid settings
     */
    boolean contentTypeEvaluated = false;

    static {
        // Generate expiration date that is one year from now in the past
        GregorianCalendar expiration = new GregorianCalendar();
        expiration.roll(Calendar.YEAR, -1);
        SimpleDateFormat httpDate =
                new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss z",
                        java.util.Locale.US);
        EXPIRATION_DATE = httpDate.format(expiration.getTime());
    }

    // end freemarker definitions...

    private static final Logger LOG = LoggerFactory.getLogger(FreemarkerManager.class);
    public static final String CONFIG_SERVLET_CONTEXT_KEY = "freemarker.Configuration";
    public static final String KEY_EXCEPTION = "exception";



    protected String templatePath;
    protected boolean nocache;
    protected boolean debug;
    protected Configuration config;
    protected ObjectWrapper wrapper;
    protected String contentType = null;
    protected boolean noCharsetInContentType = true;

    protected String encoding;
    protected boolean altMapWrapper;
    protected boolean cacheBeanWrapper;
    protected int mruMaxStrongSize;
    protected String templateUpdateDelay;
    protected Map<String,TagLibraryModelProvider> tagLibraries;
    protected Map<String, TagLibrary> oldTagLibraries;

    private FileManager fileManager;
    private FreemarkerThemeTemplateLoader themeTemplateLoader;

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    @Inject(StrutsConstants.STRUTS_FREEMARKER_WRAPPER_ALT_MAP)
    public void setWrapperAltMap(String val) {
        altMapWrapper = "true".equals(val);
    }
    
    @Inject(StrutsConstants.STRUTS_FREEMARKER_BEANWRAPPER_CACHE)
    public void setCacheBeanWrapper(String val) {
        cacheBeanWrapper = "true".equals(val);
    }
    
    @Inject(StrutsConstants.STRUTS_FREEMARKER_MRU_MAX_STRONG_SIZE)
    public void setMruMaxStrongSize(String size) {
        mruMaxStrongSize = Integer.parseInt(size);
    }
    
    @Inject(value = StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY, required = false)
    public void setTemplateUpdateDelay(String delay) {
    	templateUpdateDelay = delay;
    }
    
    @Inject
    public void setContainer(Container container) {
        Map<String,TagLibraryModelProvider> map = new HashMap<String,TagLibraryModelProvider>();
        Set<String> prefixes = container.getInstanceNames(TagLibraryModelProvider.class);
        for (String prefix : prefixes) {
            map.put(prefix, container.getInstance(TagLibraryModelProvider.class, prefix));
        }
        this.tagLibraries = Collections.unmodifiableMap(map);

        Map<String, TagLibrary> oldMap = new HashMap<String, TagLibrary>();
        Set<String> oldPrefixes = container.getInstanceNames(TagLibrary.class);
        for (String prefix : oldPrefixes) {
            oldMap.put(prefix, container.getInstance(TagLibrary.class, prefix));
        }
        this.oldTagLibraries = Collections.unmodifiableMap(oldMap);
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject
    public void setThemeTemplateLoader(FreemarkerThemeTemplateLoader themeTemplateLoader) {
        this.themeTemplateLoader = themeTemplateLoader;
    }

    public boolean getNoCharsetInContentType() {
        return noCharsetInContentType;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public boolean getNocache() {
        return nocache;
    }

    public boolean getDebug() {
        return debug;
    }

    public Configuration getConfig() {
        return config;
    }

    public ObjectWrapper getWrapper() {
        return wrapper;
    }

    public String getContentType() {
        return contentType;
    }

    public synchronized Configuration getConfiguration(ServletContext servletContext) {
        if (config == null) {
            try {
                init(servletContext);
            } catch (TemplateException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cannot load freemarker configuration: ",e);
                }
            }
            // store this configuration in the servlet context
            servletContext.setAttribute(CONFIG_SERVLET_CONTEXT_KEY, config);
        }
        return config;
    }

    public void init(ServletContext servletContext) throws TemplateException {
        config = createConfiguration(servletContext);

        // Set defaults:
        config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        contentType = DEFAULT_CONTENT_TYPE;

        // Process object_wrapper init-param out of order:
        wrapper = createObjectWrapper(servletContext);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using object wrapper of class " + wrapper.getClass().getName());
        }
        config.setObjectWrapper(wrapper);

        // Process TemplatePath init-param out of order:
        templatePath = servletContext.getInitParameter(INITPARAM_TEMPLATE_PATH);
        if (templatePath == null) {
            templatePath = servletContext.getInitParameter("templatePath");
        }

        configureTemplateLoader(createTemplateLoader(servletContext, templatePath));

        loadSettings(servletContext);
    }

    /** 
     * Sets the Freemarker Configuration's template loader with the FreemarkerThemeTemplateLoader 
     * at the top.
     * 
     * @see org.apache.struts2.views.freemarker.FreemarkerThemeTemplateLoader
     */
    protected void configureTemplateLoader(TemplateLoader templateLoader) {
        themeTemplateLoader.init(templateLoader);
        config.setTemplateLoader(themeTemplateLoader);
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
    protected Configuration createConfiguration(ServletContext servletContext) throws TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);

        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        if (mruMaxStrongSize > 0) {
            configuration.setSetting(Configuration.CACHE_STORAGE_KEY, "strong:" + mruMaxStrongSize);
        }
        if (templateUpdateDelay != null) {
            configuration.setSetting(Configuration.TEMPLATE_UPDATE_DELAY_KEY, templateUpdateDelay);
        }
        if (encoding != null) {
            configuration.setDefaultEncoding(encoding);
        }
        configuration.setLocalizedLookup(false);
        configuration.setWhitespaceStripping(true);

        LOG.debug("Sets NewBuiltinClassResolver to TemplateClassResolver.SAFER_RESOLVER");
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);

        return configuration;
    }


    protected ScopesHashModel buildScopesHashModel(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper, ValueStack stack) {
        ScopesHashModel model = new ScopesHashModel(wrapper, servletContext, request, stack);

        // Create hash model wrapper for servlet context (the application). We need one thread, once per servlet context
        synchronized (servletContext) {
            ServletContextHashModel servletContextModel = (ServletContextHashModel) servletContext.getAttribute(ATTR_APPLICATION_MODEL);
            if (servletContextModel == null) {
                // first try a JSP support servlet.  If it fails, default to the servlet.
                GenericServlet servlet = JspSupportServlet.jspSupportServlet;
                if (servlet != null) {
                    servletContextModel = new ServletContextHashModel(servlet, wrapper);
                    servletContext.setAttribute(ATTR_APPLICATION_MODEL, servletContextModel);
                } else {
                    servletContextModel = new ServletContextHashModel(servletContext, wrapper);
                    servletContext.setAttribute(ATTR_APPLICATION_MODEL, servletContextModel);
                }
                TaglibFactory taglibs = new TaglibFactory(servletContext);
                taglibs.setObjectWrapper(wrapper);
                servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, taglibs);
            }
            model.put(KEY_APPLICATION, servletContextModel);
            model.putUnlistedModel(KEY_APPLICATION_PRIVATE, servletContextModel);
        }
        model.put(KEY_JSP_TAGLIBS, (TemplateModel) servletContext.getAttribute(ATTR_JSP_TAGLIBS_MODEL));

        // Create hash model wrapper for session
        HttpSession session = request.getSession(false);
        if (session != null) {
            model.put(KEY_SESSION, new HttpSessionHashModel(session, wrapper));
        }

        // Create hash model wrapper for the request attributes
        HttpRequestHashModel requestModel = (HttpRequestHashModel) request.getAttribute(ATTR_REQUEST_MODEL);

        if ((requestModel == null) || (requestModel.getRequest() != request)) {
            requestModel = new HttpRequestHashModel(request, response, wrapper);
            request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
        }

        model.put(KEY_REQUEST, requestModel);


        // Create hash model wrapper for request parameters
        HttpRequestParametersHashModel reqParametersModel = (HttpRequestParametersHashModel) request.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
        if (reqParametersModel == null || requestModel.getRequest() != request) {
            reqParametersModel = new HttpRequestParametersHashModel(request);
            request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL, reqParametersModel);
        }
        model.put(ATTR_REQUEST_PARAMETERS_MODEL, reqParametersModel);
        model.put(KEY_REQUEST_PARAMETERS_STRUTS,reqParametersModel);

        return model;
    }

    protected ObjectWrapper createObjectWrapper(ServletContext servletContext) {
        StrutsBeanWrapper wrapper = new StrutsBeanWrapper(altMapWrapper);
        wrapper.setUseCache(cacheBeanWrapper);
        return wrapper;
    }


     /**
     * Create the template loader. The default implementation will create a
     * {@link ClassTemplateLoader} if the template path starts with "class://",
     * a {@link FileTemplateLoader} if the template path starts with "file://",
     * and a {@link WebappTemplateLoader} otherwise.
     * @param templatePath the template path to create a loader for
     * @return a newly created template loader
     * @throws IOException
     */
    protected TemplateLoader createTemplateLoader(ServletContext servletContext, String templatePath) {
        TemplateLoader templatePathLoader = null;

         try {
             if(templatePath!=null){
                 if (templatePath.startsWith("class://")) {
                     // substring(7) is intentional as we "reuse" the last slash
                     templatePathLoader = new ClassTemplateLoader(getClass(), templatePath.substring(7));
                 } else if (templatePath.startsWith("file://")) {
                     templatePathLoader = new FileTemplateLoader(new File(templatePath.substring(7)));
                 }
             }
         } catch (IOException e) {
             if (LOG.isErrorEnabled()) {
                LOG.error("Invalid template path specified: #0", e, e.getMessage());
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
     * Load the settings from the /freemarker.properties file on the classpath
     *
     * @see freemarker.template.Configuration#setSettings for the definition of valid settings
     */
    protected void loadSettings(ServletContext servletContext) {
        InputStream in = null;

        try {

            in = fileManager.loadFile(ClassLoaderUtil.getResource("freemarker.properties", getClass()));

            if (in != null) {
                Properties p = new Properties();
                p.load(in);

                for (Object o : p.keySet()) {
                    String name = (String) o;
                    String value = (String) p.get(name);

                    if (name == null) {
                        throw new IOException(
                                "init-param without param-name.  Maybe the freemarker.properties is not well-formed?");
                    }
                    if (value == null) {
                        throw new IOException(
                                "init-param without param-value.  Maybe the freemarker.properties is not well-formed?");
                    }
                    addSetting(name, value);
                }
            }
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error while loading freemarker settings from /freemarker.properties", e);
            }
        } catch (TemplateException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error while loading freemarker settings from /freemarker.properties", e);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch(IOException io) {
                    if (LOG.isWarnEnabled()) {
                	    LOG.warn("Unable to close input stream", io);
                    }
                }
            }
        }
    }

    public void addSetting(String name, String value) throws TemplateException {
        // Process all other init-params:
        if (name.equals(INITPARAM_NOCACHE)) {
            nocache = StringUtil.getYesNo(value);
        } else if (name.equals(INITPARAM_DEBUG)) {
            debug = StringUtil.getYesNo(value);
        } else if (name.equals(INITPARAM_CONTENT_TYPE)) {
            contentType = value;
        } else {
            config.setSetting(name, value);
        }

        if (contentType != null && !contentTypeEvaluated) {
            int i = contentType.toLowerCase().indexOf("charset=");
            contentTypeEvaluated = true;
            if (i != -1) {
                char c = ' ';
                i--;
                while (i >= 0) {
                    c = contentType.charAt(i);
                    if (!Character.isWhitespace(c)) break;
                    i--;
                }
                if (i == -1 || c == ';') {
                    noCharsetInContentType = false;
                }
            }
        }
    }



    public ScopesHashModel buildTemplateModel(ValueStack stack, Object action, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
        ScopesHashModel model = buildScopesHashModel(servletContext, request, response, wrapper, stack);
        populateContext(model, stack, action, request, response);
        if (tagLibraries != null) {
            for (String prefix : tagLibraries.keySet()) {
                model.put(prefix, tagLibraries.get(prefix).getModels(stack, request, response));
            }
        }

        if (oldTagLibraries != null) {
            for (String prefix : oldTagLibraries.keySet()) {
                model.put(prefix, oldTagLibraries.get(prefix).getFreemarkerModels(stack, request, response));
            }
        }

        //place the model in the request using the special parameter.  This can be retrieved for freemarker and velocity.
        request.setAttribute(ATTR_TEMPLATE_MODEL, model);

        return model;
    }


    protected void populateContext(ScopesHashModel model, ValueStack stack, Object action, HttpServletRequest request, HttpServletResponse response) {
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

}
