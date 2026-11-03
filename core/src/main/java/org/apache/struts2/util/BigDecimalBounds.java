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

import java.math.BigDecimal;

/**
 * Bounds the number of integer digits emitted when a {@link BigDecimal} is formatted, so the length of the output
 * follows the precision of the value rather than its scale.
 *
 * @since 7.5.0
 */
public final class BigDecimalBounds {

    /**
     * Covers every {@code double} value in full - the widest is {@link Double#MAX_VALUE} at 309 integer digits.
     */
    public static final int MAX_INTEGER_DIGITS = 340;

    private BigDecimalBounds() {
    }

    /**
     * @return {@code true} when the value is a {@link BigDecimal} that would format to more than
     * {@link #MAX_INTEGER_DIGITS} integer digits and should be rendered with {@link BigDecimal#toString()} instead
     */
    public static boolean exceedsMaxIntegerDigits(Object value) {
        // long: precision - scale overflows int at the edge of the scale range
        return value instanceof BigDecimal decimal && (long) decimal.precision() - decimal.scale() > MAX_INTEGER_DIGITS;
    }
}
