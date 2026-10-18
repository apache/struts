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
package org.apache.struts2.action;

/**
 * How a {@link BackgroundSessionConflictAware} action reconciles one conflicting session key.
 */
public final class SessionConflictResolution {

    private static final SessionConflictResolution KEEP_CURRENT = new SessionConflictResolution(Kind.KEEP_CURRENT, null);
    private static final SessionConflictResolution USE_BACKGROUND = new SessionConflictResolution(Kind.USE_BACKGROUND, null);
    private static final SessionConflictResolution REMOVE = new SessionConflictResolution(Kind.REPLACE, null);

    private enum Kind { KEEP_CURRENT, USE_BACKGROUND, REPLACE }

    private final Kind kind;
    private final Object replacement;

    private SessionConflictResolution(Kind kind, Object replacement) {
        this.kind = kind;
        this.replacement = replacement;
    }

    /**
     * @return a resolution keeping the value the session holds now.
     */
    public static SessionConflictResolution keepCurrent() {
        return KEEP_CURRENT;
    }

    /**
     * @return a resolution taking the background process's value, removing the key if it removed it.
     */
    public static SessionConflictResolution useBackground() {
        return USE_BACKGROUND;
    }

    /**
     * @param value the value to store, {@code null} removes the key.
     * @return a resolution storing the given value.
     */
    public static SessionConflictResolution replaceWith(Object value) {
        return new SessionConflictResolution(Kind.REPLACE, value);
    }

    /**
     * @return a resolution removing the key.
     */
    public static SessionConflictResolution remove() {
        return REMOVE;
    }

    /**
     * @param conflict the conflict this resolution answers.
     * @return the value the key holds after reconciliation, {@code null} meaning removed.
     */
    public Object resolve(SessionConflict conflict) {
        return switch (kind) {
            case KEEP_CURRENT -> conflict.currentValue();
            case USE_BACKGROUND -> conflict.backgroundValue();
            case REPLACE -> replacement;
        };
    }
}
