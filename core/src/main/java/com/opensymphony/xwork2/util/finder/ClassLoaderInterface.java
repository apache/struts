/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util.finder;

import java.net.URL;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classes implementing this interface can find resources and load classes, usually delegating to a class
 * loader 
 */
public interface ClassLoaderInterface {

    //key used to add the current ClassLoaderInterface to ActionContext
    public final String CLASS_LOADER_INTERFACE = "__current_class_loader_interface";

    Class<?> loadClass(String name) throws ClassNotFoundException;

    URL getResource(String name);

    public Enumeration<URL> getResources(String name) throws IOException;

    public InputStream getResourceAsStream(String name) throws IOException;

    ClassLoaderInterface getParent();
}
