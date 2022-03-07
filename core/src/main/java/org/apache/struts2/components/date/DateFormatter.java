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
package org.apache.struts2.components.date;

import java.time.temporal.TemporalAccessor;

/**
 * Allows defines a wrapper around different formatting APIs, like old SimpleDateFormat
 * and new DateTimeFormatter introduced in Java 8 Date/Time API
 * <p>
 * New instance will be injected using {@link org.apache.struts2.StrutsConstants#STRUTS_DATE_FORMATTER}
 */
public interface DateFormatter {

    /**
     * Formats provided temporal with the given format
     *
     * @param temporal Java 8 {@link TemporalAccessor}
     * @param format   implementation specific format
     * @return a string representation of the formatted `temporal`
     */
    String format(TemporalAccessor temporal, String format);

}
