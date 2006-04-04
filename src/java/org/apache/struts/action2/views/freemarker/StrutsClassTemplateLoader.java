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
package org.apache.struts.action2.views.freemarker;

import com.opensymphony.util.ClassLoaderUtil;
import freemarker.cache.URLTemplateLoader;

import java.net.URL;

/**
 * User: plightbo
 * Date: Aug 10, 2005
 * Time: 11:25:05 PM
 */
public class StrutsClassTemplateLoader extends URLTemplateLoader {
    protected URL getURL(String name) {
        return ClassLoaderUtil.getResource(name, getClass());
    }
}
