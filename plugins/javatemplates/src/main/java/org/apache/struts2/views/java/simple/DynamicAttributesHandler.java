package org.apache.struts2.views.java.simple;

import org.apache.struts2.views.java.Attributes;

import java.io.IOException;
import java.util.Map;

/**
 * Adds dynamic attributes
 */
public class DynamicAttributesHandler extends AbstractTagHandler {

    /* (non-Javadoc)
     * @see org.apache.struts2.views.java.simple.AbstractTagHandler#start(java.lang.String, org.apache.struts2.views.java.Attributes)
     */
    @Override
    public void start(String name, Attributes a) throws IOException {
        a.putAll((Map<String, String>) context.getParameters().get("dynamicAttributes"));
        super.start(name, a);
    }

}
