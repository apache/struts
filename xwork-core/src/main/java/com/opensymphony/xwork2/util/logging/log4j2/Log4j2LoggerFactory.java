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
package com.opensymphony.xwork2.util.logging.log4j2;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Creates log4j2-logging-backed loggers
 *
 * You can use the same to explicit tell the framework which implementation to use and don't depend on class discovery:
 * <pre>
 *   -Dxwork.loggerFactory=com.opensymphony.xwork2.util.logging.log4j2.Log4j2LoggerFactory
 * </pre>
 */
public class Log4j2LoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new Log4j2Logger(org.apache.logging.log4j.LogManager.getLogger(cls));
    }

    @Override
    protected Logger getLoggerImpl(String name) {
        return new Log4j2Logger(org.apache.logging.log4j.LogManager.getLogger(name));
    }

}
