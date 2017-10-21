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
package org.apache.struts2.views.java;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Default implementation of TagHandlerFactory 
 */
public class DefaultTagHandlerFactory implements TagHandlerFactory {
   private static final Logger LOG = LogManager.getLogger(DefaultTagHandlerFactory.class);
          
    private Class tagHandlerClass;

    public DefaultTagHandlerFactory(Class tagHandlerClass) {
        this.tagHandlerClass = tagHandlerClass;
    }

    public TagHandler create(TagHandler next) {
        try {
            TagHandler th = (TagHandler) tagHandlerClass.newInstance();
            th.setNext(next);
            return th;
        } catch (Exception e) {
            LOG.error("Failed to instantiate tag handler class [{}]", tagHandlerClass.getName(), e);
        }
        
        return null;
    }

}
