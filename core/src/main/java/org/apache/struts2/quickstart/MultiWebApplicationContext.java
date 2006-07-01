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
package org.apache.struts2.quickstart;

import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.Resource;
import org.mortbay.util.JarResource;
import org.mortbay.util.FileResource;

import java.io.IOException;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.List;

/**
 */
public class MultiWebApplicationContext extends WebApplicationContext {
	
	private static final long serialVersionUID = -4732081314967099271L;
	
	private List pathPriority;
    private Map paths;
    private Class resolver;

    public MultiWebApplicationContext() {
    }

    public MultiWebApplicationContext(List pathPriority, Map paths) {
        super(getFirstRoot(paths));
        this.pathPriority = pathPriority;
        this.paths = paths;
    }

    public MultiWebApplicationContext(List pathPriority, Map paths, String resolver) {
        super(getFirstRoot(paths));
        this.pathPriority = pathPriority;
        this.paths = paths;
        try {
            this.resolver = loadClass(resolver, getClass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getFirstRoot(Map paths) {
        return (String) ((List) paths.get("/")).get(0);
    }

    public Resource getResource(String uriInContext) throws IOException {
        if (uriInContext.startsWith("/WEB-INF/lib/")) {
            String jar = uriInContext.substring("/WEB-INF/lib/".length());
            ClassLoader parent = Thread.currentThread().getContextClassLoader();
            while (parent != null) {
                if (parent instanceof URLClassLoader) {
                    URL[] urls = ((URLClassLoader) parent).getURLs();
                    for (int i = 0; i < urls.length; i++) {
                        URL url = urls[i];
                        if (url.toExternalForm().endsWith(jar)) {
                            return JarResource.newResource(url);
                        }
                    }
                }

                parent = parent.getParent();
            }
        }

        // still haven't found what we're looking for?
        // Alright, let's just hack this to work in IDEA
        if (uriInContext.equals("/struts-action")) {
            // we do this check to support both "quickstart:showcase" and "quickstart" (using quickstart.xml)
            if (new File("../../core/src/main/resources/META-INF/taglib.tld").exists()) {
                return FileResource.newResource("../../core/src/main/resources/META-INF/taglib.tld");
            } else {
                return FileResource.newResource("src/main/resources/META-INF/taglib.tld");
            }
        }

        MultiDirResource resource = newResolver(uriInContext);
        if (resource.exists()) {
            return resource;
        }

        String aliasedUri= getResourceAlias(uriInContext);
        if (aliasedUri != null) {
            return super.getResource(aliasedUri);
        }

        return resource;
    }

    public MultiDirResource newResolver(String uriInContext) {
        if (resolver == null) {
            return new MultiDirResource(this, uriInContext, pathPriority, paths);
        } else {
            try {
                Constructor c = resolver.getDeclaredConstructor(new Class[]{
                        MultiWebApplicationContext.class,
                        String.class,
                        List.class,
                        Map.class,
                });
                return (MultiDirResource) c.newInstance(new Object[] {
                        this,
                        uriInContext,
                        pathPriority,
                        paths,
                });
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Resource getBaseResource() {
        return newResolver("");
    }

    public static Class loadClass(String className, Class callingClass)
            throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            }
            catch (ClassNotFoundException ex) {
                try {
                    return MultiWebApplicationContext.class.getClassLoader().loadClass(className);
                }
                catch (ClassNotFoundException exc) {
                    return callingClass.getClassLoader().loadClass(className);
                }

            }
        }
    }
}
