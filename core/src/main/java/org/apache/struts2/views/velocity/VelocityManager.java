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
package org.apache.struts2.views.velocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.config.Settings;
import org.apache.struts2.util.VelocityStrutsUtil;
import org.apache.struts2.views.jsp.ui.OgnlTool;
import org.apache.struts2.views.util.ContextUtil;
import org.apache.struts2.views.velocity.components.ActionDirective;
import org.apache.struts2.views.velocity.components.ActionErrorDirective;
import org.apache.struts2.views.velocity.components.ActionMessageDirective;
import org.apache.struts2.views.velocity.components.AnchorDirective;
import org.apache.struts2.views.velocity.components.BeanDirective;
import org.apache.struts2.views.velocity.components.CheckBoxDirective;
import org.apache.struts2.views.velocity.components.CheckBoxListDirective;
import org.apache.struts2.views.velocity.components.ComboBoxDirective;
import org.apache.struts2.views.velocity.components.ComponentDirective;
import org.apache.struts2.views.velocity.components.DateDirective;
import org.apache.struts2.views.velocity.components.DatePickerDirective;
import org.apache.struts2.views.velocity.components.DivDirective;
import org.apache.struts2.views.velocity.components.DoubleSelectDirective;
import org.apache.struts2.views.velocity.components.FieldErrorDirective;
import org.apache.struts2.views.velocity.components.FileDirective;
import org.apache.struts2.views.velocity.components.FormDirective;
import org.apache.struts2.views.velocity.components.HeadDirective;
import org.apache.struts2.views.velocity.components.HiddenDirective;
import org.apache.struts2.views.velocity.components.I18nDirective;
import org.apache.struts2.views.velocity.components.IncludeDirective;
import org.apache.struts2.views.velocity.components.LabelDirective;
import org.apache.struts2.views.velocity.components.OptionTransferSelectDirective;
import org.apache.struts2.views.velocity.components.PanelDirective;
import org.apache.struts2.views.velocity.components.ParamDirective;
import org.apache.struts2.views.velocity.components.PasswordDirective;
import org.apache.struts2.views.velocity.components.PropertyDirective;
import org.apache.struts2.views.velocity.components.PushDirective;
import org.apache.struts2.views.velocity.components.RadioDirective;
import org.apache.struts2.views.velocity.components.ResetDirective;
import org.apache.struts2.views.velocity.components.SelectDirective;
import org.apache.struts2.views.velocity.components.SetDirective;
import org.apache.struts2.views.velocity.components.SubmitDirective;
import org.apache.struts2.views.velocity.components.TabbedPanelDirective;
import org.apache.struts2.views.velocity.components.TextAreaDirective;
import org.apache.struts2.views.velocity.components.TextDirective;
import org.apache.struts2.views.velocity.components.TextFieldDirective;
import org.apache.struts2.views.velocity.components.TokenDirective;
import org.apache.struts2.views.velocity.components.TreeDirective;
import org.apache.struts2.views.velocity.components.TreeNodeDirective;
import org.apache.struts2.views.velocity.components.URLDirective;
import org.apache.struts2.views.velocity.components.UpDownSelectDirective;
import org.apache.struts2.views.velocity.components.WebTableDirective;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.util.OgnlValueStack;


/**
 * Manages the environment for Velocity result types
 *
 */
public class VelocityManager {
    private static final Log log = LogFactory.getLog(VelocityManager.class);
    private static VelocityManager instance;
    public static final String STRUTS = "struts";

    /**
     * the parent JSP tag
     */
    public static final String PARENT = "parent";

    /**
     * the current JSP tag
     */
    public static final String TAG = "tag";

    private VelocityEngine velocityEngine;

    /**
     * A reference to the toolbox manager.
     */
    protected ToolboxManager toolboxManager = null;
    private String toolBoxLocation;


    /**
     * Names of contexts that will be chained on every request
     */
    private String[] chainedContextNames;
    
    private Properties velocityProperties;

    protected VelocityManager() {
        init();
    }

    /**
     * retrieve an instance to the current VelocityManager
     */
    public synchronized static VelocityManager getInstance() {
        if (instance == null) {
            String classname = VelocityManager.class.getName();

            if (Settings.isSet(StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME)) {
                classname = Settings.get(StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME).trim();
            }

            if (!classname.equals(VelocityManager.class.getName())) {
                try {
                    log.info("Instantiating VelocityManager!, " + classname);
                    // singleton instances shouldn't be built accessing request or session-specific context data
                    instance = (VelocityManager) ObjectFactory.getObjectFactory().buildBean(classname, null);
                } catch (Exception e) {
                    log.fatal("Fatal exception occurred while trying to instantiate a VelocityManager instance, " + classname, e);
                    instance = new VelocityManager();
                }
            } else {
                instance = new VelocityManager();
            }
        }

        return instance;
    }

    /**
     * @return a reference to the VelocityEngine used by <b>all</b> struts velocity thingies with the exception of
     *         directly accessed *.vm pages
     */
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * This method is responsible for creating the standard VelocityContext used by all WW2 velocity views.  The
     * following context parameters are defined:
     * <p/>
     * <ul>
     * <li><strong>req</strong> - the current HttpServletRequest</li>
     * <li><strong>res</strong> - the current HttpServletResponse</li>
     * <li><strong>stack</strong> - the current {@link OgnlValueStack}</li>
     * <li><strong>ognl</strong> - an {@link OgnlTool}</li>
     * <li><strong>struts</strong> - an instance of {@link org.apache.struts2.util.StrutsUtil}</li>
     * <li><strong>action</strong> - the current Struts action</li>
     * </ul>
     *
     * @return a new StrutsVelocityContext
     */
    public Context createContext(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        VelocityContext[] chainedContexts = prepareChainedContexts(req, res, stack.getContext());
        StrutsVelocityContext context = new StrutsVelocityContext(chainedContexts, stack);
        Map standardMap = ContextUtil.getStandardContext(stack, req, res);
        for (Iterator iterator = standardMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            context.put((String) entry.getKey(), entry.getValue());
        }
        context.put(STRUTS, new VelocityStrutsUtil(context, stack, req, res));


        ServletContext ctx = null;
        try {
            ctx = ServletActionContext.getServletContext();
        } catch (NullPointerException npe) {
            // in case this was used outside the lifecycle of struts servlet
            log.debug("internal toolbox context ignored");
        }

        if (toolboxManager != null && ctx != null) {
            ChainedContext chained = new ChainedContext(context, req, res, ctx);
            chained.setToolbox(toolboxManager.getToolboxContext(chained));
            return chained;
        } else {
            return context;
        }

    }

    /**
     * constructs contexts for chaining on this request.  This method does not
     * perform any initialization of the contexts.  All that must be done in the
     * context itself.
     *
     * @param servletRequest
     * @param servletResponse
     * @param extraContext
     * @return an VelocityContext[] of contexts to chain
     */
    protected VelocityContext[] prepareChainedContexts(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Map extraContext) {
        if (this.chainedContextNames == null) {
            return null;
        }
        List contextList = new ArrayList();
        for (int i = 0; i < chainedContextNames.length; i++) {
            String className = chainedContextNames[i];
            try {
                VelocityContext velocityContext = (VelocityContext) ObjectFactory.getObjectFactory().buildBean(className, null);
                contextList.add(velocityContext);
            } catch (Exception e) {
                log.warn("Warning.  " + e.getClass().getName() + " caught while attempting to instantiate a chained VelocityContext, " + className + " -- skipping");
            }
        }
        if (contextList.size() > 0) {
            VelocityContext[] extraContexts = new VelocityContext[contextList.size()];
            contextList.toArray(extraContexts);
            return extraContexts;
        } else {
            return null;
        }
    }

    /**
     * initializes the VelocityManager.  this should be called during the initialization process, say by
     * ServletDispatcher.  this may be called multiple times safely although calls beyond the first won't do anything
     *
     * @param context the current servlet context
     */
    public synchronized void init(ServletContext context) {
        if (velocityEngine == null) {
            velocityEngine = newVelocityEngine(context);
        }
        this.initToolbox(context);
    }

    /**
     * load optional velocity properties using the following loading strategy
     * <ul>
     * <li>relative to the servlet context path</li>
     * <li>relative to the WEB-INF directory</li>
     * <li>on the classpath</li>
     * </ul>
     *
     * @param context the current ServletContext.  may <b>not</b> be null
     * @return the optional properties if struts.velocity.configfile was specified, an empty Properties file otherwise
     */
    public Properties loadConfiguration(ServletContext context) {
        if (context == null) {
            String gripe = "Error attempting to create a loadConfiguration from a null ServletContext!";
            log.error(gripe);
            throw new IllegalArgumentException(gripe);
        }

        Properties properties = new Properties();

        // now apply our systemic defaults, then allow user to override
        applyDefaultConfiguration(context, properties);
        

        String defaultUserDirective = properties.getProperty("userdirective");

        /**
         * if the user has specified an external velocity configuration file, we'll want to search for it in the
         * following order
         *
         * 1. relative to the context path
         * 2. relative to /WEB-INF
         * 3. in the class path
         */
        String configfile;

        if (Settings.isSet(StrutsConstants.STRUTS_VELOCITY_CONFIGFILE)) {
            configfile = Settings.get(StrutsConstants.STRUTS_VELOCITY_CONFIGFILE);
        } else {
            configfile = "velocity.properties";
        }

        configfile = configfile.trim();

        InputStream in = null;
        String resourceLocation = null;

        try {
            if (context.getRealPath(configfile) != null) {
                // 1. relative to context path, i.e. /velocity.properties
                String filename = context.getRealPath(configfile);

                if (filename != null) {
                    File file = new File(filename);

                    if (file.isFile()) {
                        resourceLocation = file.getCanonicalPath() + " from file system";
                        in = new FileInputStream(file);
                    }

                    // 2. if nothing was found relative to the context path, search relative to the WEB-INF directory
                    if (in == null) {
                        file = new File(context.getRealPath("/WEB-INF/" + configfile));

                        if (file.isFile()) {
                            resourceLocation = file.getCanonicalPath() + " from file system";
                            in = new FileInputStream(file);
                        }
                    }
                }
            }

            // 3. finally, if there's no physical file, how about something in our classpath
            if (in == null) {
                in = VelocityManager.class.getClassLoader().getResourceAsStream(configfile);
                if (in != null) {
                    resourceLocation = configfile + " from classloader";
                }
            }

            // if we've got something, load 'er up
            if (in != null) {
                log.info("Initializing velocity using " + resourceLocation);
                properties.load(in);
            }
        } catch (IOException e) {
            log.warn("Unable to load velocity configuration " + resourceLocation, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        
        // overide with programmatically set properties
        if (this.velocityProperties != null) {
        	Iterator keys = this.velocityProperties.keySet().iterator();
        	while (keys.hasNext()) {
        		String key = (String) keys.next();
        		properties.setProperty(key, this.velocityProperties.getProperty(key));
			}
        }

        String userdirective = properties.getProperty("userdirective");

        if ((userdirective == null) || userdirective.trim().equals("")) {
            userdirective = defaultUserDirective;
        } else {
            userdirective = userdirective.trim() + "," + defaultUserDirective;
        }

        properties.setProperty("userdirective", userdirective);

        
        // for debugging purposes, allows users to dump out the properties that have been configured
        if (log.isDebugEnabled()) {
            log.debug("Initializing Velocity with the following properties ...");

            for (Iterator iter = properties.keySet().iterator();
                 iter.hasNext();) {
                String key = (String) iter.next();
                String value = properties.getProperty(key);

                if (log.isDebugEnabled()) {
                    log.debug("    '" + key + "' = '" + value + "'");
                }
            }
        }

        return properties;
    }

    /**
     * performs one-time initializations
     */
    protected void init() {

        // read in the names of contexts to add to each request
        initChainedContexts();


        if (Settings.isSet(StrutsConstants.STRUTS_VELOCITY_TOOLBOXLOCATION)) {
            toolBoxLocation = Settings.get(StrutsConstants.STRUTS_VELOCITY_TOOLBOXLOCATION).toString();
        }

    }


    /**
     * Initializes the ServletToolboxManager for this servlet's
     * toolbox (if any).
     */
    protected void initToolbox(ServletContext context) {
        /* if we have a toolbox, get a manager for it */
        if (toolBoxLocation != null) {
            toolboxManager = ServletToolboxManager.getInstance(context, toolBoxLocation);
        } else {
            Velocity.info("VelocityViewServlet: No toolbox entry in configuration.");
        }
    }


    /**
     * allow users to specify via the struts.properties file a set of additional VelocityContexts to chain to the
     * the StrutsVelocityContext.  The intent is to allow these contexts to store helper objects that the ui
     * developer may want access to.  Examples of reasonable VelocityContexts would be an IoCVelocityContext, a
     * SpringReferenceVelocityContext, and a ToolboxVelocityContext
     */
    protected void initChainedContexts() {

        if (Settings.isSet(StrutsConstants.STRUTS_VELOCITY_CONTEXTS)) {
            // we expect contexts to be a comma separated list of classnames
            String contexts = Settings.get(StrutsConstants.STRUTS_VELOCITY_CONTEXTS).toString();
            StringTokenizer st = new StringTokenizer(contexts, ",");
            List contextList = new ArrayList();

            while (st.hasMoreTokens()) {
                String classname = st.nextToken();
                contextList.add(classname);
            }
            if (contextList.size() > 0) {
                String[] chainedContexts = new String[contextList.size()];
                contextList.toArray(chainedContexts);
                this.chainedContextNames = chainedContexts;
            }


        }

    }

    /**
     * <p/>
     * Instantiates a new VelocityEngine.
     * </p>
     * <p/>
     * The following is the default Velocity configuration
     * </p>
     * <pre>
     *  resource.loader = file, class
     *  file.resource.loader.path = real path of webapp
     *  class.resource.loader.description = Velocity Classpath Resource Loader
     *  class.resource.loader.class = org.apache.struts2.views.velocity.StrutsResourceLoader
     * </pre>
     * <p/>
     * this default configuration can be overridden by specifying a struts.velocity.configfile property in the
     * struts.properties file.  the specified config file will be searched for in the following order:
     * </p>
     * <ul>
     * <li>relative to the servlet context path</li>
     * <li>relative to the WEB-INF directory</li>
     * <li>on the classpath</li>
     * </ul>
     *
     * @param context the current ServletContext.  may <b>not</b> be null
     */
    protected VelocityEngine newVelocityEngine(ServletContext context) {
        if (context == null) {
            String gripe = "Error attempting to create a new VelocityEngine from a null ServletContext!";
            log.error(gripe);
            throw new IllegalArgumentException(gripe);
        }

        Properties p = loadConfiguration(context);

        VelocityEngine velocityEngine = new VelocityEngine();
        
        //	Set the velocity attribute for the servlet context
        //  if this is not set the webapp loader WILL NOT WORK
        velocityEngine.setApplicationAttribute(ServletContext.class.getName(),
                context);

        try {
            velocityEngine.init(p);
        } catch (Exception e) {
            String gripe = "Unable to instantiate VelocityEngine!";
            throw new StrutsException(gripe, e);
        }

        return velocityEngine;
    }

    /**
     * once we've loaded up the user defined configurations, we will want to apply Struts specification configurations.
     * <ul>
     * <li>if Velocity.RESOURCE_LOADER has not been defined, then we will use the defaults which is a joined file,
     * class loader for unpackaed wars and a straight class loader otherwise</li>
     * <li>we need to define the various Struts custom user directives such as #param, #tag, and #bodytag</li>
     * </ul>
     *
     * @param context
     * @param p
     */
    private void applyDefaultConfiguration(ServletContext context, Properties p) {
        // ensure that caching isn't overly aggressive

        /**
         * Load a default resource loader definition if there isn't one present.
         * Ben Hall (22/08/2003)
         */
        if (p.getProperty(Velocity.RESOURCE_LOADER) == null) {
            p.setProperty(Velocity.RESOURCE_LOADER, "saffile, safclass");
        }

        /**
         * If there's a "real" path add it for the saffile resource loader.
         * If there's no real path and they haven't configured a loader then we change
         * resource loader property to just use the safclass loader
         * Ben Hall (22/08/2003)
         */
        if (context.getRealPath("") != null) {
            p.setProperty("saffile.resource.loader.description", "Velocity File Resource Loader");
            p.setProperty("saffile.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
            p.setProperty("saffile.resource.loader.path", context.getRealPath(""));
            p.setProperty("saffile.resource.loader.modificationCheckInterval", "2");
            p.setProperty("saffile.resource.loader.cache", "true");
        } else {
            // remove saffile from resource loader property
            String prop = p.getProperty(Velocity.RESOURCE_LOADER);
            if (prop.indexOf("saffile,") != -1) {
                prop = replace(prop, "saffile,", "");
            } else if (prop.indexOf(", saffile") != -1) {
                prop = replace(prop, ", saffile", "");
            } else if (prop.indexOf("saffile") != -1) {
                prop = replace(prop, "saffile", "");
            }

            p.setProperty(Velocity.RESOURCE_LOADER, prop);
        }

        /**
         * Refactored the Velocity templates for the Struts taglib into the classpath from the web path.  This will
         * enable Struts projects to have access to the templates by simply including the Struts jar file.
         * Unfortunately, there does not appear to be a macro for the class loader keywords
         * Matt Ho - Mon Mar 17 00:21:46 PST 2003
         */
        p.setProperty("safclass.resource.loader.description", "Velocity Classpath Resource Loader");
        p.setProperty("safclass.resource.loader.class", "org.apache.struts2.views.velocity.StrutsResourceLoader");
        p.setProperty("safclass.resource.loader.modificationCheckInterval", "2");
        p.setProperty("safclass.resource.loader.cache", "true");

        // components
        StringBuffer sb = new StringBuffer();

        addDirective(sb, ActionDirective.class);
        addDirective(sb, BeanDirective.class);
        addDirective(sb, CheckBoxDirective.class);
        addDirective(sb, CheckBoxListDirective.class);
        addDirective(sb, ComboBoxDirective.class);
        addDirective(sb, ComponentDirective.class);
        addDirective(sb, DateDirective.class);
        addDirective(sb, DatePickerDirective.class);
        addDirective(sb, DivDirective.class);
        addDirective(sb, DoubleSelectDirective.class);
        addDirective(sb, FileDirective.class);
        addDirective(sb, FormDirective.class);
        addDirective(sb, HeadDirective.class);
        addDirective(sb, HiddenDirective.class);
        addDirective(sb, AnchorDirective.class);
        addDirective(sb, I18nDirective.class);
        addDirective(sb, IncludeDirective.class);
        addDirective(sb, LabelDirective.class);
        addDirective(sb, PanelDirective.class);
        addDirective(sb, ParamDirective.class);
        addDirective(sb, PasswordDirective.class);
        addDirective(sb, PushDirective.class);
        addDirective(sb, PropertyDirective.class);
        addDirective(sb, RadioDirective.class);
        addDirective(sb, SelectDirective.class);
        addDirective(sb, SetDirective.class);
        addDirective(sb, SubmitDirective.class);
        addDirective(sb, ResetDirective.class);
        addDirective(sb, TabbedPanelDirective.class);
        addDirective(sb, TextAreaDirective.class);
        addDirective(sb, TextDirective.class);
        addDirective(sb, TextFieldDirective.class);
        addDirective(sb, TokenDirective.class);
        addDirective(sb, TreeDirective.class);
        addDirective(sb, TreeNodeDirective.class);
        addDirective(sb, URLDirective.class);
        addDirective(sb, WebTableDirective.class);
        addDirective(sb, ActionErrorDirective.class);
        addDirective(sb, ActionMessageDirective.class);
        addDirective(sb, FieldErrorDirective.class);
        addDirective(sb, OptionTransferSelectDirective.class);
        addDirective(sb, UpDownSelectDirective.class);

        String directives = sb.toString();

        String userdirective = p.getProperty("userdirective");
        if ((userdirective == null) || userdirective.trim().equals("")) {
            userdirective = directives;
        } else {
            userdirective = userdirective.trim() + "," + directives;
        }

        p.setProperty("userdirective", userdirective);
    }

    private void addDirective(StringBuffer sb, Class clazz) {
        sb.append(clazz.getName()).append(",");
    }

    private static final String replace(String string, String oldString, String newString) {
        if (string == null) {
            return null;
        }
        // If the newString is null, just return the string since there's nothing to replace.
        if (newString == null) {
            return string;
        }
        int i = 0;
        // Make sure that oldString appears at least once before doing any processing.
        if ((i = string.indexOf(oldString, i)) >= 0) {
            // Use char []'s, as they are more efficient to deal with.
            char[] string2 = string.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(string2.length);
            buf.append(string2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = string.indexOf(oldString, i)) > 0) {
                buf.append(string2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(string2, j, string2.length - j);
            return buf.toString();
        }
        return string;
    }

	/**
	 * @return the velocityProperties
	 */
	public Properties getVelocityProperties() {
		return velocityProperties;
	}

	/**
	 * @param velocityProperties the velocityProperties to set
	 */
	public void setVelocityProperties(Properties velocityProperties) {
		this.velocityProperties = velocityProperties;
	}
}
