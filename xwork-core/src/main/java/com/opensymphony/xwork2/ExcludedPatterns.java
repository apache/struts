package com.opensymphony.xwork2;

/**
 * ExcludedPatterns contains hard-coded patterns that must be rejected by {@link com.opensymphony.xwork2.interceptor.ParametersInterceptor}
 * and partially in CookInterceptor
 */
public class ExcludedPatterns {

    public static final String CLASS_ACCESS_PATTERN = "(.*\\.|^|.*|\\[('|\"))class(\\.|('|\")]|\\[).*";

    public static final String[] EXCLUDED_PATTERNS = {
            CLASS_ACCESS_PATTERN,
            "^dojo\\..*",
            "^struts\\..*",
            "^session\\..*",
            "^request\\..*",
            "^application\\..*",
            "^servlet(Request|Response)\\..*",
            "^parameters\\..*"
    };

}
