/*
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
package org.apache.struts2.config.providers;

import org.apache.struts2.FileManager;
import org.apache.struts2.FileManagerFactory;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.ClassPathFinder;
import org.apache.struts2.util.DomHelper;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import static java.lang.String.format;
import static java.util.Collections.emptyList;


/**
 * Looks in the classpath for an XML file, "struts.xml" by default,
 * and uses it for the XWork configuration.
 *
 * @author tmjee
 * @author Rainer Hermanns
 * @author Neo
 * @version $Revision$
 */
public abstract class XmlConfigurationProvider extends XmlDocConfigurationProvider {

    private static final Logger LOG = LogManager.getLogger(XmlConfigurationProvider.class);

    private final String configFileName;
    private final Set<String> loadedFileUrls = new HashSet<>();
    private Set<String> includedFileNames;
    protected FileManager fileManager;

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    public XmlConfigurationProvider() {
        this("struts.xml");
    }

    public XmlConfigurationProvider(String filename) {
        this.configFileName = filename;
    }

    @Override
    public void init(Configuration configuration) {
        super.init(configuration);
        includedFileNames = configuration.getLoadedFileNames();
        documents = parseFile(configFileName);
    }

    @Override
    public void loadPackages() throws ConfigurationException {
        super.loadPackages();
        documents = emptyList();
    }

    @Override
    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        LOG.trace("Parsing configuration file [{}]", configFileName);
        super.register(containerBuilder, props);
    }

    /**
     * Tells whether the ConfigurationProvider should reload its configuration. This method should only be called
     * if ConfigurationManager.isReloadingConfigs() is true.
     *
     * @return true if the file has been changed since the last time we read it
     */
    @Override
    public boolean needsReload() {
        return loadedFileUrls.stream().anyMatch(url -> fileManager.fileNeedsReloading(url));
    }

    protected List<Document> parseFile(String configFileName) {
        try {
            loadedFileUrls.clear();
            return loadConfigurationFiles(configFileName, null);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Error loading configuration file " + configFileName, e);
        }
    }

    protected List<Document> loadConfigurationFiles(String fileName, Element includeElement) {
        if (includedFileNames.contains(fileName)) {
            return emptyList();
        }
        LOG.debug("Loading action configurations from: {}", fileName);
        includedFileNames.add(fileName);

        Iterator<URL> urls = getURLs(fileName);
        if (urls == null) {
            return emptyList();
        }

        List<Document> docs = getDocs(urls, fileName, includeElement);
        List<Document> finalDocs = getFinalDocs(docs);
        LOG.debug("Loaded action configuration from: {}", fileName);
        return finalDocs;
    }

    protected Iterator<URL> getURLs(String fileName) {
        Iterator<URL> urls = null;
        try {
            urls = getConfigurationUrls(fileName);
        } catch (IOException ex) {
            LOG.debug("Ignoring file that does not exist: " + fileName, ex);
        }
        if (urls != null && !urls.hasNext()) {
            LOG.debug("Ignoring file that has no URLs: " + fileName);
            urls = null;
        }
        return urls;
    }

    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
        return ClassLoaderUtil.getResources(fileName, XmlConfigurationProvider.class, false);
    }

    protected List<Document> getDocs(Iterator<URL> urls, String fileName, Element includeElement) {
        List<Document> docs = new ArrayList<>();

        while (urls.hasNext()) {
            InputStream is = null;
            URL url = null;
            try {
                url = urls.next();
                is = fileManager.loadFile(url);
                InputSource in = new InputSource(is);
                in.setSystemId(url.toString());
                Document helperDoc = DomHelper.parse(in, dtdMappings);
                if (helperDoc != null) {
                    docs.add(helperDoc);
                }
                loadedFileUrls.add(url.toString());
            } catch (StrutsException e) {
                if (includeElement != null) {
                    throw new ConfigurationException("Unable to load " + url, e, includeElement);
                } else {
                    throw new ConfigurationException("Unable to load " + url, e);
                }
            } catch (Exception e) {
                throw new ConfigurationException("Caught exception while loading file " + fileName, e, includeElement);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOG.error("Unable to close input stream", e);
                    }
                }
            }
        }
        return docs;
    }

    protected List<Document> getFinalDocs(List<Document> docs) {
        List<Document> finalDocs = new ArrayList<>();
        docs.sort(Comparator.comparing(XmlHelper::getLoadOrder));
        for (Document doc : docs) {
            iterateElementChildren(doc, child -> {
                if (!"include".equals(child.getNodeName())) {
                    return;
                }

                String includeFileName = child.getAttribute("file");
                if (includeFileName.indexOf('*') == -1) {
                    finalDocs.addAll(loadConfigurationFiles(includeFileName, child));
                    return;
                }
                // handleWildCardIncludes(includeFileName, docs, child);
                ClassPathFinder wildcardFinder = new ClassPathFinder();
                wildcardFinder.setPattern(includeFileName);
                Vector<String> wildcardMatches = wildcardFinder.findMatches();
                for (String match : wildcardMatches) {
                    finalDocs.addAll(loadConfigurationFiles(match, child));
                }
            });
            finalDocs.add(doc);
        }
        return finalDocs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XmlConfigurationProvider xmlConfigurationProvider)) {
            return false;
        }
        return Objects.equals(configFileName, xmlConfigurationProvider.configFileName);
    }

    @Override
    public int hashCode() {
        return configFileName != null ? configFileName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return format("XmlConfigurationProvider{configFileName='%s'}", configFileName);
    }
}
