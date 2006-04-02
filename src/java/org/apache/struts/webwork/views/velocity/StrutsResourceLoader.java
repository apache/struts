/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.velocity;

import org.apache.struts.webwork.util.ClassLoaderUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;


/**
 * Loads resource from the Thread's context ClassLoader.
 *
 * @author $Author: plightbo $
 * @version $Revision: 1.4 $
 */
public class StrutsResourceLoader extends ClasspathResourceLoader {

    public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if ((name == null) || (name.length() == 0)) {
            throw new ResourceNotFoundException("No template name provided");
        }

        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        try {
            return ClassLoaderUtils.getResourceAsStream(name, StrutsResourceLoader.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
