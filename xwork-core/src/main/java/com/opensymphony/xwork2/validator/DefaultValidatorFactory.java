/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Default validator factory
 *
 * @version $Date$ $Id$
 * @author Jason Carreira
 * @author James House
 */
public class DefaultValidatorFactory implements ValidatorFactory {

    protected Map<String, String> validators = new HashMap<String, String>();
    private static Logger LOG = LoggerFactory.getLogger(DefaultValidatorFactory.class);
    protected ObjectFactory objectFactory;
    protected ValidatorFileParser validatorFileParser;

    @Inject
    public DefaultValidatorFactory(@Inject ObjectFactory objectFactory, @Inject ValidatorFileParser parser) {
        this.objectFactory = objectFactory;
        this.validatorFileParser = parser;
        parseValidators();
    }

    public Validator getValidator(ValidatorConfig cfg) {

        String className = lookupRegisteredValidatorType(cfg.getType());

        Validator validator;

        try {
            // instantiate the validator, and set configured parameters
            //todo - can this use the ThreadLocal?
            validator = objectFactory.buildValidator(className, cfg.getParams(), ActionContext.getContext().getContextMap());
        } catch (Exception e) {
            final String msg = "There was a problem creating a Validator of type " + className + " : caused by " + e.getMessage();
            throw new XWorkException(msg, e, cfg);
        }

        // set other configured properties
        validator.setMessageKey(cfg.getMessageKey());
        validator.setDefaultMessage(cfg.getDefaultMessage());
        validator.setMessageParameters(cfg.getMessageParams());
        if (validator instanceof ShortCircuitableValidator) {
            ((ShortCircuitableValidator) validator).setShortCircuit(cfg.isShortCircuit());
        }

        return validator;
    }

    public void registerValidator(String name, String className) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering validator of class " + className + " with name " + name);
        }

        validators.put(name, className);
    }

    public String lookupRegisteredValidatorType(String name) {
        // lookup the validator class mapped to the type name
        String className = validators.get(name);

        if (className == null) {
            throw new IllegalArgumentException("There is no validator class mapped to the name " + name);
        }

        return className;
    }

    private void parseValidators() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading validator definitions.");
        }

        List<File> files = new ArrayList<File>();
        try {
            // Get custom validator configurations via the classpath
            Iterator<URL> urls = ClassLoaderUtil.getResources("", DefaultValidatorFactory.class, false);
            while (urls.hasNext()) {
                URL u = urls.next();
                try {
                    URI uri = new URI(u.toExternalForm().replaceAll(" ", "%20"));
                    if (!uri.isOpaque() && "file".equalsIgnoreCase(uri.getScheme())) {
                        File f = new File(uri);
                        FilenameFilter filter = new FilenameFilter() {
                            public boolean accept(File file, String fileName) {
                                return fileName.contains("-validators.xml");
                            }
                        };
                        // First check if this is a directory
                        // If yes, then just do a "list" to get all files in this directory
                        // and match the filenames with *-validators.xml. If the filename
                        // matches then add to the list of files to be parsed
                        if (f.isDirectory()) {
                            try {
                                File[] ff = f.listFiles(filter);
                                if ( ff != null && ff.length > 0) {
                                    files.addAll(Arrays.asList(ff));
                                }
                            } catch (SecurityException se) {
                                LOG.error("Security Exception while accessing directory '" + f + "'", se);
                            }

                        } else {
                            // If this is not a directory, then get hold of the inputstream.
                            // If its not a ZipInputStream, then create a ZipInputStream out
                            // of it. The intention is to allow nested jar files to be scanned
                            // for *-validators.xml.
                            // Ex: struts-app.jar -> MyApp.jar -> Login-validators.xml should be
                            // parsed and loaded.
                            ZipInputStream zipInputStream = null;
                            try {
                                InputStream inputStream = u.openStream();
                                if (inputStream instanceof ZipInputStream) {
                                    zipInputStream = (ZipInputStream) inputStream;
                                } else {
                                    zipInputStream = new ZipInputStream(inputStream);
                                }
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    if (zipEntry.getName().endsWith("-validators.xml")) {
                                        if (LOG.isTraceEnabled()) {
                                            LOG.trace("Adding validator " + zipEntry.getName());
                                        }
                                        files.add(new File(zipEntry.getName()));
                                    }
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                            } finally {
                                //cleanup
                                if (zipInputStream != null) {
                                    zipInputStream.close();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Unable to load #0", ex, u.toString());
                }
            }
        } catch (IOException e) {
            throw new ConfigurationException("Unable to parse validators", e);
        }

        // Parse default validator configurations
        String resourceName = "com/opensymphony/xwork2/validator/validators/default.xml";
        retrieveValidatorConfiguration(resourceName);

        // Overwrite and extend defaults with application specific validator configurations
        resourceName = "validators.xml";
        retrieveValidatorConfiguration(resourceName);

        // Add custom (plugin) specific validator configurations
        for (File file : files) {
            retrieveValidatorConfiguration(file.getName());
        }
    }

    private void retrieveValidatorConfiguration(String resourceName) {
        InputStream is = ClassLoaderUtil.getResourceAsStream(resourceName, DefaultValidatorFactory.class);
        if (is != null) {
            validatorFileParser.parseValidatorDefinitions(validators, is, resourceName);
        }
    }
}
