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
package org.apache.struts2.components;

import org.apache.struts2.validator.Validator;

import java.util.List;
import java.util.Map;

/**
 * Maps a field's validators onto the HTML attributes a theme should render for it.
 * <p>
 * The default implementation is deliberately conservative — see {@link StrutsHtmlConstraintProvider}.
 * Applications wanting a best-effort mapping (an {@code email} validator becoming
 * {@code type="email"}, say) should register their own implementation instead.
 *
 * @since 7.4.0
 */
public interface HtmlConstraintProvider {

    /**
     * @param validators the field's validators; may be null or empty
     * @param control    the kind of control being rendered
     * @param action     the action instance, used to resolve i18n validator messages; may be null
     * @return attribute name to value; never null, possibly empty
     */
    Map<String, String> constraintsFor(List<Validator> validators, HtmlControlType control, Object action);
}
