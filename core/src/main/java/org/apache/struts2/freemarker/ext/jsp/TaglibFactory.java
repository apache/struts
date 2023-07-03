/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.freemarker.ext.jsp;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.log.Logger;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import org.apache.struts2.freemarker.ext.servlet.FreemarkerServlet;
import org.apache.struts2.freemarker.ext.servlet.HttpRequestHashModel;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.Tag;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * A hash model associated with a servlet context that can load JSP tag libraries associated with that servlet context.
 * An instance of this class is made available in the root data model of templates executed by
 * {@link FreemarkerServlet} under key {@code JspTaglibs}. It can be added to custom servlets as
 * well to enable JSP taglib integration in them as well.
 */
public class TaglibFactory implements TemplateHashModel {

    /**
     * The default of {@link #getClasspathTlds()}; an empty list.
     * 
     * @since 2.3.22
     */
    public static final List DEFAULT_CLASSPATH_TLDS = Collections.EMPTY_LIST;
    
    /**
     * The default of {@link #getMetaInfTldSources()}; a list that contains
     * {@link WebInfPerLibJarMetaInfTldSource#INSTANCE}, which gives the behavior described in the JSP 2.2
     * specification.
     * 
     * @since 2.3.22
     */
    public static final List/*<? extends MetaInfTldSource>*/ DEFAULT_META_INF_TLD_SOURCES
            = Collections.singletonList(WebInfPerLibJarMetaInfTldSource.INSTANCE);

    private static final Logger LOG = Logger.getLogger("freemarker.jsp");

    private static final int URL_TYPE_FULL = 0;
    private static final int URL_TYPE_ABSOLUTE = 1;
    private static final int URL_TYPE_RELATIVE = 2;

    private static final String META_INF_REL_PATH = "META-INF/";
    private static final String META_INF_ABS_PATH = "/META-INF/";
    private static final String DEFAULT_TLD_RESOURCE_PATH = META_INF_ABS_PATH + "taglib.tld";
    private static final String JAR_URL_ENTRY_PATH_START = "!/";

    private static final String PLATFORM_FILE_ENCODING = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");

    private final ServletContext servletContext;

    private ObjectWrapper objectWrapper;
    private List/*<MetaInfTldSource>*/ metaInfTldSources = DEFAULT_META_INF_TLD_SOURCES;
    private List/*<String>*/ classpathTlds = DEFAULT_CLASSPATH_TLDS;
    
    boolean test_emulateNoUrlToFileConversions = false;
    boolean test_emulateNoJarURLConnections = false;
    boolean test_emulateJarEntryUrlOpenStreamFails = false;    

    private final Object lock = new Object(); 
    private final Map taglibs = new HashMap();
    private final Map tldLocations = new HashMap();
    private List/*<String>*/ failedTldLocations = new ArrayList();
    private int nextTldLocationLookupPhase = 0;

    /**
    /**
     * Creates a new JSP taglib factory that will be used to load JSP tag libraries and functions for the web
     * application represented by the passed in {@link ServletContext}.
     * You should at least call {@link #setObjectWrapper(ObjectWrapper)} before start using this object.
     * 
     * <p>This object is only thread-safe after you have stopped calling its setter methods (and it was properly
     * published to the other threads; see JSR 133 (Java Memory Model)).
     * 
     * @param ctx
     *            The servlet context whose JSP tag libraries this factory will load.
     */
    public TaglibFactory(ServletContext ctx) {
        this.servletContext = ctx;
    }

    /**
     * Retrieves a JSP tag library identified by an URI. The matching of the URI to a JSP taglib is done as described in
     * the JSP 1.2 FCS specification.
     * 
     * @param taglibUri
     *            The URI used in templates to refer to the taglib (like {@code <%@ taglib uri="..." ... %>} in
     *            JSP). It can be any of the three forms allowed by the JSP specification: absolute URI (like
     *            {@code http://example.com/foo}), root relative URI (like {@code /bar/foo.tld}) and non-root relative
     *            URI (like {@code bar/foo.tld}). Note that if a non-root relative URI is used it's resolved relative to
     *            the URL of the current request. In this case, the current request is obtained by looking up a
     *            {@link HttpRequestHashModel} object named <tt>Request</tt> in the root data model.
     *            {@link FreemarkerServlet} provides this object under the expected name, and custom servlets that want
     *            to integrate JSP taglib support should do the same.
     * 
     * @return a {@link TemplateHashModel} representing the JSP taglib. Each element of this hash represents a single
     *         custom tag or EL function from the library, implemented as a {@link TemplateTransformModel} or
     *         {@link TemplateMethodModelEx}, respectively.
     */
    @Override
    public TemplateModel get(final String taglibUri) throws TemplateModelException {
        synchronized (lock) {
            {
                final Taglib taglib = (Taglib) taglibs.get(taglibUri);
                if (taglib != null) {
                    return taglib;
                }
            }

            boolean failedTldListAlreadyIncluded = false;
            final TldLocation tldLocation;
            final String normalizedTaglibUri;
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Locating TLD for taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + ".");
                }
                
                TldLocation explicitlyMappedTldLocation = getExplicitlyMappedTldLocation(taglibUri);
                if (explicitlyMappedTldLocation != null) {
                    tldLocation = explicitlyMappedTldLocation;
                    normalizedTaglibUri = taglibUri;
                } else {
                    // Taglib URI must be directly the path (no mapping).
                    
                    final int urlType;
                    try {
                        urlType = getUriType(taglibUri);
                    } catch (MalformedURLException e) {
                        throw new TaglibGettingException("Malformed taglib URI: " + StringUtil.jQuote(taglibUri), e);
                    }
                    if (urlType == URL_TYPE_RELATIVE) {
                        normalizedTaglibUri = resolveRelativeUri(taglibUri);
                    } else if (urlType == URL_TYPE_ABSOLUTE) {
                        normalizedTaglibUri = taglibUri;
                    } else if (urlType == URL_TYPE_FULL) {
                        // Per spec., full URI-s can only be resolved through explicit mapping
                        String failedTLDsList = getFailedTLDsList();
                        failedTldListAlreadyIncluded = true;
                        throw new TaglibGettingException("No TLD was found for the "
                                + StringUtil.jQuoteNoXSS(taglibUri) + " JSP taglib URI. (TLD-s are searched according "
                                + "the JSP 2.2 specification. In development- and embedded-servlet-container "
                                + "setups you may also need the "
                                + "\"" + FreemarkerServlet.INIT_PARAM_META_INF_TLD_LOCATIONS + "\" and "
                                + "\"" + FreemarkerServlet.INIT_PARAM_CLASSPATH_TLDS + "\" "
                                + FreemarkerServlet.class.getName() + " init-params or the similar system "
                                + "properites."
                                + (failedTLDsList == null
                                        ? ""
                                        : " Also note these TLD-s were skipped earlier due to errors; "
                                                + "see error in the log: " + failedTLDsList
                                ) + ")");
                    } else {
                        throw new BugException();
                    }

                    if (!normalizedTaglibUri.equals(taglibUri)) {
                        final Taglib taglib = (Taglib) taglibs.get(normalizedTaglibUri);
                        if (taglib != null) {
                            return taglib;
                        }
                    }

                    tldLocation = isJarPath(normalizedTaglibUri)
                                ? new ServletContextJarEntryTldLocation(
                                        normalizedTaglibUri, DEFAULT_TLD_RESOURCE_PATH)
                                : new ServletContextTldLocation(normalizedTaglibUri);
                }
            } catch (Exception e) {
                String failedTLDsList = failedTldListAlreadyIncluded ? null : getFailedTLDsList();
                throw new TemplateModelException(
                        "Error while looking for TLD file for " + StringUtil.jQuoteNoXSS(taglibUri)
                        + "; see cause exception."
                        + (failedTLDsList == null
                                ? ""
                                : " (Note: These TLD-s were skipped earlier due to errors; "
                                + "see errors in the log: " + failedTLDsList + ")"),
                        e);
            }

            try {
                return loadTaglib(tldLocation, normalizedTaglibUri);
            } catch (Exception e) {
                throw new TemplateModelException("Error while loading tag library for URI "
                        + StringUtil.jQuoteNoXSS(normalizedTaglibUri) + " from TLD location "
                        + StringUtil.jQuoteNoXSS(tldLocation) + "; see cause exception.",
                        e);
            }
        }
    }

    /**
     * Returns the joined list of failed TLD-s, or {@code null} if there was none.
     */
    private String getFailedTLDsList() {
        synchronized (failedTldLocations) {
            if (failedTldLocations.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < failedTldLocations.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(StringUtil.jQuote(failedTldLocations.get(i)));
            }
            return sb.toString();
        }
    }

    /**
     * Returns false.
     */
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    /**
     * See {@link #setObjectWrapper(ObjectWrapper)}.
     * 
     * @since 2.3.22
     */
    public ObjectWrapper getObjectWrapper() {
        return objectWrapper;
    }

    /**
     * Sets the {@link ObjectWrapper} used when building the JSP tag library {@link TemplateHashModel}-s from the TLD-s.
     * Usually, it should be the same {@link ObjectWrapper} that will be used inside the templates. {@code null} value
     * is only supported for backward compatibility. For custom EL functions to be exposed, it must be non-{@code null}
     * and an {@code intanceof} {@link BeansWrapper} (like typically, a {@link DefaultObjectWrapper}).
     * 
     * @since 2.3.22
     */
    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        checkNotStarted();
        this.objectWrapper = objectWrapper;
    }

    /**
     * See {@link #setMetaInfTldSources(List)}.
     * 
     * @since 2.3.22
     */
    public List/*<Pattern>*/ getMetaInfTldSources() {
        return metaInfTldSources;
    }

    /**
     * Sets the list of places where we will look for {@code META-INF/**}{@code /*.tld} files. By default this is a list
     * that only contains {@link WebInfPerLibJarMetaInfTldSource#INSTANCE}. This corresponds to the behavior that the
     * JSP specification describes. See the {@link MetaInfTldSource} subclasses for the possible values and their
     * meanings.
     * 
     * <p>
     * This is usually set via the init-params of {@link FreemarkerServlet}.
     * 
     * @param metaInfTldSources
     *            The list of {@link MetaInfTldSource} subclass instances. Their order matters if multiple TLD-s define
     *            a taglib with the same {@code taglib-uri}. In that case, the one found by the earlier
     *            {@link MetaInfTldSource} wins.
     * 
     * @see #setClasspathTlds(List)
     * 
     * @since 2.3.22
     */
    public void setMetaInfTldSources(List/*<? extends MetaInfTldSource>*/ metaInfTldSources) {
        checkNotStarted();
        NullArgumentException.check("metaInfTldSources", metaInfTldSources);
        this.metaInfTldSources = metaInfTldSources;
    }

    /**
     * See {@link #setClasspathTlds(List)}.
     * 
     * @since 2.3.22
     */
    public List/*<String>*/ getClasspathTlds() {
        return classpathTlds;
    }

    /**
     * Sets the class-loader resource paths of the TLD-s that aren't inside the locations covered by
     * {@link #setMetaInfTldSources(List)}, yet you want them to be discovered. They will be loaded with the class
     * loader provided by the servlet container.
     * 
     * <p>
     * This is usually set via the init-params of {@link FreemarkerServlet}. Otherwise it defaults to an empty list.
     * 
     * @param classpathTlds
     *            List of {@code String}-s, maybe {@code null}. Each item is a resource path, like
     *            {@code "/META-INF/my.tld"}. (Relative resource paths will be interpreted as root-relative.)
     * 
     * @see #setMetaInfTldSources(List)
     * 
     * @since 2.3.22
     */
    public void setClasspathTlds(List/*<String>*/ classpathTlds) {
        checkNotStarted();
        NullArgumentException.check("classpathTlds", classpathTlds);
        this.classpathTlds = classpathTlds;
    }

    private void checkNotStarted() {
        synchronized (lock) {
            if (nextTldLocationLookupPhase != 0) {
                throw new IllegalStateException(TaglibFactory.class.getName() + " object was already in use.");
            }
        }
    }

    private TldLocation getExplicitlyMappedTldLocation(final String uri) throws SAXException, IOException,
            TaglibGettingException {
        while (true) {
            final TldLocation tldLocation = (TldLocation) tldLocations.get(uri);
            if (tldLocation != null) {
                return tldLocation;
            }

            switch (nextTldLocationLookupPhase) {
            case 0:
                // Not in JSP spec.
                addTldLocationsFromClasspathTlds();
                break;
            case 1:
                // JSP 2.2 spec / JSP.7.3.3 (also JSP.3.2)
                addTldLocationsFromWebXml();
                break;
            case 2:
                // JSP 2.2 spec / JSP.7.3.4, FM-specific TLD processing order #1
                addTldLocationsFromWebInfTlds();
                break;
            case 3:
                // JSP 2.2 spec / JSP.7.3.4, FM-specific TLD processing order #2
                addTldLocationsFromMetaInfTlds();
                break;
            case 4:
                return null;
            default:
                throw new BugException();
            }
            nextTldLocationLookupPhase++;
        }
    }

    private void addTldLocationsFromWebXml() throws SAXException, IOException {
        LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/web.xml");

        WebXmlParser webXmlParser = new WebXmlParser();
        InputStream in = servletContext.getResourceAsStream("/WEB-INF/web.xml");
        if (in == null) {
            LOG.debug("No web.xml was found in servlet context");
            return;
        }
        try {
            parseXml(in, servletContext.getResource("/WEB-INF/web.xml").toExternalForm(), webXmlParser);
        } finally {
            in.close();
        }
    }

    private void addTldLocationsFromWebInfTlds()
            throws IOException, SAXException {
        LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/**/*.tld");
        addTldLocationsFromServletContextResourceTlds("/WEB-INF");
    }

    private void addTldLocationsFromServletContextResourceTlds(String basePath)
            throws IOException, SAXException {
        Set unsortedResourcePaths = servletContext.getResourcePaths(basePath);
        if (unsortedResourcePaths != null) {
            List/*<String>*/ resourcePaths = new ArrayList/*<String>*/(unsortedResourcePaths);
            Collections.sort(resourcePaths);
            // First process the files...
            for (Iterator it = resourcePaths.iterator(); it.hasNext(); ) {
                String resourcePath = (String) it.next();
                if (resourcePath.endsWith(".tld")) {
                    addTldLocationFromTld(new ServletContextTldLocation(resourcePath));
                }
            }
            // ... only later the directories
            for (Iterator it = resourcePaths.iterator(); it.hasNext(); ) {
                String resourcePath = (String) it.next();
                if (resourcePath.endsWith("/")) {
                    addTldLocationsFromServletContextResourceTlds(resourcePath);
                }
            }
        }
    }
    
    private void addTldLocationsFromMetaInfTlds() throws IOException, SAXException {
        if (metaInfTldSources == null || metaInfTldSources.isEmpty()) {
            return;
        }

        Set/*<URLWithExternalForm>*/ cpMetaInfDirUrlsWithEF = null;
        
        // Skip past the last "clear":
        int srcIdxStart = 0;
        for (int i = metaInfTldSources.size() - 1; i >= 0; i--) {
            if (metaInfTldSources.get(i) instanceof ClearMetaInfTldSource) {
                srcIdxStart = i + 1;
                break;
            }
        }
        
        for (int srcIdx = srcIdxStart; srcIdx < metaInfTldSources.size(); srcIdx++) {
            MetaInfTldSource miTldSource = (MetaInfTldSource) metaInfTldSources.get(srcIdx);
            
            if (miTldSource == WebInfPerLibJarMetaInfTldSource.INSTANCE) {
                addTldLocationsFromWebInfPerLibJarMetaInfTlds();
            } else if (miTldSource instanceof ClasspathMetaInfTldSource) {
                ClasspathMetaInfTldSource cpMiTldLocation = (ClasspathMetaInfTldSource) miTldSource;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Looking for TLD-s in "
                            + "classpathRoots[" + cpMiTldLocation.getRootContainerPattern() + "]"
                            + META_INF_ABS_PATH + "**/*.tld");
                }
                
                if (cpMetaInfDirUrlsWithEF == null) {
                    cpMetaInfDirUrlsWithEF = collectMetaInfUrlsFromClassLoaders();
                }

                for (Iterator iterator = cpMetaInfDirUrlsWithEF.iterator(); iterator.hasNext(); ) {
                    URLWithExternalForm urlWithEF = (URLWithExternalForm) iterator.next();
                    final URL url = urlWithEF.getUrl();
                    final boolean isJarUrl = isJarUrl(url);
                    final String urlEF = urlWithEF.externalForm;
                    
                    final String rootContainerUrl;
                    if (isJarUrl) {
                        int sep = urlEF.indexOf(JAR_URL_ENTRY_PATH_START);
                        rootContainerUrl = sep != -1 ? urlEF.substring(0, sep) : urlEF;
                    } else {
                        rootContainerUrl = urlEF.endsWith(META_INF_ABS_PATH)
                                ? urlEF.substring(0, urlEF.length() - META_INF_REL_PATH.length())
                                : urlEF;
                    }
                    
                    if (cpMiTldLocation.getRootContainerPattern().matcher(rootContainerUrl).matches()) {
                        final File urlAsFile = urlToFileOrNull(url);
                        if (urlAsFile != null) {
                            addTldLocationsFromFileDirectory(urlAsFile);
                        } else if (isJarUrl) {
                            addTldLocationsFromJarDirectoryEntryURL(url);
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Can't list entries under this URL; TLD-s won't be discovered here: "
                                        + urlWithEF.getExternalForm());
                            }
                        }
                    }
                }
            } else {
                throw new BugException();
            }
        }
    }
    
    private void addTldLocationsFromWebInfPerLibJarMetaInfTlds() throws IOException, SAXException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/lib/*.{jar,zip}" + META_INF_ABS_PATH
                    + "*.tld");
        }

        Set libEntPaths = servletContext.getResourcePaths("/WEB-INF/lib");
        if (libEntPaths != null) {
            for (Iterator iter = libEntPaths.iterator(); iter.hasNext(); ) {
                final String libEntryPath = (String) iter.next();
                if (isJarPath(libEntryPath)) {
                    addTldLocationsFromServletContextJar(libEntryPath);
                }
            }
        }
    }

    private void addTldLocationsFromClasspathTlds() throws SAXException, IOException, TaglibGettingException {
        if (classpathTlds == null || classpathTlds.size() == 0) {
            return;
        }
        
        LOG.debug("Looking for TLD locations in TLD-s specified in cfg.classpathTlds");
        
        for (Iterator it = classpathTlds.iterator(); it.hasNext(); ) {
            String tldResourcePath = (String) it.next();
            if (tldResourcePath.trim().length() == 0) {
                throw new TaglibGettingException("classpathTlds can't contain empty item"); 
            }
            
            if (!tldResourcePath.startsWith("/")) {
                tldResourcePath = "/" + tldResourcePath;
            }
            if (tldResourcePath.endsWith("/")) {
                throw new TaglibGettingException("classpathTlds can't specify a directory: " + tldResourcePath); 
            }
            
            ClasspathTldLocation tldLocation = new ClasspathTldLocation(tldResourcePath);
            InputStream in;
            try {
                in = tldLocation.getInputStream();
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Ignored classpath TLD location " + StringUtil.jQuoteNoXSS(tldResourcePath)
                            + " because of error", e);
                }
                in = null;
            }
            if (in != null) {
                try {
                    addTldLocationFromTld(in, tldLocation);
                } finally {
                    in.close();
                }
            }
        }
    }

    /**
     * Finds and processes *.tld inside a jar in the servet context.
     */
    private void addTldLocationsFromServletContextJar(
            final String jarResourcePath)
            throws IOException, MalformedURLException, SAXException {
        final String metaInfEntryPath = normalizeJarEntryPath(META_INF_ABS_PATH, true);
        
        // Null for non-random-access backing resource:
        final JarFile jarFile = servletContextResourceToFileOrNull(jarResourcePath);
        if (jarFile != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for " + META_INF_ABS_PATH + "*.tld-s in JarFile: servletContext:"
                        + jarResourcePath);
            }
            for (Enumeration/*<JarEntry>*/ entries = jarFile.entries(); entries.hasMoreElements(); ) {
                final JarEntry curEntry = (JarEntry) entries.nextElement();
                final String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
                if (curEntryPath.startsWith(metaInfEntryPath) && curEntryPath.endsWith(".tld")) {
                    addTldLocationFromTld(new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
                }
            }
        } else {  // jarFile == null => fall back to streamed access
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for " + META_INF_ABS_PATH
                        + "*.tld-s in ZipInputStream (slow): servletContext:" + jarResourcePath);
            }
    
            final InputStream in = servletContext.getResourceAsStream(jarResourcePath);
            if (in == null) {
                throw new IOException("ServletContext resource not found: " + jarResourcePath);
            }
            try {
                try (ZipInputStream zipIn = new ZipInputStream(in)) {
                    while (true) {
                        ZipEntry curEntry = zipIn.getNextEntry();
                        if (curEntry == null) break;

                        String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
                        if (curEntryPath.startsWith(metaInfEntryPath) && curEntryPath.endsWith(".tld")) {
                            addTldLocationFromTld(zipIn,
                                    new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
                        }
                    }
                }
            } finally {
                in.close();
            }
        }
    }

    /**
     * Finds and processes *.tld inside a directory in a jar.
     * 
     * @param jarBaseEntryUrl
     *            Something like "jar:file:/C:/foo%20bar/baaz.jar!/META-INF/". If this is not a jar(-like) URL, the
     *            behavior is undefined.
     */
    private void addTldLocationsFromJarDirectoryEntryURL(final URL jarBaseEntryUrl)
            throws IOException, MalformedURLException, SAXException {
        // Null for non-random-access backing resource:
        final JarFile jarFile;
        // Not null; the path of the directory *inside* the JAR where we will search
        // (like "/META-INF/" in "jar:file:/C:/foo%20bar/baaz.jar!/META-INF/"):
        final String baseEntryPath;
        // Null when URLConnection is used
        // (like "file:/C:/foo%20bar/baaz.jar" in "jar:file:/C:/foo%20bar/baaz.jar!/META-INF/"):
        final String rawJarContentUrlEF;
        {
            final URLConnection urlCon = jarBaseEntryUrl.openConnection();
            if (!test_emulateNoJarURLConnections && urlCon instanceof JarURLConnection) {
                final JarURLConnection jarCon = (JarURLConnection) urlCon;
                jarFile = jarCon.getJarFile();
                rawJarContentUrlEF = null; // Not used as we have a JarURLConnection
                baseEntryPath = normalizeJarEntryPath(jarCon.getEntryName(), true);
                if (baseEntryPath == null) {
                    throw newFailedToExtractEntryPathException(jarBaseEntryUrl);
                }
            } else {
                final String jarBaseEntryUrlEF = jarBaseEntryUrl.toExternalForm();
                final int jarEntrySepIdx = jarBaseEntryUrlEF.indexOf(JAR_URL_ENTRY_PATH_START);
                if (jarEntrySepIdx == -1) {
                    throw newFailedToExtractEntryPathException(jarBaseEntryUrl);
                }
                rawJarContentUrlEF = jarBaseEntryUrlEF.substring(jarBaseEntryUrlEF.indexOf(':') + 1, jarEntrySepIdx);
                baseEntryPath = normalizeJarEntryPath(
                        jarBaseEntryUrlEF.substring(jarEntrySepIdx + JAR_URL_ENTRY_PATH_START.length()), true);
    
                File rawJarContentAsFile = urlToFileOrNull(new URL(rawJarContentUrlEF));
                jarFile = rawJarContentAsFile != null ? new JarFile(rawJarContentAsFile) : null;
            }
        }
        if (jarFile != null) {  // jarFile == null => fall back to streamed access
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for " + META_INF_ABS_PATH + "**/*.tld-s in random access mode: "
                        + jarBaseEntryUrl);
            }
            for (Enumeration/*<JarEntry>*/ entries = jarFile.entries(); entries.hasMoreElements(); ) {
                final JarEntry curEntry = (JarEntry) entries.nextElement();
                final String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
                if (curEntryPath.startsWith(baseEntryPath) && curEntryPath.endsWith(".tld")) {
                    final String curEntryBaseRelativePath = curEntryPath.substring(baseEntryPath.length());
                    final URL tldUrl = createJarEntryUrl(jarBaseEntryUrl, curEntryBaseRelativePath);
                    addTldLocationFromTld(new JarEntryUrlTldLocation(tldUrl, null));
                }
            }
        } else {
            // Not a random-access file, so we fall back to the slower ZipInputStream approach.
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for " + META_INF_ABS_PATH + "**/*.tld-s in stream mode (slow): "
                        + rawJarContentUrlEF);
            }

            try (InputStream in = new URL(rawJarContentUrlEF).openStream()) {
                ZipInputStream zipIn = new ZipInputStream(in);
                try {
                    while (true) {
                        ZipEntry curEntry = zipIn.getNextEntry();
                        if (curEntry == null) break;

                        String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
                        if (curEntryPath.startsWith(baseEntryPath) && curEntryPath.endsWith(".tld")) {
                            final String curEntryBaseRelativePath = curEntryPath.substring(baseEntryPath.length());
                            final URL tldUrl = createJarEntryUrl(jarBaseEntryUrl, curEntryBaseRelativePath);
                            addTldLocationFromTld(zipIn, new JarEntryUrlTldLocation(tldUrl, null));
                        }
                    }
                } finally {
                    zipIn.close();
                }
            } catch (ZipException e) {
                // ZipException messages miss the zip URL
                IOException ioe = new IOException("Error reading ZIP (see cause excepetion) from: "
                        + rawJarContentUrlEF);
                try {
                    ioe.initCause(e);
                } catch (Exception e2) {
                    throw e;
                }
                throw ioe;
            }
        }
    }

    private void addTldLocationsFromFileDirectory(final File dir) throws IOException, SAXException {
        if (dir.isDirectory()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for *.tld-s in File directory: " + StringUtil.jQuoteNoXSS(dir));
            }
            File[] tldFiles = dir.listFiles(new FilenameFilter() {
    
                @Override
                public boolean accept(File urlAsFile, String name) {
                    return isTldFileNameIgnoreCase(name);
                }
    
            });
            if (tldFiles == null) {
                throw new IOException("Can't list this directory for some reason: " + dir);
            }
            for (int i = 0; i < tldFiles.length; i++) {
                final File file = tldFiles[i];
                addTldLocationFromTld(new FileTldLocation(file));
            }
        } else {
            LOG.warn("Skipped scanning for *.tld for non-existent directory: " + StringUtil.jQuoteNoXSS(dir));
        }
    }
    
    /**
     * Adds the TLD location mapping from the TLD itself.
     */
    private void addTldLocationFromTld(TldLocation tldLocation) throws IOException, SAXException {
        try (InputStream in = tldLocation.getInputStream()) {
            addTldLocationFromTld(in, tldLocation);
        }
    }

    /**
     * Use this overload only if you already have the {@link InputStream} for some reason, otherwise use
     * {@link #addTldLocationFromTld(TldLocation)}. 
     * 
     * @param reusedIn
     *            The stream that we already had (so we don't have to open a new one from the {@code tldLocation}).
     */
    private void addTldLocationFromTld(InputStream reusedIn, TldLocation tldLocation) throws SAXException,
            IOException {
        String taglibUri;
        try {
            taglibUri = getTaglibUriFromTld(reusedIn, tldLocation.getXmlSystemId());
        } catch (SAXException e) {
            LOG.error("Error while parsing TLD; skipping: " + tldLocation, e);
            synchronized (failedTldLocations) {
                failedTldLocations.add(tldLocation.toString());
            }
            taglibUri = null;
        }
        if (taglibUri != null) {
                addTldLocation(tldLocation, taglibUri);
        }
    }

    private void addTldLocation(TldLocation tldLocation, String taglibUri) {
        if (tldLocations.containsKey(taglibUri)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignored duplicate mapping of taglib URI " + StringUtil.jQuoteNoXSS(taglibUri)
                        + " to TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
            }
        } else {
            tldLocations.put(taglibUri, tldLocation);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mapped taglib URI " + StringUtil.jQuoteNoXSS(taglibUri)
                        + " to TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
            }
        }
    }

    private static Set/*<URLWithExternalForm>*/ collectMetaInfUrlsFromClassLoaders() throws IOException {
        final Set/*<URLWithExternalForm>*/ metainfDirUrls = new TreeSet();
    
        final ClassLoader tccl = tryGetThreadContextClassLoader();
        if (tccl != null) {
            collectMetaInfUrlsFromClassLoader(tccl, metainfDirUrls);
        }
    
        final ClassLoader cccl = TaglibFactory.class.getClassLoader();
        if (!isDescendantOfOrSameAs(tccl, cccl)) {
            collectMetaInfUrlsFromClassLoader(cccl, metainfDirUrls);
        }
        return metainfDirUrls;
    }

    private static void collectMetaInfUrlsFromClassLoader(ClassLoader cl, Set/* <URLWithExternalForm> */metainfDirUrls)
            throws IOException {
        Enumeration/*<URL>*/ urls = cl.getResources(META_INF_REL_PATH);
        if (urls != null) {
            while (urls.hasMoreElements()) {
                metainfDirUrls.add(new URLWithExternalForm((URL) urls.nextElement()));
            }
        }
    }

    private String getTaglibUriFromTld(InputStream tldFileIn, String tldFileXmlSystemId) throws SAXException, IOException {
        TldParserForTaglibUriExtraction tldParser = new TldParserForTaglibUriExtraction();
        parseXml(tldFileIn, tldFileXmlSystemId, tldParser);
        return tldParser.getTaglibUri();
    }

    /**
     * @param tldLocation
     *            The physical location of the TLD file
     * @param taglibUri
     *            The URI used in templates to refer to the taglib (like {@code <%@ taglib uri="..." ... %>} in JSP).
     */
    private TemplateHashModel loadTaglib(TldLocation tldLocation, String taglibUri) throws IOException, SAXException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading taglib for URI " + StringUtil.jQuoteNoXSS(taglibUri)
                    + " from TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
        }
        final Taglib taglib = new Taglib(servletContext, tldLocation, objectWrapper);
        taglibs.put(taglibUri, taglib);
        tldLocations.remove(taglibUri);
        return taglib;
    }

    private static void parseXml(InputStream in, String systemId, DefaultHandler handler)
            throws SAXException, IOException {
        InputSource inSrc = new InputSource();
        inSrc.setSystemId(systemId);
        inSrc.setByteStream(toCloseIgnoring(in));
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false); // Especially as we use dummy empty DTD-s
        XMLReader reader;
        try {
            reader = factory.newSAXParser().getXMLReader();
        } catch (ParserConfigurationException e) {
            // Not expected
            throw new RuntimeException("XML parser setup failed", e);
        }
        reader.setEntityResolver(new EmptyContentEntityResolver()); // To deal with referred DTD-s
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        
        reader.parse(inSrc);
    }

    private static String resolveRelativeUri(String uri) throws TaglibGettingException {
        TemplateModel reqHash;
        try {
            reqHash = Environment.getCurrentEnvironment().getVariable(
                    FreemarkerServlet.KEY_REQUEST_PRIVATE);
        } catch (TemplateModelException e) {
            throw new TaglibGettingException("Failed to get FreemarkerServlet request information", e);
        }
        if (reqHash instanceof HttpRequestHashModel) {
            HttpServletRequest req =
                    ((HttpRequestHashModel) reqHash).getRequest();
            String pi = req.getPathInfo();
            String reqPath = req.getServletPath();
            if (reqPath == null) {
                reqPath = "";
            }
            reqPath += (pi == null ? "" : pi);
            // We don't care about paths with ".." in them. If the container
            // wishes to resolve them on its own, let it be.
            int lastSlash = reqPath.lastIndexOf('/');
            if (lastSlash != -1) {
                return reqPath.substring(0, lastSlash + 1) + uri;
            } else {
                return '/' + uri;
            }
        }
        throw new TaglibGettingException(
                "Can't resolve relative URI " + uri + " as request URL information is unavailable.");
    }

    /**
     * Ignores attempts to close the stream.
     */
    private static FilterInputStream toCloseIgnoring(InputStream in) {
        return new FilterInputStream(in) {
            @Override
            public void close() {
                // Do nothing 
            }
        };
    }
    
    private static int getUriType(String uri) throws MalformedURLException {
        if (uri == null) {
            throw new IllegalArgumentException("null is not a valid URI");
        }
        if (uri.length() == 0) {
            throw new MalformedURLException("empty string is not a valid URI");
        }
        final char c0 = uri.charAt(0);
        if (c0 == '/') {
            return URL_TYPE_ABSOLUTE;
        }
        // Check if it conforms to RFC 3986 3.1 in order to qualify as ABS_URI
        if (c0 < 'a' || c0 > 'z') { // First char of scheme must be alpha
            return URL_TYPE_RELATIVE;
        }
        final int colon = uri.indexOf(':');
        if (colon == -1) { // Must have a colon
            return URL_TYPE_RELATIVE;
        }
        // Subsequent chars must be [a-z,0-9,+,-,.]
        for (int i = 1; i < colon; ++i) {
            final char c = uri.charAt(i);
            if ((c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '+' && c != '-' && c != '.') {
                return URL_TYPE_RELATIVE;
            }
        }
        return URL_TYPE_FULL;
    }

    private static boolean isJarPath(final String uriPath) {
        return uriPath.endsWith(".jar") || uriPath.endsWith(".zip");
    }
    
    private static boolean isJarUrl(URL url) {
        final String scheme = url.getProtocol();
        return "jar".equals(scheme) || "zip".equals(scheme)
                || "vfszip".equals(scheme) // JBoss AS
                || "wsjar".equals(scheme); // WebSphere
    }

    private static URL createJarEntryUrl(final URL jarBaseEntryUrl, String relativeEntryPath)
            throws MalformedURLException {
        if (relativeEntryPath.startsWith("/")) {
            relativeEntryPath = relativeEntryPath.substring(1);
        }
        try {
            return new URL(jarBaseEntryUrl, StringUtil.URLPathEnc(relativeEntryPath, PLATFORM_FILE_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new BugException();
        }
    }

    /**
     * Trying to hide any JarFile implementation inconsistencies.
     */
    private static String normalizeJarEntryPath(String jarEntryDirPath, boolean directory) {
        // Not know to be a problem, but to be in the safe side:
        if (!jarEntryDirPath.startsWith("/")) {
            jarEntryDirPath = "/" + jarEntryDirPath;
        }
    
        // Known to be a problem:
        if (directory && !jarEntryDirPath.endsWith("/")) {
            jarEntryDirPath = jarEntryDirPath + "/";
        }
    
        return jarEntryDirPath;
    }

    private static MalformedURLException newFailedToExtractEntryPathException(final URL url) {
        return new MalformedURLException("Failed to extract jar entry path from: " + url);
    }

    /**
     * Converts an URL to a {@code File} object, if the URL format (scheme) makes is possible.
     */
    private File urlToFileOrNull(URL url) {
        if (test_emulateNoUrlToFileConversions) {
            return null;
        }
        
        if (!"file".equals(url.getProtocol())) {
            return null;
        }
    
        String filePath;
        try {
            // Using URI instead of URL, so we get an URL-decoded path.
            filePath = url.toURI().getSchemeSpecificPart();
        } catch (URISyntaxException e) { // Can happen, as URI-s are stricter than legacy URL-s.
            // URL.getFile() doesn't decode %XX-s (used for spaces and non-US-ASCII letters usually), so we do.
            // As it was originally created for a file somewhere, we hope that it uses the platform default encoding.
            try {
                filePath = URLDecoder.decode(url.getFile(), PLATFORM_FILE_ENCODING);
            } catch (UnsupportedEncodingException e2) {
                throw new BugException(e2);
            }
        }
        return new File(filePath);
    }

    /**
     * Gets a servlet context resource as a {@link JarFile} if possible, return {@code null} otherwise.
     * For BC only, we try to get over errors during URL/JarFile construction, so then the caller can fall back to the
     * legacy ZipInputStream-based approach.
     */
    private JarFile servletContextResourceToFileOrNull(final String jarResourcePath) throws MalformedURLException,
            IOException {
        URL jarResourceUrl = servletContext.getResource(jarResourcePath);
        if (jarResourceUrl == null) {
            LOG.error("ServletContext resource URL was null (missing resource?): " + jarResourcePath);
            return null;
        }

        File jarResourceAsFile = urlToFileOrNull(jarResourceUrl);
        if (jarResourceAsFile == null) {
            // Expected - it's just not File
            return null;
        }

        if (!jarResourceAsFile.isFile()) {
            LOG.error("Jar file doesn't exist - falling back to stream mode: " + jarResourceAsFile);
            return null;
        }

        return new JarFile(jarResourceAsFile);
    }

    private static URL tryCreateServletContextJarEntryUrl(
            ServletContext servletContext, final String servletContextJarFilePath, final String entryPath) {
        try {
            final URL jarFileUrl = servletContext.getResource(servletContextJarFilePath);
            if (jarFileUrl == null) {
                throw new IOException("Servlet context resource not found: " + servletContextJarFilePath);
            }
            return new URL(
                    "jar:"
                    + jarFileUrl.toURI()
                    + JAR_URL_ENTRY_PATH_START
                    + URLEncoder.encode(
                            entryPath.startsWith("/") ? entryPath.substring(1) : entryPath,
                            PLATFORM_FILE_ENCODING));
        } catch (Exception e) {
            LOG.error("Couldn't get URL for serlvetContext resource "
                        + StringUtil.jQuoteNoXSS(servletContextJarFilePath)
                        + " / jar entry " + StringUtil.jQuoteNoXSS(entryPath),
                    e);
            return null;
        }
    }

    private static boolean isTldFileNameIgnoreCase(String name) {
        final int dotIdx = name.lastIndexOf('.');
        if (dotIdx < 0) return false;
        final String extension = name.substring(dotIdx + 1).toLowerCase();
        return extension.equalsIgnoreCase("tld");
    }

    private static ClassLoader tryGetThreadContextClassLoader() {
        ClassLoader tccl;
        try {
            tccl = Thread.currentThread().getContextClassLoader();
        } catch (SecurityException e) {
            // Suppress
            tccl = null;
            LOG.warn("Can't access Thread Context ClassLoader", e);
        }
        return tccl;
    }
    
    private static boolean isDescendantOfOrSameAs(ClassLoader descendant, ClassLoader parent) {
        while (true) {
            if (descendant == null) {
                return false;
            }
            if (descendant == parent) {
                return true;
            }
            descendant = descendant.getParent();
        }
    }
    
    /**
     * A location within which we will look for {@code META-INF/**}{@code /*.tld}-s. Used in the parameter to
     * {@link #setMetaInfTldSources}. See concrete subclasses for more.
     * 
     * @since 2.3.22
     */
    public static abstract class MetaInfTldSource {
        private MetaInfTldSource() { }
    }

    /**
     * To search TLD-s under <tt>sevletContext:/WEB-INF/lib/*.{jar,zip}/META-INF/**</tt><tt>/*.tld</tt>, as requested by
     * the JSP specification. Note that these also used to be in the classpath, so it's redundant to use this together
     * with a sufficiently permissive {@link ClasspathMetaInfTldSource}.
     * 
     * @since 2.3.22
     */
    public static final class WebInfPerLibJarMetaInfTldSource extends MetaInfTldSource {
        public final static WebInfPerLibJarMetaInfTldSource INSTANCE = new WebInfPerLibJarMetaInfTldSource();
        private WebInfPerLibJarMetaInfTldSource() { }; 
    }

    /**
     * To search TLD-s under {@code META-INF/**}{@code /*.tld} inside classpath root containers, that is, in directories
     * and jar-s that are in the classpath (or are visible for the class loader otherwise). It will only search inside
     * those roots whose URL matches the pattern specified in the constructor. It correctly handles when multiple roots
     * contain a TLD with the same name (typically, {@code META-INF/taglib.tld}), that is, those TLD-s won't shadow each
     * other, all of them will be loaded independently.
     * 
     * <p>
     * Note that this TLD discovery mechanism is not part of the JSP specification.
     * 
     * @since 2.3.22
     */
    public static final class ClasspathMetaInfTldSource extends MetaInfTldSource {
        
        private final Pattern rootContainerPattern; 
        
        /**
         * @param rootContainerPattern
         *            The pattern against which the classpath root container URL-s will be matched. For example, to only
         *            search in jar-s whose name ends with "taglib", the patter should be {@code ".*taglib\.jar$"}. To
         *            search everywhere, the pattern should be {@code ".*"}. The pattern need to match the whole URL,
         *            not just part of it.
         */
        public ClasspathMetaInfTldSource(Pattern rootContainerPattern) {
            this.rootContainerPattern = rootContainerPattern;
        }

        /**
         * See constructor argument: {@link #ClasspathMetaInfTldSource(Pattern)}.
         */
        public Pattern getRootContainerPattern() {
            return rootContainerPattern;
        };
        
    }

    /**
     * When it occurs in the {@link MetaInfTldSource} list, all {@link MetaInfTldSource}-s before it will be disabled.
     * This is useful when the list is assembled from multiple sources, and some want to re-start it, rather than append
     * to the end of it.
     * 
     * @see FreemarkerServlet#SYSTEM_PROPERTY_META_INF_TLD_SOURCES
     * @see TaglibFactory#setMetaInfTldSources(List)
     */
    public static final class ClearMetaInfTldSource extends MetaInfTldSource {
        public final static ClearMetaInfTldSource INSTANCE = new ClearMetaInfTldSource();
        private ClearMetaInfTldSource() { }; 
    }
    
    private interface TldLocation {
        
        /**
         * Reads the TLD file.
         * @return Not {@code null}
         */
        public abstract InputStream getInputStream() throws IOException;
        
        /**
         * The absolute URL of the TLD file.
         * @return Not {@code null}
         */
        public abstract String getXmlSystemId() throws IOException;
    }

    private interface InputStreamFactory {
        InputStream getInputStream();
    
    }

    private class ServletContextTldLocation implements TldLocation {
        
        private final String fileResourcePath;
    
        public ServletContextTldLocation(String fileResourcePath) {
            this.fileResourcePath = fileResourcePath;
        }
    
        @Override
        public InputStream getInputStream() throws IOException {
            final InputStream in = servletContext.getResourceAsStream(fileResourcePath);
            if (in == null) {
                throw newResourceNotFoundException();
            }
            return in;
        }
    
        @Override
        public String getXmlSystemId() throws IOException {
            final URL url = servletContext.getResource(fileResourcePath);
            return url != null ? url.toExternalForm() : null;
        }
        
        private IOException newResourceNotFoundException() {
            return new IOException("Resource not found: servletContext:" + fileResourcePath);
        }
        
        @Override
        public final String toString() {
            return "servletContext:" + fileResourcePath;
        }
    
    }
    

    /**
     * Points to plain class loader resource (regardless of if in what classpath root container it's in).
     */
    private static class ClasspathTldLocation implements TldLocation {
        
        private final String resourcePath;
    
        public ClasspathTldLocation(String resourcePath) {
            if (!resourcePath.startsWith("/")) {
                throw new IllegalArgumentException("\"resourcePath\" must start with /");
            }
            this.resourcePath = resourcePath;
        }
    
        @Override
        public String toString() {
            return "classpath:" + resourcePath;
        }
    
        @Override
        public InputStream getInputStream() throws IOException {
            ClassLoader tccl = tryGetThreadContextClassLoader();
            if (tccl != null) {
                InputStream ins = ClassUtil.getReasourceAsStream(tccl, resourcePath, true);
                if (ins != null) {
                    return ins;
                }
            }
            
            return ClassUtil.getReasourceAsStream(getClass(), resourcePath, false);
        }

        @Override
        public String getXmlSystemId() throws IOException {
            ClassLoader tccl = tryGetThreadContextClassLoader();
            if (tccl != null) {
                final URL url = tccl.getResource(resourcePath);
                if (url != null) { 
                    return url.toExternalForm();
                }
            }
            
            final URL url = getClass().getResource(resourcePath);
            return url == null ? null : url.toExternalForm();
        }
    
    }

    private abstract class JarEntryTldLocation implements TldLocation {

        /**
         * Can be {@code null} if there was some technical problem, but then
         * {@link #fallbackRawJarContentInputStreamFactory} and {@link #entryPath} will be non-{@code null}
         */
        private final URL entryUrl;
        private final InputStreamFactory fallbackRawJarContentInputStreamFactory;
        private final String entryPath;
        
        public JarEntryTldLocation(URL entryUrl, InputStreamFactory fallbackRawJarContentInputStreamFactory,
                String entryPath) {
            if (entryUrl == null) {
                NullArgumentException.check(fallbackRawJarContentInputStreamFactory);
                NullArgumentException.check(entryPath);
            }
            
            this.entryUrl = entryUrl;
            this.fallbackRawJarContentInputStreamFactory = fallbackRawJarContentInputStreamFactory;
            this.entryPath = entryPath != null ? normalizeJarEntryPath(entryPath, false) : null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (entryUrl != null) {
                try {
                    if (test_emulateJarEntryUrlOpenStreamFails) {
                        throw new RuntimeException("Test only");
                    }
                    return entryUrl.openStream();
                } catch (Exception e) {
                    if (fallbackRawJarContentInputStreamFactory == null) {
                        // Java 7 (Java 6?): We could just re-throw `e`
                        if (e instanceof IOException) {
                            throw (IOException) e;
                        }
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException) e;
                        }
                        throw new RuntimeException(e);
                    }
                    LOG.error("Failed to open InputStream for URL (will try fallback stream): " + entryUrl);
                }
                // Retry with the fallbackRawJarContentInputStreamFactory comes.
            }
            
            final String entryPath;
            if (this.entryPath != null) {
                entryPath = this.entryPath;
            } else {
                if (entryUrl == null) {
                    throw new IOException("Nothing to deduce jar entry path from.");
                }
                String urlEF = entryUrl.toExternalForm();
                int sepIdx = urlEF.indexOf(JAR_URL_ENTRY_PATH_START);
                if (sepIdx == -1) {
                    throw new IOException("Couldn't extract jar entry path from: " + urlEF);
                }
                entryPath = normalizeJarEntryPath(
                        URLDecoder.decode(
                                urlEF.substring(sepIdx + JAR_URL_ENTRY_PATH_START.length()),
                                PLATFORM_FILE_ENCODING),
                        false);
            }
            
            InputStream rawIn = null;
            ZipInputStream zipIn = null;
            boolean returnedZipIn = false;
            try {
                rawIn = fallbackRawJarContentInputStreamFactory.getInputStream();
                if (rawIn == null) {
                    throw new IOException("Jar's InputStreamFactory (" + fallbackRawJarContentInputStreamFactory
                            + ") says the resource doesn't exist.");
                }
                zipIn = new ZipInputStream(rawIn);
                while (true) {
                    final ZipEntry macthedJarEntry = zipIn.getNextEntry();
                    if (macthedJarEntry == null) {
                        throw new IOException("Could not find JAR entry " + StringUtil.jQuoteNoXSS(entryPath) + ".");
                    }
                    if (entryPath.equals(normalizeJarEntryPath(macthedJarEntry.getName(), false))) {
                        returnedZipIn = true;
                        return zipIn;
                    }
                }
            } finally {
                if (!returnedZipIn) {
                    if (zipIn != null) {
                        zipIn.close();
                    }
                    if (rawIn != null) {
                        rawIn.close();
                    }
                }
            }
        }
    
        @Override
        public String getXmlSystemId() {
            return entryUrl != null ? entryUrl.toExternalForm() : null;
        }
    
        @Override
        public String toString() {
            return entryUrl != null
                    ? entryUrl.toExternalForm()
                    : "jar:{" + fallbackRawJarContentInputStreamFactory + "}!" + entryPath;
        }
        
    }
    
    private class JarEntryUrlTldLocation extends JarEntryTldLocation {
        
        private JarEntryUrlTldLocation(URL entryUrl, InputStreamFactory fallbackRawJarContentInputStreamFactory) {
            super(entryUrl, fallbackRawJarContentInputStreamFactory, null);
        }
        
    }

    /**
     * Points to a file entry inside a jar, with optional {@link ZipInputStream} fallback.
     */
    private class ServletContextJarEntryTldLocation extends JarEntryTldLocation {
        
        /**
         * For creating instance based on the servlet context resource path of a jar.
         * While it tries to construct and use an URL that points directly to the target entry inside the jar, it will
         * operate even if these URL-related operations fail. 
         */
        private ServletContextJarEntryTldLocation(final String servletContextJarFilePath, final String entryPath) {
            super(
                    tryCreateServletContextJarEntryUrl(servletContext, servletContextJarFilePath, entryPath),
                    new InputStreamFactory() {
                        @Override
                        public InputStream getInputStream() {
                            return servletContext.getResourceAsStream(servletContextJarFilePath);
                        }

                        @Override
                        public String toString() {
                            return "servletContext:" + servletContextJarFilePath;
                        }
                    },
                    entryPath);
        }
        
    }

    private static class FileTldLocation implements TldLocation {

        private final File file;

        public FileTldLocation(File file) {
            this.file = file;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(file);
        }

        @Override
        public String getXmlSystemId() throws IOException {
            return file.toURI().toURL().toExternalForm();
        }

        @Override
        public String toString() {
            return file.toString();
        }

    }
    
    private static final class Taglib implements TemplateHashModel {
        private final Map tagsAndFunctions;

        Taglib(ServletContext ctx, TldLocation tldPath, ObjectWrapper wrapper) throws IOException, SAXException {
            tagsAndFunctions = parseToTagsAndFunctions(ctx, tldPath, wrapper);
        }

        @Override
        public TemplateModel get(String key) {
            return (TemplateModel) tagsAndFunctions.get(key);
        }

        @Override
        public boolean isEmpty() {
            return tagsAndFunctions.isEmpty();
        }

        private static final Map parseToTagsAndFunctions(
                ServletContext ctx, TldLocation tldLocation, ObjectWrapper objectWrapper) throws IOException, SAXException {
            final TldParserForTaglibBuilding tldParser = new TldParserForTaglibBuilding(objectWrapper);

            try (InputStream in = tldLocation.getInputStream()) {
                parseXml(in, tldLocation.getXmlSystemId(), tldParser);
            }
            
            EventForwarding eventForwarding = EventForwarding.getInstance(ctx);
            if (eventForwarding != null) {
                eventForwarding.addListeners(tldParser.getListeners());
            } else if (tldParser.getListeners().size() > 0) {
                throw new TldParsingSAXException(
                        "Event listeners specified in the TLD could not be " +
                                " registered since the web application doesn't have a" +
                                " listener of class " + EventForwarding.class.getName() +
                                ". To remedy this, add this element to web.xml:\n" +
                                "| <listener>\n" +
                                "|   <listener-class>" + EventForwarding.class.getName() + "</listener-class>\n" +
                                "| </listener>", null);
            }
            return tldParser.getTagsAndFunctions();
        }
    }

    private class WebXmlParser extends DefaultHandler {
        private static final String E_TAGLIB = "taglib";
        private static final String E_TAGLIB_LOCATION = "taglib-location";
        private static final String E_TAGLIB_URI = "taglib-uri";

        private StringBuilder cDataCollector;
        private String taglibUriCData;
        private String taglibLocationCData;
        private Locator locator;

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(
                String nsuri,
                String localName,
                String qName,
                Attributes atts) {
            if (E_TAGLIB_URI.equals(qName) || E_TAGLIB_LOCATION.equals(qName)) {
                cDataCollector = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] chars, int off, int len) {
            if (cDataCollector != null) {
                cDataCollector.append(chars, off, len);
            }
        }

        @Override
        public void endElement(String nsUri, String localName, String qName) throws TldParsingSAXException {
            if (E_TAGLIB_URI.equals(qName)) {
                taglibUriCData = cDataCollector.toString().trim();
                cDataCollector = null;
            } else if (E_TAGLIB_LOCATION.equals(qName)) {
                taglibLocationCData = cDataCollector.toString().trim();
                if (taglibLocationCData.length() == 0) {
                    throw new TldParsingSAXException("Required \"" + E_TAGLIB_URI + "\" element was missing or empty",
                            locator);
                }
                try {
                    if (getUriType(taglibLocationCData) == URL_TYPE_RELATIVE) {
                        taglibLocationCData = "/WEB-INF/" + taglibLocationCData;
                    }
                } catch (MalformedURLException e) {
                    throw new TldParsingSAXException("Failed to detect URI type for: " + taglibLocationCData, locator, e);
                }
                cDataCollector = null;
            } else if (E_TAGLIB.equals(qName)) {
                addTldLocation(
                        isJarPath(taglibLocationCData)
                                ? new ServletContextJarEntryTldLocation(
                                        taglibLocationCData, DEFAULT_TLD_RESOURCE_PATH)
                                : new ServletContextTldLocation(taglibLocationCData),
                        taglibUriCData);
            }
        }
    }

    private static class TldParserForTaglibUriExtraction extends DefaultHandler {
        private static final String E_URI = "uri";

        private StringBuilder cDataCollector;
        private String uri;

        TldParserForTaglibUriExtraction() {
        }

        String getTaglibUri() {
            return uri;
        }

        @Override
        public void startElement(
                String nsuri,
                String localName,
                String qName,
                Attributes atts) {
            if (E_URI.equals(qName)) {
                cDataCollector = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] chars, int off, int len) {
            if (cDataCollector != null) {
                cDataCollector.append(chars, off, len);
            }
        }

        @Override
        public void endElement(String nsuri, String localName, String qName) {
            if (E_URI.equals(qName)) {
                uri = cDataCollector.toString().trim();
                cDataCollector = null;
            }
        }
    }

    static final class TldParserForTaglibBuilding extends DefaultHandler {
        private static final String E_TAG = "tag";
        private static final String E_NAME = "name";
        private static final String E_TAG_CLASS = "tag-class";
        private static final String E_TAG_CLASS_LEGACY = "tagclass";

        private static final String E_FUNCTION = "function";
        private static final String E_FUNCTION_CLASS = "function-class";
        private static final String E_FUNCTION_SIGNATURE = "function-signature";

        private static final String E_LISTENER = "listener";
        private static final String E_LISTENER_CLASS = "listener-class";

        private final BeansWrapper beansWrapper;

        private final Map<String, TemplateModel> tagsAndFunctions = new HashMap<>();
        private final List listeners = new ArrayList();

        private Locator locator;
        private StringBuilder cDataCollector;

        private Stack stack = new Stack();

        private String tagNameCData;
        private String tagClassCData;
        private String functionNameCData;
        private String functionClassCData;
        private String functionSignatureCData;
        private String listenerClassCData;

        TldParserForTaglibBuilding(ObjectWrapper wrapper) {
            if (wrapper instanceof BeansWrapper) {
                beansWrapper = (BeansWrapper) wrapper;
            } else {
                beansWrapper = null;
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Custom EL functions won't be loaded because "
                            + (wrapper == null
                                    ? "no ObjectWrapper was specified for the TaglibFactory "
                                            + "(via TaglibFactory.setObjectWrapper(...), exists since 2.3.22)"
                                    : "the ObjectWrapper wasn't instance of " + BeansWrapper.class.getName())
                            + ".");
                }
            }
        }

        Map<String, TemplateModel> getTagsAndFunctions() {
            return tagsAndFunctions;
        }

        List getListeners() {
            return listeners;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(String nsUri, String localName, String qName, Attributes atts) {
            stack.push(qName);
            if (stack.size() == 3) {
                if (E_NAME.equals(qName) || E_TAG_CLASS_LEGACY.equals(qName) || E_TAG_CLASS.equals(qName)
                        || E_LISTENER_CLASS.equals(qName) || E_FUNCTION_CLASS.equals(qName)
                        || E_FUNCTION_SIGNATURE.equals(qName)) {
                    cDataCollector = new StringBuilder();
                }
            }
        }

        @Override
        public void characters(char[] chars, int off, int len) {
            if (cDataCollector != null) {
                cDataCollector.append(chars, off, len);
            }
        }

        @Override
        public void endElement(String nsuri, String localName, String qName) throws TldParsingSAXException {
            if (!stack.peek().equals(qName)) {
                throw new TldParsingSAXException("Unbalanced tag nesting at \"" + qName + "\" end-tag.", locator);
            }

            if (stack.size() == 3) {
                if (E_NAME.equals(qName)) {
                    if (E_TAG.equals(stack.get(1))) {
                        tagNameCData = pullCData();
                    } else if (E_FUNCTION.equals(stack.get(1))) {
                        functionNameCData = pullCData();
                    }
                } else if (E_TAG_CLASS_LEGACY.equals(qName) || E_TAG_CLASS.equals(qName)) {
                    tagClassCData = pullCData();
                } else if (E_LISTENER_CLASS.equals(qName)) {
                    listenerClassCData = pullCData();
                } else if (E_FUNCTION_CLASS.equals(qName)) {
                    functionClassCData = pullCData();
                } else if (E_FUNCTION_SIGNATURE.equals(qName)) {
                    functionSignatureCData = pullCData();
                }
            } else if (stack.size() == 2) {
                if (E_TAG.equals(qName)) {
                    checkChildElementNotNull(qName, E_NAME, tagNameCData);
                    checkChildElementNotNull(qName, E_TAG_CLASS, tagClassCData);

                    final Class tagClass = resoveClassFromTLD(tagClassCData, "custom tag", tagNameCData);

                    final TemplateModel customTagModel;
                    try {
                        if (Tag.class.isAssignableFrom(tagClass)) {
                            customTagModel = new TagTransformModel(tagNameCData, tagClass);
                        } else {
                            customTagModel = new SimpleTagDirectiveModel(tagNameCData, tagClass);
                        }
                    } catch (IntrospectionException e) {
                        throw new TldParsingSAXException(
                                "JavaBean introspection failed on custom tag class " + tagClassCData,
                                locator,
                                e);
                    }

                    TemplateModel replacedTagOrFunction = tagsAndFunctions.put(tagNameCData, customTagModel);
                    if (replacedTagOrFunction != null) {
                        if (CustomTagAndELFunctionCombiner.canBeCombinedAsELFunction(replacedTagOrFunction)) {
                            tagsAndFunctions.put(tagNameCData, CustomTagAndELFunctionCombiner.combine(
                                    customTagModel, (TemplateMethodModelEx) replacedTagOrFunction));
                        } else {
                            LOG.warn("TLD contains multiple tags with name " + StringUtil.jQuote(tagNameCData)
                                    + "; keeping only the last one.");
                        }
                    }

                    tagNameCData = null;
                    tagClassCData = null;
                } else if (E_FUNCTION.equals(qName) && beansWrapper != null) {
                    checkChildElementNotNull(qName, E_FUNCTION_CLASS, functionClassCData);
                    checkChildElementNotNull(qName, E_FUNCTION_SIGNATURE, functionSignatureCData);
                    checkChildElementNotNull(qName, E_NAME, functionNameCData);

                    final Class functionClass = resoveClassFromTLD(
                            functionClassCData, "custom EL function", functionNameCData);

                    final Method functionMethod;
                    try {
                        functionMethod = TaglibMethodUtil.getMethodByFunctionSignature(
                                functionClass, functionSignatureCData);
                    } catch (Exception e) {
                        throw new TldParsingSAXException(
                                "Error while trying to resolve signature " + StringUtil.jQuote(functionSignatureCData)
                                        + " on class " + StringUtil.jQuote(functionClass.getName())
                                        + " for custom EL function " + StringUtil.jQuote(functionNameCData) + ".",
                                locator,
                                e);
                    }

                    final int modifiers = functionMethod.getModifiers();
                    if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                        throw new TldParsingSAXException(
                                "The custom EL function method must be public and static: " + functionMethod,
                                locator);
                    }

                    final TemplateMethodModelEx elFunctionModel;
                    try {
                        elFunctionModel = beansWrapper.wrap(null, functionMethod);
                    } catch (Exception e) {
                        throw new TldParsingSAXException(
                                "FreeMarker object wrapping failed on method : " + functionMethod,
                                locator);
                    }

                    TemplateModel replacedTagOrFunction = tagsAndFunctions.put(functionNameCData, elFunctionModel);
                    if (replacedTagOrFunction != null) {
                        if (CustomTagAndELFunctionCombiner.canBeCombinedAsCustomTag(replacedTagOrFunction)) {
                            tagsAndFunctions.put(functionNameCData, CustomTagAndELFunctionCombiner.combine(
                                    replacedTagOrFunction, elFunctionModel));
                        } else {
                            LOG.warn("TLD contains multiple functions with name " + StringUtil.jQuote(functionNameCData)
                                    + "; keeping only the last one.");
                        }
                    }

                    functionNameCData = null;
                    functionClassCData = null;
                    functionSignatureCData = null;
                } else if (E_LISTENER.equals(qName)) {
                    checkChildElementNotNull(qName, E_LISTENER_CLASS, listenerClassCData);

                    final Class listenerClass = resoveClassFromTLD(listenerClassCData, E_LISTENER, null);

                    final Object listener;
                    try {
                        listener = listenerClass.newInstance();
                    } catch (Exception e) {
                        throw new TldParsingSAXException(
                                "Failed to create new instantiate from listener class " + listenerClassCData,
                                locator,
                                e);
                    }

                    listeners.add(listener);

                    listenerClassCData = null;
                }
            }

            stack.pop();
        }
        
        private String pullCData() {
            String r = cDataCollector.toString().trim();
            cDataCollector = null;
            return r;
        }

        private void checkChildElementNotNull(String parentElementName, String childElementName, String value)
                throws TldParsingSAXException {
            if (value == null) {
                throw new TldParsingSAXException(
                        "Missing required \"" + childElementName + "\" element inside the \""
                                + parentElementName + "\" element.", locator);
            }
        }

        private Class resoveClassFromTLD(String className, String entryType, String entryName)
                throws TldParsingSAXException {
            try {
                return ClassUtil.forName(className);
            } catch (LinkageError | ClassNotFoundException e) {
                throw newTLDEntryClassLoadingException(e, className, entryType, entryName);
            }
        }

        private TldParsingSAXException newTLDEntryClassLoadingException(Throwable e, String className,
                String entryType, String entryName)
                throws TldParsingSAXException {
            int dotIdx = className.lastIndexOf('.');
            if (dotIdx != -1) {
                dotIdx = className.lastIndexOf('.', dotIdx - 1);
            }
            boolean looksLikeNestedClass =
                    dotIdx != -1 && className.length() > dotIdx + 1
                            && Character.isUpperCase(className.charAt(dotIdx + 1));
            return new TldParsingSAXException(
                    (e instanceof ClassNotFoundException ? "Not found class " : "Can't load class ")
                            + StringUtil.jQuote(className) + " for " + entryType
                            + (entryName != null ? " " + StringUtil.jQuote(entryName) : "") + "."
                            + (looksLikeNestedClass
                                    ? " Hint: Before nested classes, use \"$\", not \".\"."
                                    : ""),
                    locator,
                    e);
        }

    }

    /**
     * Dummy resolver that returns 0 length content for all requests.
     */
    private static final class EmptyContentEntityResolver implements EntityResolver {
        
        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            InputSource is = new InputSource(new ByteArrayInputStream(new byte[0]));
            is.setPublicId(publicId);
            is.setSystemId(systemId);
            return is;
        }
    }

    /**
     * Redefines {@code SAXParseException#toString()} and {@code SAXParseException#getCause()} because it's broken on
     * Java 1.6 and earlier.
     */
    private static class TldParsingSAXException extends SAXParseException {
    
        private final Throwable cause;
    
        TldParsingSAXException(String message, Locator locator) {
            this(message, locator, null);
        }
    
        TldParsingSAXException(String message, Locator locator, Throwable e) {
            super(message, locator, e instanceof Exception ? (Exception) e : new Exception(
                    "Unchecked exception; see cause", e));
            cause = e;
        }
    
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(getClass().getName());
            sb.append(": ");
            int startLn = sb.length();
    
            String systemId = getSystemId();
            String publicId = getPublicId();
            if (systemId != null || publicId != null) {
                sb.append("In ");
                if (systemId != null) {
                    sb.append(systemId);
                }
                if (publicId != null) {
                    if (systemId != null) {
                        sb.append(" (public ID: ");
                    }
                    sb.append(publicId);
                    if (systemId != null) {
                        sb.append(')');
                    }
                }
            }
    
            int line = getLineNumber();
            if (line != -1) {
                sb.append(sb.length() != startLn ? ", at " : "At ");
                sb.append("line ");
                sb.append(line);
                int col = getColumnNumber();
                if (col != -1) {
                    sb.append(", column ");
                    sb.append(col);
                }
            }
    
            String message = getLocalizedMessage();
            if (message != null) {
                if (sb.length() != startLn) {
                    sb.append(":\n");
                }
                sb.append(message);
            }
    
            return sb.toString();
        }
    
        @Override
        public Throwable getCause() {
            Throwable superCause = super.getCause();
            return superCause == null ? this.cause : superCause;
        }
    
    }
    
    private static class URLWithExternalForm implements Comparable {

        private final URL url;
        private final String externalForm;

        public URLWithExternalForm(URL url) {
            this.url = url;
            this.externalForm = url.toExternalForm();
        }

        public URL getUrl() {
            return url;
        }

        public String getExternalForm() {
            return externalForm;
        }

        @Override
        public int hashCode() {
            return externalForm.hashCode();
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) return true;
            if (that == null) return false;
            if (getClass() != that.getClass()) return false;
            return !externalForm.equals(((URLWithExternalForm) that).externalForm);
        }

        @Override
        public String toString() {
            return "URLWithExternalForm(" + externalForm + ")";
        }

        @Override
        public int compareTo(Object that) {
            return this.getExternalForm().compareTo(((URLWithExternalForm) that).getExternalForm());
        }

    }
    
    private static class TaglibGettingException extends Exception {

        public TaglibGettingException(String message, Throwable cause) {
            super(message, cause);
        }

        public TaglibGettingException(String message) {
            super(message);
        }
        
    }

}
