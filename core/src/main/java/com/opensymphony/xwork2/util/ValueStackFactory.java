/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util;

/**
 * Factory that creates a value stack, defaulting to the OgnlValueStackFactory
 */
public interface ValueStackFactory {

    /**
     * Get a new instance of {@link com.opensymphony.xwork2.util.ValueStack}
     *
     * @return  a new {@link com.opensymphony.xwork2.util.ValueStack}.
     */
    ValueStack createValueStack();
    
    /**
     * Get a new instance of {@link com.opensymphony.xwork2.util.ValueStack}
     *
     * @param stack an existing stack to include.
     * @return  a new {@link com.opensymphony.xwork2.util.ValueStack}.
     */
    ValueStack createValueStack(ValueStack stack);
    
}
