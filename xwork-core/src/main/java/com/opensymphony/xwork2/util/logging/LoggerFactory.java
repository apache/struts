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

import com.opensymphony.xwork2.util.logging.jdk.JdkLoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Creates loggers.  Static accessor will lazily try to decide on the best factory if none specified.
 */
public abstract class LoggerFactory {
    
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static LoggerFactory factory;
    
    public static void setLoggerFactory(LoggerFactory factory) {
        lock.writeLock().lock();
        try {
            LoggerFactory.factory = factory;
        } finally {
            lock.writeLock().unlock();
        }
            
    }
    
    public static Logger getLogger(Class<?> cls) {
        return getLoggerFactory().getLoggerImpl(cls);
    }
    
    public static Logger getLogger(String name) {
        return getLoggerFactory().getLoggerImpl(name);
    }
    
    protected static LoggerFactory getLoggerFactory() {
        lock.readLock().lock();
        try {
            if (factory != null) {
                return factory;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (factory == null) {
                try {
                    Class.forName("org.apache.commons.logging.LogFactory");
                    factory = new com.opensymphony.xwork2.util.logging.commons.CommonsLoggerFactory();
                } catch (ClassNotFoundException ex) {
                    // commons logging not found, falling back to jdk logging
                    factory = new JdkLoggerFactory();
                }
            }
            return factory;
        }
        finally {
            lock.writeLock().unlock();
        }
    }
    
    protected abstract Logger getLoggerImpl(Class<?> cls);
    
    protected abstract Logger getLoggerImpl(String name); 

}
