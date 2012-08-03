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
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.XWorkException;


/**
 * ConfigurationException
 *
 * @author Jason Carreira
 */
public class ConfigurationException extends XWorkException {

    /**
     * Constructs a <code>ConfigurationException</code> with no detail message.
     */
    public ConfigurationException() {
    }

    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s) {
        super(s);
    }
    
    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s, Object target) {
        super(s, target);
    }

    /**
     * Constructs a <code>ConfigurationException</code> with no detail message.
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a <code>ConfigurationException</code> with no detail message.
     */
    public ConfigurationException(Throwable cause, Object target) {
        super(cause, target);
    }

    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s, Throwable cause) {
        super(s, cause);
    }
    
    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s, Throwable cause, Object target) {
        super(s, cause, target);
    }
}
