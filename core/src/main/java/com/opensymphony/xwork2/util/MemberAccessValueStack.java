package com.opensymphony.xwork2.util;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * ValueStacks implementing this interface provide a way to remove block or allow access
 * to properties using regular expressions
 */
public interface MemberAccessValueStack {

    void setExcludeProperties(Set<Pattern> excludeProperties);

    void setAcceptProperties(Set<Pattern> acceptedProperties);

}
