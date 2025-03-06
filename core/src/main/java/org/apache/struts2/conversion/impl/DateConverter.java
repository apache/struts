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
package org.apache.struts2.conversion.impl;

import org.apache.struts2.ActionContext;
import org.apache.struts2.text.TextProvider;
import org.apache.struts2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.conversion.TypeConversionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DateConverter extends DefaultTypeConverter {

    private final static Logger LOG = LogManager.getLogger(DateConverter.class);

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName,
            Object value, Class toType) {
        Date result = null;

        if (value instanceof String sa && !sa.isEmpty()) {
            Locale locale = getLocale(context);

            DateFormat df = null;
            if (java.sql.Time.class == toType) {
                df = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
            } else if (java.sql.Timestamp.class == toType) {
                Date check;
                SimpleDateFormat dtfmt = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.MEDIUM, locale);
                SimpleDateFormat fullfmt = new SimpleDateFormat(dtfmt.toPattern() + MILLISECOND_FORMAT, locale);

                SimpleDateFormat dfmt = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);

                SimpleDateFormat[] fmts = { fullfmt, dtfmt, dfmt };
                for (SimpleDateFormat fmt : fmts) {
                    try {
                        check = fmt.parse(sa);
                        df = fmt;
                        if (check != null) {
                            break;
                        }
                    } catch (ParseException ignore) {
                    }
                }
            } else if (java.util.Date.class == toType) {
                Date check;
                DateFormat[] dfs = getDateFormats(ActionContext.of(context), locale);

                for (DateFormat df1 : dfs) {
                    try {
                        check = df1.parse(sa);
                        df = df1;
                        if (check != null) {
                            break;
                        }
                    } catch (ParseException ignore) {
                    }
                }
            } else if (java.time.LocalDateTime.class == toType || java.time.LocalDate.class == toType
                    || java.time.LocalTime.class == toType) {
                DateTimeFormatter dtf = null;
                TemporalAccessor check = null;
                DateTimeFormatter[] dfs = getDateTimeFormats(ActionContext.of(context), locale);

                for (DateTimeFormatter df1 : dfs) {
                    try {
                        check = df1.parseBest(sa, LocalDateTime::from, LocalDate::from, LocalTime::from);
                        dtf = df1;
                        break;
                    } catch (DateTimeParseException ignore) {
                    }
                }
                try {
                    if (dtf != null && check instanceof LocalDateTime) {
                        return LocalDateTime.parse(sa, dtf);
                    } else if (dtf != null && check instanceof LocalDate) {
                        return LocalDate.parse(sa, dtf);
                    } else if (dtf != null && check instanceof LocalTime) {
                        return LocalTime.parse(sa, dtf);
                    } else {
                        throw new TypeConversionException("Could not parse date");
                    }
                } catch (DateTimeParseException e) {
                    throw new TypeConversionException("Could not parse date", e);
                }

            } else if (OffsetDateTime.class == toType) {
                DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

                try {
                    return OffsetDateTime.parse(sa, dtf);
                } catch (DateTimeParseException e) {
                    throw new TypeConversionException("Could not parse OffsetDateTime", e);
                }
            }

            // final fallback for dates without time
            if (df == null) {
                df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
            }
            try {
                df.setLenient(false); // let's use strict parsing (XW-341)
                result = df.parse(sa);
                if (!(Date.class == toType)) {
                    try {
                        Constructor<?> constructor = toType.getConstructor(long.class);
                        return constructor.newInstance(result.getTime());
                    } catch (Exception e) {
                        throw new TypeConversionException(
                                "Couldn't create class " + toType + " using default (long) constructor", e);
                    }
                }
            } catch (ParseException e) {
                throw new TypeConversionException("Could not parse date", e);
            }

        } else if (Date.class.isAssignableFrom(value.getClass())) {
            result = (Date) value;
        }
        return result;
    }

    /**
     * The user defined global date format, see
     * {@link org.apache.struts2.components.Date#DATETAG_PROPERTY}
     *
     * @param context current ActionContext
     *
     * @return defined global date string format
     */
    protected String getGlobalDateString(ActionContext context) {
        final String dateTagProperty = org.apache.struts2.components.Date.DATETAG_PROPERTY;
        String globalDateString = null;

        final TextProvider tp = findProviderInStack(context.getValueStack());

        if (tp != null) {
            String globalFormat = tp.getText(dateTagProperty);
            // if tp.getText can not find the property then the returned string
            // is the same as input = DATETAG_PROPERTY
            if (globalFormat != null && !dateTagProperty.equals(globalFormat)) {
                LOG.debug("Found \"{}\" as \"{}\"", dateTagProperty, globalFormat);
                globalDateString = globalFormat;
            } else {
                LOG.debug("\"{}\" has not been defined, ignoring it", dateTagProperty);
            }
        }

        return globalDateString;
    }

    /**
     * Retrieves the list of date formats to be used when converting dates
     *
     * @param context the current ActionContext
     * @param locale  the current locale of the action
     * @return a list of DateFormat to be used for date conversion
     */
    private DateFormat[] getDateFormats(ActionContext context, Locale locale) {
        DateFormat globalDateFormat = null;
        String globalFormat = getGlobalDateString(context);
        if (globalFormat != null) {
            globalDateFormat = new SimpleDateFormat(globalFormat, locale);
        }

        DateFormat dt1 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale);
        DateFormat dt2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        DateFormat dt3 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);

        DateFormat d1 = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        DateFormat d2 = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        DateFormat d3 = DateFormat.getDateInstance(DateFormat.LONG, locale);

        DateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat rfc3339dateOnly = new SimpleDateFormat("yyyy-MM-dd");

        final DateFormat[] dateFormats;

        if (globalDateFormat == null) {
            dateFormats = new DateFormat[] { dt1, dt2, dt3, rfc3339, d1, d2, d3, rfc3339dateOnly };
        } else {
            dateFormats = new DateFormat[] { globalDateFormat, dt1, dt2, dt3, rfc3339, d1, d2, d3, rfc3339dateOnly };
        }

        return dateFormats;
    }

    /**
     * Retrieves the list of date time formats to be used when converting dates
     *
     * @param context the current ActionContext
     * @param locale  the current locale of the action
     *
     * @return a list of DateTimeFormatter to be used for date conversion
     */
    protected DateTimeFormatter[] getDateTimeFormats(ActionContext context, Locale locale) {

        DateTimeFormatter globalDateFormat = null;
        String globalFormat = getGlobalDateString(context);
        if (globalFormat != null) {
            globalDateFormat = DateTimeFormatter.ofPattern(globalFormat, locale);
        }

        DateTimeFormatter df1 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter df2 = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter df3 = DateTimeFormatter.ISO_LOCAL_TIME;

        final DateTimeFormatter[] dateFormats;

        if (globalDateFormat == null) {
            dateFormats = new DateTimeFormatter[] { df1, df2, df3 };
        } else {
            dateFormats = new DateTimeFormatter[] { globalDateFormat, df1, df2, df3 };
        }

        return dateFormats;
    }

    private TextProvider findProviderInStack(ValueStack stack) {
        // TODO: ValueStack will never be null, this is just a workaround for tests
        if (stack == null) {
            LOG.warn("ValueStack is null, won't be able to find TextProvider!");
            return null;
        }
        for (Object o : stack.getRoot()) {
            if (o instanceof TextProvider) {
                return (TextProvider) o;
            }
        }
        return null;
    }

}
