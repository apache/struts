/*
 * $Id$
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.struts2.legacy;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Adds the ability to set a model, probably retrieved from a given state.
 */
public interface ScopedModelDriven<T> extends ModelDriven {

    /**
     * Sets the model
     */
    void setModel(T model);
}
