package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * TODO lukaszlenart: add a comment
 */
public class DefaultConversionPropertiesProcessor implements ConversionPropertiesProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConversionPropertiesProcessor.class);

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

                if (LOG.isDebugEnabled()) {
                    LOG.debug("processing conversion file [" + propsName + "]");
                }

                for (Object o : props.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String key = (String) entry.getKey();

                    try {
                        TypeConverter _typeConverter = converterCreator.createTypeConverter((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + " [treated as TypeConverter " + _typeConverter + "]");
                        }
                        converterHolder.addDefaultMapping(key, _typeConverter);
                    } catch (Exception e) {
                        LOG.error("Conversion registration error", e);
                    }
                }
            }
        } catch (IOException ex) {
            if (require) {
                throw new XWorkException("Cannot load conversion properties file: "+propsName, ex);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cannot load conversion properties file: #0", ex, propsName);
                }
            }
        }
    }

}
