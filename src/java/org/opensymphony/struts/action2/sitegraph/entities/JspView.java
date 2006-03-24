package com.opensymphony.webwork.sitegraph.entities;

import java.io.File;
import java.util.regex.Pattern;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 2:19:15 PM
 */
public class JspView extends FileBasedView {
    public JspView(File file) {
        super(file);
    }

    protected Pattern getActionPattern() {
        return Pattern.compile("<ww:action [^>]*name=\"([^\"]+)\"[^>]*>");
    }

    protected Pattern getFormPattern() {
        return Pattern.compile("<ww:form [^>]*action=\"([^\"]+)\"[^>]*>");
    }
}
