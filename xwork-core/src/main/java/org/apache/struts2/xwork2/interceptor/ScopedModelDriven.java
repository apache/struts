/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Adds the ability to set a model, probably retrieved from a given state.
 */
public interface ScopedModelDriven<T> extends ModelDriven<T> {

    /**
     * Sets the model
     */
    void setModel(T model);
    
    /**
     * Sets the key under which the model is stored
     * @param key The model key
     */
    void setScopeKey(String key);
    
    /**
     * Gets the key under which the model is stored
     */
    String getScopeKey();
}
