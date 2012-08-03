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

package org.apache.struts2.jsf;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * * Initializes the JSF context for this request.
 * <p>
 * </P>
 * The JSF Application can additionaly be configured from the Struts.xml by
 * adding &lt;param&gt; tags to the jsfSetup &lt;interceptor-ref&gt;.
 * <p>
 * </p>
 * <b>Example struts.xml configuration:</b>
 *
 * <pre>
 *   &lt;interceptor-ref name=&quot;jsfSetup&quot;&gt;
 *       &lt;param name=&quot;actionListener&quot;&gt;&lt;/param&gt;
 *       &lt;param name=&quot;defaultRenderKitId&quot;&gt;&lt;/param&gt;
 *       &lt;param name=&quot;supportedLocale&quot;&gt;&lt;/param&gt;
 *       &lt;param name=&quot;defaultLocale&quot;&gt;&lt;/param&gt;
 *       &lt;param name=&quot;messageBundle&quot;&gt;&lt;/param&gt;
 *       &lt;param name=&quot;navigationHandler&quot;&gt;org.apache.struts2.jsf.StrutsNavigationHandler&lt;/param&gt;
 *       &lt;param name=&quot;propertyResolver&quot;&gt;&lt;/param&gt;
 *       &lt;param name=&quot;stateManager&quot;&gt;&lt;/param&gt;
 *       &lt;param name=&quot;variableResolver&quot;&gt;
 *           org.apache.myfaces.el.VariableResolverImpl
 *          ,org.apache.struts2.jsf.StrutsVariableResolver
 *       &lt;/param&gt;
 *       &lt;param name=&quot;viewHandler;&quot;&gt;org.apache.shale.tiles.TilesViewHandler&lt;/param&gt;
 *   &lt;/interceptor-ref&gt;
 * </pre>
 *
 * <p>
 * </p>
 * <b>Note: None of the parameters are required but all are shown in the example
 * for completeness.</b>
 */
public class FacesSetupInterceptor extends FacesSupport implements Interceptor {

    private static final long serialVersionUID = -621512342655103941L;

    private String lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;

    private FacesContextFactory facesContextFactory;

    private Lifecycle lifecycle;

    // jsf Application configuration
    private List<String> actionListener;

    private String defaultRenderKitId;

    private List<String> supportedLocale;

    private String defaultLocale;

    private String messageBundle;

    private List<String> navigationHandler;

    private List<String> propertyResolver;

    private List<String> stateManager;

    private List<String> variableResolver;

    private List<String> viewHandler;

    /**
     * Sets the lifecycle id
     *
     * @param id
     *            The id
     */
    public void setLifecycleId(String id) {
        this.lifecycleId = id;
    }

    /**
     * Initializes the lifecycle and factories
     */
    public void init() {
        try {
            facesContextFactory = (FacesContextFactory) FactoryFinder
                    .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        } catch (Exception ex) {
            log.debug("Unable to initialize faces", ex);
            return;
        }

        // Javadoc says: Lifecycle instance is shared across multiple
        // simultaneous requests, it must be implemented in a thread-safe
        // manner.
        // So we can acquire it here once:
        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
                .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        lifecycle = lifecycleFactory.getLifecycle(lifecycleId);

        Application application = ((ApplicationFactory) FactoryFinder
                .getFactory(FactoryFinder.APPLICATION_FACTORY))
                .getApplication();

        if (actionListener != null) {
            Iterator i = actionListener.iterator();
            application
                    .setActionListener((ActionListener) getApplicationObject(
                            ActionListener.class, i, application
                                    .getActionListener()));
        }
        if (defaultRenderKitId != null && defaultRenderKitId.length() > 0) {
            application.setDefaultRenderKitId(defaultRenderKitId);
        }
        if (messageBundle != null && messageBundle.length() > 0) {
            application.setMessageBundle(messageBundle);
        }
        if (supportedLocale != null) {
            List<Locale> locales = new ArrayList<Locale>();
            for (Iterator i = supportedLocale.iterator(); i.hasNext();) {
                locales.add(toLocale((String) i.next()));
            }
            application.setSupportedLocales(locales);
        }
        if (defaultLocale != null && defaultLocale.length() > 0) {
            application.setDefaultLocale(toLocale(defaultLocale));
        }
        if (navigationHandler != null) {
            Iterator i = navigationHandler.iterator();
            application
                    .setNavigationHandler((NavigationHandler) getApplicationObject(
                            NavigationHandler.class, i, application
                                    .getNavigationHandler()));
        }
        if (propertyResolver != null) {
            Iterator i = propertyResolver.iterator();
            application
                    .setPropertyResolver((PropertyResolver) getApplicationObject(
                            PropertyResolver.class, i, application
                                    .getPropertyResolver()));
        }
        if (stateManager != null) {
            Iterator i = stateManager.iterator();
            application.setStateManager((StateManager) getApplicationObject(
                    StateManager.class, i, application.getStateManager()));
        }
        if (variableResolver != null) {
            Iterator i = variableResolver.iterator();
            application
                    .setVariableResolver((VariableResolver) getApplicationObject(
                            VariableResolver.class, i, application
                                    .getVariableResolver()));
        }
        if (viewHandler != null) {
            Iterator i = viewHandler.iterator();
            application.setViewHandler((ViewHandler) getApplicationObject(
                    ViewHandler.class, i, application.getViewHandler()));
        }
    }

    /**
     * Creates the faces context for other phases.
     *
     * @param invocation
     *            The action invocation
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        if (facesContextFactory != null) {
            if (isFacesAction(invocation)) {

                invocation.getInvocationContext().put(
                        FacesInterceptor.FACES_ENABLED, Boolean.TRUE);

                FacesContext facesContext = facesContextFactory
                        .getFacesContext(ServletActionContext
                                .getServletContext(), ServletActionContext
                                .getRequest(), ServletActionContext
                                .getResponse(), lifecycle);

                setLifecycle(lifecycle);

                try {
                    return invocation.invoke();
                } finally {
                    facesContext.release();
                }
            }
        } else {
            throw new StrutsException(
                    "Unable to initialize jsf interceptors probably due missing JSF implementation libraries",
                    invocation.getProxy().getConfig());
        }
        return invocation.invoke();
    }

    /**
     * Cleans up the lifecycle and factories
     */
    public void destroy() {
        facesContextFactory = null;
        lifecycle = null;
    }

    /**
     * Determines if this action mapping will be have a JSF view
     *
     * @param inv
     *            The action invocation
     * @return True if the JSF interceptors should fire
     */
    protected boolean isFacesAction(ActionInvocation inv) {
        ActionConfig config = inv.getProxy().getConfig();
        if (config != null) {
            ResultConfig resultConfig = config.getResults().get(Action.SUCCESS);
            Class resClass = null;
            try {
                resClass = Class.forName(resultConfig.getClassName());
            } catch (ClassNotFoundException ex) {
                log.warn(
                        "Can't find result class, ignoring as a faces request",
                        ex);
            }
            if (resClass != null) {
                if (resClass.isAssignableFrom(FacesResult.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Constructs an object from a list of class names. This method supports
     * creating the objects using constructor delegation, if the requested class
     * supports it. Classes will be imbedded from top to bottom in the list with
     * the last class listed being the one that will be returned.
     *
     * @param interfaceClass
     *            The Class type that is expected to be returned
     * @param classNamesIterator
     *            An Iterator for a list of Strings that represent the class
     *            names
     * @param defaultObject
     *            The current Object that the jsf Application has set
     * @return
     */
    private Object getApplicationObject(Class interfaceClass,
            Iterator classNamesIterator, Object defaultObject) {
        Object current = defaultObject;

        while (classNamesIterator.hasNext()) {
            String implClassName = (String) classNamesIterator.next();
            Class implClass = null;

            try {
                implClass = ClassLoaderUtil.loadClass(implClassName, this
                        .getClass());
            } catch (ClassNotFoundException e1) {
                throw new IllegalArgumentException("Class " + implClassName
                        + " was not found.");
            }

            // check, if class is of expected interface type
            if (!interfaceClass.isAssignableFrom(implClass)) {
                throw new IllegalArgumentException("Class " + implClassName
                        + " is no " + interfaceClass.getName());
            }

            if (current == null) {
                // nothing to decorate
                try {
                    current = implClass.newInstance();
                } catch (InstantiationException e) {
                    log.error(e.getMessage(), e);
                    throw new StrutsException(e);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                    throw new StrutsException(e);
                }
            } else {
                // let's check if class supports the decorator pattern
                try {
                    Constructor delegationConstructor = implClass
                            .getConstructor(new Class[] { interfaceClass });
                    // impl class supports decorator pattern,
                    try {
                        // create new decorator wrapping current
                        current = delegationConstructor
                                .newInstance(new Object[] { current });
                    } catch (InstantiationException e) {
                        log.error(e.getMessage(), e);
                        throw new StrutsException(e);
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage(), e);
                        throw new StrutsException(e);
                    } catch (InvocationTargetException e) {
                        log.error(e.getMessage(), e);
                        throw new StrutsException(e);
                    }
                } catch (NoSuchMethodException e) {
                    // no decorator pattern support
                    try {
                        current = implClass.newInstance();
                    } catch (InstantiationException e1) {
                        log.error(e.getMessage(), e);
                        throw new StrutsException(e);
                    } catch (IllegalAccessException e1) {
                        log.error(e.getMessage(), e);
                        throw new StrutsException(e);
                    }
                }
            }
        }

        return current;
    }

    /**
     * Takes a comma delimited string of class names and stores the names in an
     * <code>ArrayList</code>. The incoming <code>String</code> will be
     * cleaned of any whitespace characters before the class names are stored.
     *
     * @param actionListener
     *            A comma delimited string of class names
     */
    public void setActionListener(String actionListener) {
        if (this.actionListener == null) {
            this.actionListener = new ArrayList<String>();
        }
        String clean = actionListener.replaceAll("[ \t\r\n]", "");
        String[] actionListenerNames = clean.split(",");

        for (int i = 0; i < actionListenerNames.length; i++) {
            this.actionListener.add(actionListenerNames[i]);
        }
    }

    /**
     * A <code>String</code> to be used as the defaultRenderKitId for the jsf
     * application. The incoming <code>String</code> will be cleaned of
     * whitespace characters.
     *
     * @param defaultRenderKitId
     *            The defaultRenderKitId
     */
    public void setDefaultRenderKitId(String defaultRenderKitId) {
        String clean = defaultRenderKitId.replaceAll("[ \t\r\n]", "");
        this.defaultRenderKitId = clean;
    }

    /**
     * Takes a comma delimited string of local names and stores the names in an
     * <code>ArrayList</code>. The incoming <code>String</code> will be
     * cleaned of any whitespace characters before the class names are stored.
     *
     * @param supportedLocale
     *            A comma delimited string of local names
     */
    public void setSupportedLocale(String supportedLocale) {
        if (this.supportedLocale == null) {
            this.supportedLocale = new ArrayList<String>();
        }
        String clean = supportedLocale.replaceAll("[ \t\r\n]", "");
        String[] supportedLocaleNames = clean.split(",");

        for (int i = 0; i < supportedLocaleNames.length; i++) {
            this.supportedLocale.add(supportedLocaleNames[i]);
        }
    }

    /**
     * Stores a String representation of the defaultLocale. The incoming
     * <code>String</code> will be cleaned of any whitespace characters before
     * the class names are stored.
     *
     * @param defaultLocale
     *            The default local
     */
    public void setDefaultLocale(String defaultLocale) {
        String clean = defaultLocale.replaceAll("[ \t\r\n]", "");
        this.defaultLocale = clean;
    }

    /**
     * Stores the messageBundle to be used to configure the jsf Application.
     *
     * @param messageBundle
     *            The messageBundle
     */
    public void setMessageBundle(String messageBundle) {
        String clean = messageBundle.replaceAll("[ \t\r\n]", "");
        this.messageBundle = clean;
    }

    /**
     * Takes a comma delimited string of class names and stores the names in an
     * <code>ArrayList</code>. The incoming <code>String</code> will be
     * cleaned of any whitespace characters before the class names are stored.
     *
     * @param navigationHandlerName
     *            A comma delimited string of class names
     */
    public void setNavigationHandler(String navigationHandlerName) {
        if (navigationHandler == null) {
            navigationHandler = new ArrayList<String>();
        }
        String clean = navigationHandlerName.replaceAll("[ \t\r\n]", "");
        String[] navigationHandlerNames = clean.split(",");

        for (int i = 0; i < navigationHandlerNames.length; i++) {
            navigationHandler.add(navigationHandlerNames[i]);
        }
    }

    /**
     * Takes a comma delimited string of class names and stores the names in an
     * <code>ArrayList</code>. The incoming <code>String</code> will be
     * cleaned of any whitespace characters before the class names are stored.
     *
     * @param propertyResolverName
     *            A comma delimited string of class names
     */
    public void setPropertyResolver(String propertyResolverName) {
        if (propertyResolver == null) {
            propertyResolver = new ArrayList<String>();
        }
        String clean = propertyResolverName.replaceAll("[ \t\r\n]", "");
        String[] propertyResolverNames = clean.split(",");

        for (int i = 0; i < propertyResolverNames.length; i++) {
            propertyResolver.add(propertyResolverNames[i]);
        }
    }

    /**
     * Takes a comma delimited string of class names and stores the names in an
     * <code>ArrayList</code>. The incoming <code>String</code> will be
     * cleaned of any whitespace characters before the class names are stored.
     *
     * @param stateManagerName
     *            A comma delimited string of class names
     */
    public void setStateManager(String stateManagerName) {
        if (stateManager == null) {
            stateManager = new ArrayList<String>();
        }
        String clean = stateManagerName.replaceAll("[ \t\r\n]", "");
        String[] stateManagerNames = clean.split(",");

        for (int i = 0; i < stateManagerNames.length; i++) {
            stateManager.add(stateManagerNames[i]);
        }
    }

    /**
     * Takes a comma delimited string of class names and stores the names in an
     * <code>ArrayList</code>. The incoming <code>String</code> will be
     * cleaned of any whitespace characters before the class names are stored.
     *
     * @param variableResolverName
     *            A comma delimited string of class names
     */
    public void setVariableResolver(String variableResolverName) {
        if (variableResolver == null) {
            variableResolver = new ArrayList<String>();
        }
        String clean = variableResolverName.replaceAll("[ \t\r\n]", "");
        String[] variableResolverNames = clean.split(",");

        for (int i = 0; i < variableResolverNames.length; i++) {
            variableResolver.add(variableResolverNames[i]);
        }
    }

    /**
     * Takes a comma delimited string of class names and stores the names in an
     * <code>ArrayList</code>. The incoming <code>String</code> will be
     * cleaned of any whitespace characters before the class names are stored.
     *
     * @param viewHandlerName
     *            A comma delimited string of class names
     */
    public void setViewHandler(String viewHandlerName) {
        if (viewHandler == null) {
            viewHandler = new ArrayList<String>();
        }
        String[] viewHandlerNames = viewHandlerName
                .split(",^[ \t\r\n]+|[ \t\r\n]+$");

        for (int i = 0; i < viewHandlerNames.length; i++) {
            viewHandler.add(viewHandlerNames[i]);
        }
    }

    /**
     * Converts a locale string to <code>Locale</code> class. Accepts both '_'
     * and '-' as separators for locale components.
     *
     * @param localeString
     *            string representation of a locale
     * @return Locale instance, compatible with the string representation
     */
    private Locale toLocale(String localeString) {
        if ((localeString == null) || (localeString.length() == 0)) {
            Locale locale = Locale.getDefault();
            if (log.isWarnEnabled())
                log
                        .warn("Locale name in faces-config.xml null or empty, setting locale to default locale : "
                                + locale.toString());
            return locale;
        }

        int separatorCountry = localeString.indexOf('_');
        char separator;
        if (separatorCountry >= 0) {
            separator = '_';
        } else {
            separatorCountry = localeString.indexOf('-');
            separator = '-';
        }

        String language, country, variant;
        if (separatorCountry < 0) {
            language = localeString;
            country = variant = "";
        } else {
            language = localeString.substring(0, separatorCountry);

            int separatorVariant = localeString.indexOf(separator,
                    separatorCountry + 1);
            if (separatorVariant < 0) {
                country = localeString.substring(separatorCountry + 1);
                variant = "";
            } else {
                country = localeString.substring(separatorCountry + 1,
                        separatorVariant);
                variant = localeString.substring(separatorVariant + 1);
            }
        }

        return new Locale(language, country, variant);
    }
}
