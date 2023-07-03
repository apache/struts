/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.freemarker.ext.servlet;

import freemarker.template.SimpleCollection;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * TemplateHashModel wrapper for a HttpServletRequest parameters.
 */

public class HttpRequestParametersHashModel
    implements
    TemplateHashModelEx {
    private final HttpServletRequest request;
    private List keys;
        
    public HttpRequestParametersHashModel(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public TemplateModel get(String key) {
        String value = request.getParameter(key);
        return value == null ? null : new SimpleScalar(value);
    }

    @Override
    public boolean isEmpty() {
        return !request.getParameterNames().hasMoreElements();
    }
    
    @Override
    public int size() {
        return getKeys().size();
    }
    
    @Override
    public TemplateCollectionModel keys() {
        return new SimpleCollection(getKeys().iterator());
    }
    
    @Override
    public TemplateCollectionModel values() {
        final Iterator iter = getKeys().iterator();
        return new SimpleCollection(
            new Iterator() {
                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }
                @Override
                public Object next() {
                    return request.getParameter((String) iter.next()); 
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            });
    }

    protected String transcode(String string) {
        return string;
    }

    private synchronized List getKeys() {
        if (keys == null) {
            keys = new ArrayList();
            for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements(); ) {
                keys.add(enumeration.nextElement());
            }
        }
        return keys;
    }
}
