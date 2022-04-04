package org.apache.struts2.util;

import java.util.Map;

/**
 * Matches content type of uploaded files, similar to {@link com.opensymphony.xwork2.util.PatternMatcher}
 *
 * @since 2.3.22
 */
public interface ContentTypeMatcher<E extends Object> {

    E compilePattern(String data);

    boolean match(Map<String,String> map, String data, E expr);

}
