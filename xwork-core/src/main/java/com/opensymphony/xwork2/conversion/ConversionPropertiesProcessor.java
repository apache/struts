package com.opensymphony.xwork2.conversion;

/**
 * Used to read converters from Properties file
 */
public interface ConversionPropertiesProcessor {

    /**
     * Process given property to load converters as not required (Properties file doesn't have to exist)
     *
     * @param propsName Properties file name
     */
    void process(String propsName);

    /**
     * Process given property to load converters as required (Properties file must exist)
     *
     * @param propsName Properties file name
     */
    void processRequired(String propsName);

}
