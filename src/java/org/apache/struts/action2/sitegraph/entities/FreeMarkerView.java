package org.apache.struts.action2.sitegraph.entities;

import java.io.File;
import java.util.regex.Pattern;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 2:22:22 PM
 */
public class FreeMarkerView extends FileBasedView {
    public FreeMarkerView(File file) {
        super(file);
    }

    protected Pattern getActionPattern() {
        return Pattern.compile("<\\@ww.action [^>]*name=\"([^\"]+)\"[^>]*>");
    }

    protected Pattern getFormPattern() {
        return Pattern.compile("<\\@ww.form [^>]*action=\"([^\"]+)\"[^>]*>");
    }
}
