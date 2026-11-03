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

import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Formats message arguments with the {@link BigDecimalBounds} integer-digit bound applied, leaving every call that
 * carries no argument over the bound to {@link MessageFormat} untouched.
 *
 * @since 6.13.0
 */
public final class BoundedMessageFormat {

    private BoundedMessageFormat() {
    }

    public static String format(MessageFormat messageFormat, Object[] args) {
        if (args != null && Arrays.stream(args).anyMatch(BigDecimalBounds::exceedsMaxIntegerDigits)) {
            return bound(messageFormat, args).format(args);
        }
        return messageFormat.format(args);
    }

    private static MessageFormat bound(MessageFormat messageFormat, Object[] args) {
        MessageFormat bounded = (MessageFormat) messageFormat.clone();
        Format[] formats = bounded.getFormats();
        for (int i = 0; i < formats.length; i++) {
            bounded.setFormat(i, new ElementFormat(formats[i], bounded.getLocale(), args));
        }
        return bounded;
    }

    /**
     * Wraps one format element. {@link MessageFormat} re-parses a {@link ChoiceFormat} result containing {@code {}
     * only when the element's format is a {@link ChoiceFormat} itself, so that step is repeated here with the bound
     * applied to the nested pattern.
     */
    private static final class ElementFormat extends Format {

        private static final long serialVersionUID = 1L;

        private final transient Format original;
        private final Locale locale;
        private final transient Object[] args;

        private ElementFormat(Format original, Locale locale, Object[] args) {
            this.original = original;
            this.locale = locale;
            this.args = args;
        }

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            if (original instanceof ChoiceFormat) {
                String selected = original.format(obj);
                if (selected.indexOf('{') >= 0) {
                    return toAppendTo.append(BoundedMessageFormat.format(new MessageFormat(selected, locale), args));
                }
                return toAppendTo.append(selected);
            }
            if (BigDecimalBounds.exceedsMaxIntegerDigits(obj)) {
                return toAppendTo.append(obj);
            }
            if (original != null) {
                return original.format(obj, toAppendTo, pos);
            }
            return formatAsMessageFormatDefault(obj, toAppendTo);
        }

        private StringBuffer formatAsMessageFormatDefault(Object obj, StringBuffer toAppendTo) {
            if (obj instanceof Number) {
                return toAppendTo.append(NumberFormat.getInstance(locale).format(obj));
            }
            if (obj instanceof Date) {
                return toAppendTo.append(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale).format(obj));
            }
            return toAppendTo.append(obj);
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return original == null ? null : original.parseObject(source, pos);
        }
    }
}
