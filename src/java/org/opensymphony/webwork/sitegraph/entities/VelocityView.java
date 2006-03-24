package com.opensymphony.webwork.sitegraph.entities;

import java.io.File;
import java.util.regex.Pattern;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 2:23:01 PM
 */
public class VelocityView extends FileBasedView {
    public VelocityView(File file) {
        super(file);
    }

    protected Pattern getActionPattern() {
        return Pattern.compile("#tag( Action [^)]*name=\"([^\"]+)\"[^)]*)");
    }

    protected Pattern getFormPattern() {
        return Pattern.compile("#tag \\(Form [^)]*action=\"([^\"]+)\"[^)]*\\)");
    }
}
