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

/**
 * Escapes the values rendered by the {@code property} and {@code text} tags.
 * <p>
 * The instance is selected with {@link org.apache.struts2.StrutsConstants#STRUTS_TAG_ESCAPER}; replacing the
 * default {@link StrutsTagEscaper} makes the application responsible for the safety of the rendered output.
 */
public interface TagEscaper {

    /**
     * @param config the escape options set on the tag and the tag they come from
     * @param value  the value to escape, may be {@code null}
     * @return the escaped value
     */
    String escape(EscapeConfig config, String value);
}
