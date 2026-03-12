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
package org.apache.struts2.json;

import org.apache.struts2.json.annotations.JSON;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class TemporalBean {

    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private LocalTime localTime;
    private ZonedDateTime zonedDateTime;
    private OffsetDateTime offsetDateTime;
    private Instant instant;
    private Calendar calendar;
    private LocalDate customFormatDate;

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @JSON(format = "dd/MM/yyyy")
    public LocalDate getCustomFormatDate() {
        return customFormatDate;
    }

    @JSON(format = "dd/MM/yyyy")
    public void setCustomFormatDate(LocalDate customFormatDate) {
        this.customFormatDate = customFormatDate;
    }

    private LocalDateTime customFormatDateTime;

    @JSON(format = "dd/MM/yyyy HH:mm")
    public LocalDateTime getCustomFormatDateTime() {
        return customFormatDateTime;
    }

    @JSON(format = "dd/MM/yyyy HH:mm")
    public void setCustomFormatDateTime(LocalDateTime customFormatDateTime) {
        this.customFormatDateTime = customFormatDateTime;
    }

    private Instant customFormatInstant;

    @JSON(format = "yyyy-MM-dd HH:mm:ss")
    public Instant getCustomFormatInstant() {
        return customFormatInstant;
    }

    @JSON(format = "yyyy-MM-dd HH:mm:ss")
    public void setCustomFormatInstant(Instant customFormatInstant) {
        this.customFormatInstant = customFormatInstant;
    }

    private OffsetDateTime customFormatOffsetDateTime;

    @JSON(format = "dd/MM/yyyy HH:mm:ssXXX")
    public OffsetDateTime getCustomFormatOffsetDateTime() {
        return customFormatOffsetDateTime;
    }

    @JSON(format = "dd/MM/yyyy HH:mm:ssXXX")
    public void setCustomFormatOffsetDateTime(OffsetDateTime customFormatOffsetDateTime) {
        this.customFormatOffsetDateTime = customFormatOffsetDateTime;
    }
}
