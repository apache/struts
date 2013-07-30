package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Map;

/**
 * Default implementation
 */
public class DefaultConverterFactory implements ConverterFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConverterFactory.class);

    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, Map<String, Object> extraContext) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating converter of type [#0]", converterClass.getCanonicalName());
        }
        return container.getInstance(converterClass);
    }

}
