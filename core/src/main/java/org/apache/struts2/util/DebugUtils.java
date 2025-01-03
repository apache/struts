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
package org.apache.struts2.util;

import org.apache.struts2.text.TextProvider;
import org.apache.struts2.interceptor.ValidationAware;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 6.5.0
 */
public final class DebugUtils {

    private static final Set<String> IS_LOGGED = ConcurrentHashMap.newKeySet();

    public static void notifyDeveloperOfError(Logger log, Object action, String message) {
        if (action instanceof TextProvider tp) {
            message = tp.getText("devmode.notification", "Developer Notification:\n{0}", new String[]{message});
        }
        log.error(message);
        if (action instanceof ValidationAware validationAware) {
            validationAware.addActionMessage(message);
        }
    }

    /**
     * @since 7.0
     */
    public static void logWarningForFirstOccurrence(String key, Logger log, String msg, Object... args) {
        if (IS_LOGGED.add(key)) {
            log.warn(msg, args);
        }
    }
}
