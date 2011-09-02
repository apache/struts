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
package com.opensymphony.xwork2.util.logging;

/**
 * Main logger interface for logging things
 */
public interface Logger {
    void trace(String msg, String... args);
    void trace(String msg, Throwable ex, String... args);
    boolean isTraceEnabled();
    
    void debug(String msg, String... args);
    void debug(String msg, Throwable ex, String... args);
    boolean isDebugEnabled();
    
    void info(String msg, String... args);
    void info(String msg, Throwable ex, String... args);
    boolean isInfoEnabled();
    
    void warn(String msg, String... args);
    void warn(String msg, Throwable ex, String... args);
    boolean isWarnEnabled();
    
    void error(String msg, String... args);
    void error(String msg, Throwable ex, String... args);
    boolean isErrorEnabled();
    
    void fatal(String msg, String... args);
    void fatal(String msg, Throwable ex, String... args);
    boolean isFatalEnabled();
}
