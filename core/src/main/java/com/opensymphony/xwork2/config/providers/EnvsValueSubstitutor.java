package com.opensymphony.xwork2.config.providers;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvsValueSubstitutor implements ValueSubstitutor {

    private static final Logger LOG = LogManager.getLogger(EnvsValueSubstitutor.class);

    protected StrSubstitutor strSubstitutor;

    public EnvsValueSubstitutor() {
        strSubstitutor = new StrSubstitutor(System.getenv());
        strSubstitutor.setVariablePrefix("${ENV.");
        strSubstitutor.setVariableSuffix('}');
        strSubstitutor.setValueDelimiter(":");
    }

    @Override
    public String substitute(String value) {
        LOG.debug("Substituting value {} with proper ENV value", value);

        String substituted = StrSubstitutor.replaceSystemProperties(value);
        return strSubstitutor.replace(substituted);
    }
}
