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

import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * TemplateHashModel wrapper for a HttpServletRequest attributes.
 */
public final class HttpRequestHashModel implements TemplateHashModelEx {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ObjectWrapper wrapper;

    /**
     * @param wrapper
     *            Should be an {@link ObjectWrapperAndUnwrapper}, or else some features might won't work properly. (It's
     *            declared as {@link ObjectWrapper} only for backward compatibility.)
     */
    public HttpRequestHashModel(
        HttpServletRequest request, ObjectWrapper wrapper) {
        this(request, null, wrapper);
    }

    public HttpRequestHashModel(
        HttpServletRequest request, HttpServletResponse response, 
        ObjectWrapper wrapper) {
        this.request = request;
        this.response = response;
        this.wrapper = wrapper;
    }
    
    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        return wrapper.wrap(request.getAttribute(key));
    }

    @Override
    public boolean isEmpty() {
        return !request.getAttributeNames().hasMoreElements();
    }
    
    @Override
    public int size() {
        int result = 0;
        for (Enumeration enumeration = request.getAttributeNames(); enumeration.hasMoreElements(); ) {
            enumeration.nextElement();
            ++result;
        }
        return result;
    }
    
    @Override
    public TemplateCollectionModel keys() {
        ArrayList keys = new ArrayList();
        for (Enumeration enumeration = request.getAttributeNames(); enumeration.hasMoreElements(); ) {
            keys.add(enumeration.nextElement());
        }
        return new SimpleCollection(keys.iterator());
    }
    
    @Override
    public TemplateCollectionModel values() {
        ArrayList values = new ArrayList();
        for (Enumeration enumeration = request.getAttributeNames(); enumeration.hasMoreElements(); ) {
            values.add(request.getAttribute((String) enumeration.nextElement()));
        }
        return new SimpleCollection(values.iterator(), wrapper);
    }

    public HttpServletRequest getRequest() {
        return request;
    }
    
    public HttpServletResponse getResponse() {
        return response;
    }
    
    public ObjectWrapper getObjectWrapper() {
        return wrapper;
    }
}
