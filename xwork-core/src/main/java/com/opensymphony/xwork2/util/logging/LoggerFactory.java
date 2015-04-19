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

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.util.logging.commons.CommonsLoggerFactory;
import com.opensymphony.xwork2.util.logging.jdk.JdkLoggerFactory;
import com.opensymphony.xwork2.util.logging.log4j2.Log4j2LoggerFactory;
import com.opensymphony.xwork2.util.logging.slf4j.Slf4jLoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Creates loggers.  Static accessor will lazily try to decide on the best factory if none specified.
 */
public abstract class LoggerFactory {

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static LoggerFactory factory;

    private static final List<LoggerClass> loggers = new LinkedList<LoggerClass>(){
        {
            add(new LoggerClass<CommonsLoggerFactory>("org.apache.commons.logging.LogFactory", CommonsLoggerFactory.class));
            add(new LoggerClass<Slf4jLoggerFactory>("org.slf4j.LoggerFactory", Slf4jLoggerFactory.class));
            add(new LoggerClass<Log4j2LoggerFactory>("org.apache.logging.log4j.LogManager", Log4j2LoggerFactory.class));
        }
    };

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
                createLoggerFactory();
            }
            return factory;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static void createLoggerFactory() {
        String userLoggerFactory = System.getProperty(XWorkConstants.XWORK_LOGGER_FACTORY);
        if (userLoggerFactory != null) {
            try {
                Class clazz = Class.forName(userLoggerFactory);
                factory = (LoggerFactory) clazz.newInstance();
            } catch (Exception e) {
                throw new XWorkException("System property [" + XWorkConstants.XWORK_LOGGER_FACTORY +
                        "] was defined as [" + userLoggerFactory + "] but there is a problem to use that LoggerFactory!", e);
            }
        } else {
            factory = new JdkLoggerFactory();
            for (LoggerClass logger : loggers) {
                if (logger.isSupported()) {
                    factory = logger.createInstance();
                    break;
                }
            }
        }
    }

    protected abstract Logger getLoggerImpl(Class<?> cls);

    protected abstract Logger getLoggerImpl(String name);

    private static class LoggerClass<T extends LoggerFactory> {

        private final String loggerClazzName;
        private final Class<T> loggerImplClazz;

        public LoggerClass(String loggerClazzName, Class<T> loggerImplClazz) {
            this.loggerClazzName = loggerClazzName;
            this.loggerImplClazz = loggerImplClazz;
        }

        public boolean isSupported() {
            try {
                Class.forName(loggerClazzName);
                return true;
            } catch (ClassNotFoundException ignore) {
                return false;
            }
        }

        public LoggerFactory createInstance() {
            try {
                return loggerImplClazz.newInstance();
            } catch (Exception e) {
                throw new XWorkException(e);
            }
        }
    }

}
