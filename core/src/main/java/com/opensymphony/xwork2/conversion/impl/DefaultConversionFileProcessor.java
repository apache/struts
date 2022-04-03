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

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of {@link ConversionFileProcessor}
 */
public class DefaultConversionFileProcessor implements ConversionFileProcessor {

    private static final Logger LOG = LogManager.getLogger(DefaultConversionFileProcessor.class);

    private FileManager fileManager;
    private TypeConverterCreator converterCreator;

    @Inject
    public void setFileManagerFactory(FileManagerFactory factory) {
        fileManager = factory.getFileManager();
    }

    @Inject
    public void setTypeConverterCreator(TypeConverterCreator converterCreator) {
        this.converterCreator = converterCreator;
    }

    public void process(Map<String, Object> mapping, Class clazz, String converterFilename) {
        try {
            InputStream is = fileManager.loadFile(ClassLoaderUtil.getResource(converterFilename, clazz));

            if (is != null) {
                LOG.debug("Processing conversion file [{}] for class [{}]", converterFilename, clazz);

                Properties prop = new Properties();
                prop.load(is);

                for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                    String key = (String) entry.getKey();

                    if (mapping.containsKey(key)) {
                        break;
                    }
                    // for keyProperty of Set
                    if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX)
                            || key.startsWith(DefaultObjectTypeDeterminer.CREATE_IF_NULL_PREFIX)) {
                        LOG.debug("\t{}:{} [treated as String]", key, entry.getValue());
                        mapping.put(key, entry.getValue());
                    }
                    //for properties of classes
                    else if (!(key.startsWith(DefaultObjectTypeDeterminer.ELEMENT_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX))
                            ) {
                        TypeConverter _typeConverter = converterCreator.createTypeConverter((String) entry.getValue());
                        LOG.debug("\t{}:{} [treated as TypeConverter {}]", key, entry.getValue(), _typeConverter);
                        mapping.put(key, _typeConverter);
                    }
                    //for keys of Maps
                    else if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX)) {

                        Class converterClass = ClassLoaderUtil.loadClass((String) entry.getValue(), this.getClass());

                        //check if the converter is a type converter if it is one
                        //then just put it in the map as is. Otherwise
                        //put a value in for the type converter of the class
                        if (converterClass.isAssignableFrom(TypeConverter.class)) {
                            TypeConverter _typeConverter = converterCreator.createTypeConverter((String) entry.getValue());
                            LOG.debug("\t{}:{} [treated as TypeConverter {}]", key, entry.getValue(), _typeConverter);
                            mapping.put(key, _typeConverter);
                        } else {
                            LOG.debug("\t{}:{} [treated as Class {}]", key, entry.getValue(), converterClass);
                            mapping.put(key, converterClass);
                        }
                    }
                    //elements(values) of maps / lists
                    else {
                        Class _c = ClassLoaderUtil.loadClass((String) entry.getValue(), this.getClass());
                        LOG.debug("\t{}:{} [treated as Class {}]", key, entry.getValue(), _c);
                        mapping.put(key, _c);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Problem loading properties for {}", clazz.getName(), ex);
        }
    }

}
