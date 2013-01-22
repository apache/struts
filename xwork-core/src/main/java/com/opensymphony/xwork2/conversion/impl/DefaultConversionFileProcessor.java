package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of {@link ConversionFileProcessor}
 */
public class DefaultConversionFileProcessor implements ConversionFileProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConversionFileProcessor.class);

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
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Processing conversion file [#0] for class [#1]", converterFilename, clazz);
                }

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
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as String]");
                        }
                        mapping.put(key, entry.getValue());
                    }
                    //for properties of classes
                    else if (!(key.startsWith(DefaultObjectTypeDeterminer.ELEMENT_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX))
                            ) {
                        TypeConverter _typeConverter = converterCreator.createTypeConverter((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as TypeConverter " + _typeConverter + "]");
                        }
                        mapping.put(key, _typeConverter);
                    }
                    //for keys of Maps
                    else if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX)) {

                        Class converterClass = Thread.currentThread().getContextClassLoader().loadClass((String) entry.getValue());

                        //check if the converter is a type converter if it is one
                        //then just put it in the map as is. Otherwise
                        //put a value in for the type converter of the class
                        if (converterClass.isAssignableFrom(TypeConverter.class)) {
                            TypeConverter _typeConverter = converterCreator.createTypeConverter((String) entry.getValue());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as TypeConverter " + _typeConverter + "]");
                            }
                            mapping.put(key, _typeConverter);
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as Class " + converterClass + "]");
                            }
                            mapping.put(key, converterClass);
                        }
                    }
                    //elements(values) of maps / lists
                    else {
                        Class _c = Thread.currentThread().getContextClassLoader().loadClass((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as Class " + _c + "]");
                        }
                        mapping.put(key, _c);
                    }
                }
            }
        } catch (Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Problem loading properties for #0", ex, clazz.getName());
            }
        }
    }

}
