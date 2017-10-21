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
package org.apache.struts2.oval.interceptor;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.annotation.AnnotationsConfigurer;
import net.sf.oval.configuration.annotation.JPAAnnotationsConfigurer;
import net.sf.oval.configuration.xml.XMLConfigurer;

import org.apache.struts2.StrutsConstants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class DefaultOValValidationManager implements OValValidationManager {
    private static final Logger LOG = LogManager.getLogger(DefaultOValValidationManager.class);

    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";
    protected final Map<String, List<Configurer>> validatorCache = new HashMap<String, List<Configurer>>();
    protected final Map<String, Configurer> validatorFileCache = new HashMap<String, Configurer>();

    protected boolean validateJPAAnnotations;

    private boolean reloadConfigs;
    private FileManager fileManager;

    @Inject(value = StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, required = false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    public synchronized List<Configurer> getConfigurers(Class clazz, String context, boolean validateJPAAnnotations) {
        this.validateJPAAnnotations =validateJPAAnnotations;
        final String validatorKey = buildValidatorKey(clazz, context);

        if (validatorCache.containsKey(validatorKey)) {
            if (reloadConfigs) {
                List<Configurer> configurers = buildXMLConfigurers(clazz, context, true, null);

                //add an annotation configurer
                addAditionalConfigurers(configurers);
                validatorCache.put(validatorKey, configurers);
            }
        } else {
            List<Configurer> configurers = buildXMLConfigurers(clazz, context, false, null);

            //add an annotation configurer
            addAditionalConfigurers(configurers);
            validatorCache.put(validatorKey, configurers);
        }

        // get the set of validator configs
        return validatorCache.get(validatorKey);
    }

    private void addAditionalConfigurers(List<Configurer> configurers) {
        AnnotationsConfigurer annotationsConfigurer = new AnnotationsConfigurer();
        configurers.add(annotationsConfigurer);

        if (validateJPAAnnotations) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding support for JPA annotations validations in OVal");
            }
            configurers.add(new JPAAnnotationsConfigurer());
        }
    }

    protected static String buildValidatorKey(Class clazz, String context) {
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append("/");
        sb.append(context);
        return sb.toString();
    }

    private List<Configurer> buildXMLConfigurers(Class clazz, String context, boolean checkFile, Set<String> checked) {
        List<Configurer> configurers = new ArrayList<Configurer>();

        if (checked == null) {
            checked = new TreeSet<String>();
        } else if (checked.contains(clazz.getName())) {
            return configurers;
        }

        if (clazz.isInterface()) {
            for (Class anInterface : clazz.getInterfaces()) {
                configurers.addAll(buildXMLConfigurers(anInterface, context, checkFile, checked));
            }
        } else {
            if (!clazz.equals(Object.class)) {
                configurers.addAll(buildXMLConfigurers(clazz.getSuperclass(), context, checkFile, checked));
            }
        }

        // look for validators for implemented interfaces
        for (Class anInterface1 : clazz.getInterfaces()) {
            if (checked.contains(anInterface1.getName())) {
                continue;
            }

            addIfNotNull(configurers, buildClassValidatorConfigs(anInterface1, checkFile));

            if (context != null) {
                addIfNotNull(configurers, buildAliasValidatorConfigs(anInterface1, context, checkFile));
            }

            checked.add(anInterface1.getName());
        }

        addIfNotNull(configurers, buildClassValidatorConfigs(clazz, checkFile));

        if (context != null) {
            addIfNotNull(configurers, buildAliasValidatorConfigs(clazz, context, checkFile));
        }

        checked.add(clazz.getName());

        return configurers;
    }

    protected void addIfNotNull(List<Configurer> configurers, Configurer configurer) {
        if (configurer != null)
            configurers.add(configurer);
    }


    protected XMLConfigurer buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    protected XMLConfigurer buildClassValidatorConfigs(Class aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    protected XMLConfigurer loadFile(String fileName, Class clazz, boolean checkFile) {
        URL fileUrl = ClassLoaderUtil.getResource(fileName, clazz);
        if ((checkFile && fileManager.fileNeedsReloading(fileUrl)) || !validatorFileCache.containsKey(fileName)) {

            try (InputStream is = fileManager.loadFile(fileUrl)) {
                if (is != null) {
                    LOG.debug("Loading validation xml file [{}]", fileName);
                    XMLConfigurer configurer = new XMLConfigurer();
                    configurer.fromXML(is);
                    validatorFileCache.put(fileName, configurer);
                    return configurer;
                }
            } catch (IOException e) {
                LOG.error("Unable to close input stream for [{}] ", fileName, e);
            }
        } else {
            return (XMLConfigurer) validatorFileCache.get(fileName);
        }

        return null;
    }
}
