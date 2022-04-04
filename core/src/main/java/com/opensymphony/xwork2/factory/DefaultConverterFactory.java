package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * Default implementation
 */
public class DefaultConverterFactory implements ConverterFactory {

    private static final Logger LOG = LogManager.getLogger(DefaultConverterFactory.class);

    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, Map<String, Object> extraContext) throws Exception {
        LOG.debug("Creating converter of type [{}]", converterClass.getCanonicalName());
        return container.getInstance(converterClass);
    }

}
