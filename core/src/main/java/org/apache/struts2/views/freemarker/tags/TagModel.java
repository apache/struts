/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.views.freemarker.tags;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;

public abstract class TagModel implements TemplateTransformModel {
    private static final Log LOG = LogFactory.getLog(TagModel.class);

    protected ValueStack stack;
    protected HttpServletRequest req;
    protected HttpServletResponse res;

    public TagModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    public Writer getWriter(Writer writer, Map params) throws TemplateModelException, IOException {
        Component bean = getBean();
        Map basicParams = convertParams(params);
        bean.copyParams(basicParams);
        bean.addAllParameters(getComplexParams(params));
        return new CallbackWriter(bean, writer);
    }

    protected abstract Component getBean();

    private Map convertParams(Map params) {
        HashMap map = new HashMap(params.size());
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object value = entry.getValue();
            if (value != null && !complexType(value)) {
                map.put(entry.getKey(), value.toString());
            }
        }
        return map;
    }

    private Map getComplexParams(Map params) {
        HashMap map = new HashMap(params.size());
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object value = entry.getValue();
            if (value != null && complexType(value)) {
                if (value instanceof freemarker.ext.beans.BeanModel) {
                    map.put(entry.getKey(), ((freemarker.ext.beans.BeanModel) value).getWrappedObject());
                } else if (value instanceof SimpleNumber) {
                    map.put(entry.getKey(), ((SimpleNumber) value).getAsNumber());
                } else if (value instanceof SimpleSequence) {
                    try {
                        map.put(entry.getKey(), ((SimpleSequence) value).toList());
                    } catch (TemplateModelException e) {
                        LOG.error("There was a problem converting a SimpleSequence to a list", e);
                    }
                }
            }
        }
        return map;
    }

    private boolean complexType(Object value) {
        return value instanceof freemarker.ext.beans.BeanModel
                || value instanceof SimpleNumber
                || value instanceof SimpleSequence;
    }
}
