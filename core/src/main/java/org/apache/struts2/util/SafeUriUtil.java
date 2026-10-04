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

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Guards a value that is about to become the {@code href} of a rendered link against carrying an
 * executable URI scheme (for example {@code javascript:}), rather than a navigable one.
 * <p>
 * A relative, path-only, query-only or fragment-only value is always safe on its own: it cannot change
 * the scheme of the page the link is on. An absolute value naming an executable scheme is not: activating
 * such a link runs the scheme's payload immediately, in the page's own origin, with no further
 * interaction required beyond the click. This is checked with an allowlist of the schemes a rendered
 * link legitimately needs, rather than a list of the schemes known to be dangerous, since a blocklist
 * only ever covers the schemes its author thought of.
 *
 * @since 7.4.0
 */
public final class SafeUriUtil {

    /**
     * Schemes a rendered link may legitimately carry. Absent from this set: {@code javascript}, which
     * runs script in the page's own origin on activation; {@code vbscript}, the legacy Internet Explorer
     * equivalent; and {@code data}, which can navigate to an attacker-authored document.
     */
    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https", "mailto", "tel");

    /**
     * A URI scheme name — a leading letter, then letters/digits/{@code +}/{@code -}/{@code .} — followed
     * by its terminating colon, anchored to the start of the (already normalized) value.
     */
    private static final Pattern SCHEME = Pattern.compile("^([a-zA-Z][a-zA-Z0-9+.\\-]*):");

    private SafeUriUtil() {
    }

    /**
     * Returns {@code value} unchanged if it names no scheme, or names one in {@link #ALLOWED_SCHEMES};
     * otherwise returns {@code "#"}, an inert, self-referential fragment, in its place.
     * <p>
     * Before the scheme is read, {@code value} has every ASCII tab/newline/carriage-return removed and
     * any leading C0 control or space stripped — the two steps of the normalization a browser's own URL
     * parser applies before doing the same read (<a href="https://url.spec.whatwg.org/#url-parsing">WHATWG
     * URL &sect;4.4</a>) that can affect where a scheme starts or what it contains; the spec's matching
     * trailing-character strip is omitted here as it cannot move or alter a scheme at the start of the
     * string. Skipping this normalization would let a scheme split across those characters —
     * {@code java\tscript:alert(1)}, or a leading NUL before an otherwise-ordinary scheme — read as
     * schemeless here while a browser still recovers and runs it.
     *
     * @param value the candidate href value, or {@code null}
     * @return {@code value}, or {@code "#"} if it names a scheme outside {@link #ALLOWED_SCHEMES}; {@code null} if {@code value} is {@code null}
     */
    public static String rejectUnsafeScheme(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.replaceAll("[\\t\\n\\r]", "").replaceFirst("^[\\x00-\\x20]+", "");
        Matcher matcher = SCHEME.matcher(normalized);
        if (matcher.find() && !ALLOWED_SCHEMES.contains(matcher.group(1).toLowerCase(Locale.ROOT))) {
            return "#";
        }
        return value;
    }
}
