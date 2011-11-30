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
package com.opensymphony.xwork2.util.logging.commons;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerUtils;
import org.apache.commons.logging.Log;

/**
 * Simple logger that delegates to commons logging
 */
public class CommonsLogger implements Logger {
    
    private Log log;
    
    public CommonsLogger(Log log) {
        this.log = log;
    }

    public void error(String msg, String... args) {
        log.error(LoggerUtils.format(msg, args));
    }

    public void error(String msg, Throwable ex, String... args) {
        log.error(LoggerUtils.format(msg, args), ex);
    }

    public void info(String msg, String... args) {
        log.info(LoggerUtils.format(msg, args));
    }

    public void info(String msg, Throwable ex, String... args) {
        log.info(LoggerUtils.format(msg, args), ex);
    }

    

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void warn(String msg, String... args) {
        log.warn(LoggerUtils.format(msg, args));
    }

    public void warn(String msg, Throwable ex, String... args) {
        log.warn(LoggerUtils.format(msg, args), ex);
    }
    
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }
    
    public void debug(String msg, String... args) {
        log.debug(LoggerUtils.format(msg, args));
    }

    public void debug(String msg, Throwable ex, String... args) {
        log.debug(LoggerUtils.format(msg, args), ex);
    }
    
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }
    
    public void trace(String msg, String... args) {
        log.trace(LoggerUtils.format(msg, args));
    }

    public void trace(String msg, Throwable ex, String... args) {
        log.trace(LoggerUtils.format(msg, args), ex);
    }


    public void fatal(String msg, String... args) {
        log.fatal(LoggerUtils.format(msg, args));
    }

    public void fatal(String msg, Throwable ex, String... args) {
        log.fatal(LoggerUtils.format(msg, args), ex);
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return log.isFatalEnabled();
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

}
