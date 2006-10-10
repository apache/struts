/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.velocity;

import java.io.InputStream;

import org.apache.struts2.util.ClassLoaderUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;


/**
 * Loads resource from the Thread's context ClassLoader.
 *
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
