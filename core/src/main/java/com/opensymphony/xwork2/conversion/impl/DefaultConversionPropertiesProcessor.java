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
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.inject.EarlyInitializable;
import com.opensymphony.xwork2.inject.Initializable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DefaultConversionPropertiesProcessor implements ConversionPropertiesProcessor, EarlyInitializable {

    private static final Logger LOG = LogManager.getLogger(DefaultConversionPropertiesProcessor.class);

    private TypeConverterCreator converterCreator;
    private TypeConverterHolder converterHolder;

    @Inject
    public void setTypeConverterCreator(TypeConverterCreator converterCreator) {
        this.converterCreator = converterCreator;
    }

    @Inject
    public void setTypeConverterHolder(TypeConverterHolder converterHolder) {
        this.converterHolder = converterHolder;
    }

    @Override
    public void init() {
        LOG.debug("Processing default conversion properties files");
        processRequired("struts-default-conversion.properties");
        process("xwork-conversion.properties");
    }

    public void process(String propsName) {
        loadConversionProperties(propsName, false);
    }

    public void processRequired(String propsName) {
        loadConversionProperties(propsName, true);
    }

    public void loadConversionProperties(String propsName, boolean require) {
        try {
            Iterator<URL> resources = ClassLoaderUtil.getResources(propsName, getClass(), true);
            while (resources.hasNext()) {
                URL url = resources.next();
                Properties props = new Properties();
                props.load(url.openStream());

                LOG.debug("Processing conversion file [{}]", propsName);

                for (Object o : props.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String key = (String) entry.getKey();

                    try {
                        TypeConverter typeConverter = converterCreator.createTypeConverter((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t{}:{} [treated as TypeConverter {}]", key, entry.getValue(), typeConverter);
                        }
                        converterHolder.addDefaultMapping(key, typeConverter);
                    } catch (Exception e) {
                        LOG.error("Conversion registration error", e);
                    }
                }
            }
        } catch (IOException ex) {
            if (require) {
                throw new XWorkException("Cannot load conversion properties file: "+propsName, ex);
            } else {
                LOG.debug("Cannot load conversion properties file: {}", propsName, ex);
            }
        }
    }

}
