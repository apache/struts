/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.rest.handler.jackson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tallies the dynamic keys an any-setter rejected while a body was read. The key space of an
 * any-setter is the request body itself, so the per-key detail stays at DEBUG and a single WARN per
 * sink and reason is written when the request state is cleared.
 */
final class DynamicKeyRejections {

    private static final Logger LOG = LogManager.getLogger(DynamicKeyRejections.class);
    private static final ThreadLocal<Map<Entry, Integer>> TALLIES = new ThreadLocal<>();

    enum Reason {
        CONSENT_MISSING("dynamic keys require @StrutsParameter(allowDynamicKeys = true) on a method or field"),
        CREATOR_PARAMETER("dynamic-key consent can only be declared on an any-setter method or field"),
        DEPTH_EXCEEDED("value depth exceeds the @StrutsParameter depth",
                "value depth [{}] exceeds @StrutsParameter depth [{}]"),
        PROPERTY_NAME_UNAVAILABLE("dynamic property name is unavailable");

        private final String summary;
        private final String detail;

        Reason(String summary) {
            this(summary, summary);
        }

        Reason(String summary, String detail) {
            this.summary = summary;
            this.detail = detail;
        }
    }

    private DynamicKeyRejections() {
        // utility
    }

    static void tally(Reason reason, String sink, String path, Object... detailArguments) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("REST body any-setter parameter [{}] rejected by [{}]; {}", path, sink,
                    new ParameterizedMessage(reason.detail, detailArguments).getFormattedMessage());
        }
        Map<Entry, Integer> tallies = TALLIES.get();
        if (tallies == null) {
            tallies = new LinkedHashMap<>();
            TALLIES.set(tallies);
        }
        tallies.merge(new Entry(sink, reason), 1, Integer::sum);
    }

    static void reportAndClear() {
        Map<Entry, Integer> tallies = TALLIES.get();
        TALLIES.remove();
        if (tallies == null) {
            return;
        }
        tallies.forEach((entry, count) ->
                LOG.warn("REST body any-setter [{}] rejected [{}] dynamic key(s); {}",
                        entry.sink, count, entry.reason.summary));
    }

    private record Entry(String sink, Reason reason) {
    }
}
