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
package org.apache.struts2.test;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Collects what a class logs while open, as the formatted message followed by the exception, if any. Only levels
 * enabled by the test log configuration are seen.
 */
public class LogCapture implements AutoCloseable {

    private final Logger logger;
    private final AbstractAppender appender;
    private final List<Entry> entries = new CopyOnWriteArrayList<>();

    private record Entry(Level level, String message) {
    }

    public LogCapture(Class<?> source) {
        logger = (Logger) LogManager.getLogger(source);
        appender = new AbstractAppender("LogCapture-" + System.nanoTime(), null, null, false, Property.EMPTY_ARRAY) {
            @Override
            public void append(LogEvent event) {
                String message = event.getMessage().getFormattedMessage();
                entries.add(new Entry(event.getLevel(), event.getThrown() == null ? message : message + " " + event.getThrown()));
            }
        };
        appender.start();
        logger.addAppender(appender);
    }

    public List<String> messages() {
        return entries.stream().map(Entry::message).toList();
    }

    public List<String> messages(Level level) {
        return entries.stream().filter(entry -> entry.level() == level).map(Entry::message).toList();
    }

    @Override
    public void close() {
        logger.removeAppender(appender);
        appender.stop();
    }
}
