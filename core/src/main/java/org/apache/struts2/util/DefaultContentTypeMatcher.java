package org.apache.struts2.util;

import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.WildcardHelper;

import java.util.Map;

public class DefaultContentTypeMatcher implements ContentTypeMatcher<int[]> {

    private PatternMatcher<int[]> matcher = new WildcardHelper();

    public int[] compilePattern(String data) {
        return matcher.compilePattern(data);
    }

    public boolean match(Map<String, String> map, String data, int[] expr) {
        return matcher.match(map, data, expr);
    }

}
