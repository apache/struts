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
package org.apache.struts2;

import org.apache.struts2.dispatcher.mapper.CompositeActionMapper;

/**
 * This class provides a central location for framework configuration keys
 * used to retrieve and store Struts configuration settings.
 */
public final class StrutsConstants {

    /** Whether Struts is in development mode or not */
    public static final String STRUTS_DEVMODE = "struts.devMode";

    /** Whether the localization messages should automatically be reloaded */
    public static final String STRUTS_I18N_RELOAD = "struts.i18n.reload";

    /** The encoding to use for localization messages */
    public static final String STRUTS_I18N_ENCODING = "struts.i18n.encoding";

    /** Whether to reload the XML configuration or not */
    public static final String STRUTS_CONFIGURATION_XML_RELOAD = "struts.configuration.xml.reload";

    /** The URL extension to use to determine if the request is meant for a Struts action */
    public static final String STRUTS_ACTION_EXTENSION = "struts.action.extension";

    /** Whether to use the alterative syntax for the tags or not */
    public static final String STRUTS_TAG_ALTSYNTAX = "struts.tag.altSyntax";

    /** The HTTP port used by Struts URLs */
    public static final String STRUTS_URL_HTTP_PORT = "struts.url.http.port";

    /** The HTTPS port used by Struts URLs */
    public static final String STRUTS_URL_HTTPS_PORT = "struts.url.https.port";

    /** The default includeParams method to generate Struts URLs */
    public static final String STRUTS_URL_INCLUDEPARAMS = "struts.url.includeParams";

    /** The com.opensymphony.xwork2.ObjectFactory implementation class */
    public static final String STRUTS_OBJECTFACTORY = "struts.objectFactory";

    /** The com.opensymphony.xwork2.util.ObjectTypeDeterminer implementation class */
    public static final String STRUTS_OBJECTTYPEDETERMINER = "struts.objectTypeDeterminer";

    /** The package containing actions that use Rife continuations */
    public static final String STRUTS_CONTINUATIONS_PACKAGE = "struts.continuations.package";

    /** The org.apache.struts2.config.Configuration implementation class */
    public static final String STRUTS_CONFIGURATION = "struts.configuration";

    /** The default locale for the Struts application */
    public static final String STRUTS_LOCALE = "struts.locale";

    /** Whether to use a Servlet request parameter workaround necessary for some versions of WebLogic */
    public static final String STRUTS_DISPATCHER_PARAMETERSWORKAROUND = "struts.dispatcher.parametersWorkaround";

    /** The org.apache.struts2.views.freemarker.FreemarkerManager implementation class */
    public static final String STRUTS_FREEMARKER_MANAGER_CLASSNAME = "struts.freemarker.manager.classname";

    /** org.apache.struts2.views.velocity.VelocityManager implementation class */
    public static final String STRUTS_VELOCITY_MANAGER_CLASSNAME = "struts.velocity.manager.classname";

    /** The Velocity configuration file path */
    public static final String STRUTS_VELOCITY_CONFIGFILE = "struts.velocity.configfile";

    /** The location of the Velocity toolbox */
    public static final String STRUTS_VELOCITY_TOOLBOXLOCATION = "struts.velocity.toolboxlocation";

    /** List of Velocity context names */
    public static final String STRUTS_VELOCITY_CONTEXTS = "struts.velocity.contexts";

    /** The directory containing UI templates */
    public static final String STRUTS_UI_TEMPLATEDIR = "struts.ui.templateDir";

    /** The default UI template theme */
    public static final String STRUTS_UI_THEME = "struts.ui.theme";

    /** The maximize size of a multipart request (file upload) */
    public static final String STRUTS_MULTIPART_MAXSIZE = "struts.multipart.maxSize";

    /** The directory to use for storing uploaded files */
    public static final String STRUTS_MULTIPART_SAVEDIR = "struts.multipart.saveDir";

    /**
     * The org.apache.struts2.dispatcher.multipart.MultiPartRequest parser implementation
     * for a multipart request (file upload)
     */
    public static final String STRUTS_MULTIPART_PARSER = "struts.multipart.parser";

    /** Whether Spring should autoWire or not */
    public static final String STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE = "struts.objectFactory.spring.autoWire";

    /** Whether Spring should use its class cache or not */
    public static final String STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE = "struts.objectFactory.spring.useClassCache";

    /** Whether or not XSLT templates should not be cached */
    public static final String STRUTS_XSLT_NOCACHE = "struts.xslt.nocache";

    /** Location of additional configuration properties files to load */
    public static final String STRUTS_CUSTOM_PROPERTIES = "struts.custom.properties";

    /** Location of additional localization properties files to load */
    public static final String STRUTS_CUSTOM_I18N_RESOURCES = "struts.custom.i18n.resources";

    /** The org.apache.struts2.dispatcher.mapper.ActionMapper implementation class */
    public static final String STRUTS_MAPPER_CLASS = "struts.mapper.class";

    /** Whether the Struts filter should serve static content or not */
    public static final String STRUTS_SERVE_STATIC_CONTENT = "struts.serve.static";

    /** If static content served by the Struts filter should set browser caching header properties or not */
    public static final String STRUTS_SERVE_STATIC_BROWSER_CACHE = "struts.serve.static.browserCache";

    /** Allows one to disable dynamic method invocation from the URL */
    public static final String STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION = "struts.enable.DynamicMethodInvocation";

    /** A list of configuration files automatically loaded by Struts */
    public static final String STRUTS_CONFIGURATION_FILES = "struts.configuration.files";

    /** Whether slashes in action names are allowed or not */
    public static final String STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES = "struts.enable.SlashesInActionNames";

    /** Prefix used by {@link CompositeActionMapper} to identified its containing {@link ActionMapper} class. */
    public static final String STRUTS_MAPPER_COMPOSITE = "struts.mapper.composite.";
}
