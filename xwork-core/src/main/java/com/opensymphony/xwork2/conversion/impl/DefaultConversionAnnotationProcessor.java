package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Map;

/**
 * Default implementation of {@link ConversionAnnotationProcessor}
 */
public class DefaultConversionAnnotationProcessor implements ConversionAnnotationProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConversionAnnotationProcessor.class);

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

    public void process(Map<String, Object> mapping, TypeConversion tc, String key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("TypeConversion [#0] with key: [#1]", tc.converter(), key);
        }
        if (key == null) {
            return;
        }
        try {
            if (tc.type() == ConversionType.APPLICATION) {
                converterHolder.addDefaultMapping(key, converterCreator.createTypeConverter(tc.converter()));
            } else {
                if (tc.rule() == ConversionRule.KEY_PROPERTY || tc.rule() == ConversionRule.CREATE_IF_NULL) {
                    mapping.put(key, tc.value());
                }
                //for properties of classes
                else if (tc.rule() != ConversionRule.ELEMENT || tc.rule() == ConversionRule.KEY || tc.rule() == ConversionRule.COLLECTION) {
                    mapping.put(key, converterCreator.createTypeConverter(tc.converter()));
                }
                //for keys of Maps
                else if (tc.rule() == ConversionRule.KEY) {
                    Class converterClass = Thread.currentThread().getContextClassLoader().loadClass(tc.converter());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Converter class: [#0]", converterClass);
                    }
                    //check if the converter is a type converter if it is one
                    //then just put it in the map as is. Otherwise
                    //put a value in for the type converter of the class
                    if (converterClass.isAssignableFrom(TypeConverter.class)) {
                        mapping.put(key, converterCreator.createTypeConverter(tc.converter()));
                    } else {
                        mapping.put(key, converterClass);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Object placed in mapping for key [#0] is [#1]", key, mapping.get(key));
                        }
                    }
                }
                //elements(values) of maps / lists
                else {
                    mapping.put(key, Thread.currentThread().getContextClassLoader().loadClass(tc.converter()));
                }
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Got exception for #0", e, key);
            }
        }
    }

}
