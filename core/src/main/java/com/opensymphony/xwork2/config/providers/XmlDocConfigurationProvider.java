/*
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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class XmlDocConfigurationProvider implements ConfigurationProvider {

    private static final Logger LOG = LogManager.getLogger(XmlConfigurationProvider.class);

    protected final Map<String, Element> declaredPackages = new HashMap<>();

    protected List<Document> documents;
    protected ObjectFactory objectFactory;

    protected Map<String, String> dtdMappings = new HashMap<>();
    protected Configuration configuration;

    protected boolean throwExceptionOnDuplicateBeans = true;

    protected ValueSubstitutor valueSubstitutor;

    public XmlDocConfigurationProvider(Document... documents) {
        this.documents = Arrays.asList(documents);
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject(required = false)
    public void setValueSubstitutor(ValueSubstitutor valueSubstitutor) {
        this.valueSubstitutor = valueSubstitutor;
    }

    public void setThrowExceptionOnDuplicateBeans(boolean val) {
        this.throwExceptionOnDuplicateBeans = val;
    }

    public void setDtdMappings(Map<String, String> mappings) {
        this.dtdMappings = Collections.unmodifiableMap(mappings);
    }

    /**
     * Returns an unmodifiable map of DTD mappings
     *
     * @return map of DTD mappings
     */
    public Map<String, String> getDtdMappings() {
        return dtdMappings;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void destroy() {
    }
}
