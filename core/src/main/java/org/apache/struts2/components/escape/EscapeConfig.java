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
package org.apache.struts2.components.escape;

import java.util.Objects;

/**
 * Immutable escape options of a single tag render, passed to {@link TagEscaper}.
 */
public final class EscapeConfig {

    private final TagOrigin origin;
    private final boolean html;
    private final boolean javaScript;
    private final boolean xml;
    private final boolean csv;

    private EscapeConfig(Builder builder) {
        this.origin = builder.origin;
        this.html = builder.html;
        this.javaScript = builder.javaScript;
        this.xml = builder.xml;
        this.csv = builder.csv;
    }

    public static Builder builder(TagOrigin origin) {
        return new Builder(origin);
    }

    public TagOrigin origin() {
        return origin;
    }

    public boolean html() {
        return html;
    }

    public boolean javaScript() {
        return javaScript;
    }

    public boolean xml() {
        return xml;
    }

    public boolean csv() {
        return csv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EscapeConfig that)) {
            return false;
        }
        return origin == that.origin && html == that.html && javaScript == that.javaScript
                && xml == that.xml && csv == that.csv;
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, html, javaScript, xml, csv);
    }

    @Override
    public String toString() {
        return "EscapeConfig{origin=" + origin + ", html=" + html + ", javaScript=" + javaScript
                + ", xml=" + xml + ", csv=" + csv + '}';
    }

    public static final class Builder {

        private final TagOrigin origin;
        private boolean html;
        private boolean javaScript;
        private boolean xml;
        private boolean csv;

        private Builder(TagOrigin origin) {
            this.origin = Objects.requireNonNull(origin, "origin");
        }

        public Builder html(boolean html) {
            this.html = html;
            return this;
        }

        public Builder javaScript(boolean javaScript) {
            this.javaScript = javaScript;
            return this;
        }

        public Builder xml(boolean xml) {
            this.xml = xml;
            return this;
        }

        public Builder csv(boolean csv) {
            this.csv = csv;
            return this;
        }

        public EscapeConfig build() {
            return new EscapeConfig(this);
        }
    }
}
