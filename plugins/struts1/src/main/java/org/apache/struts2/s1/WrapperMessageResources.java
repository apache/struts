/*
 * $Id$
 *
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

package org.apache.struts2.s1;

import java.util.Locale;

import org.apache.struts.util.MessageResources;

import com.opensymphony.xwork2.TextProvider;

/**
 * Wraps the Struts 1 message resources, delegating to Struts 2 resources
 */
public class WrapperMessageResources extends MessageResources {

    private TextProvider textProvider;

    public WrapperMessageResources(TextProvider provider) {
        super(null, null, true);
        this.textProvider = provider;
    }

    @Override
    public String getMessage(Locale locale, String key) {
        String msg = textProvider.getText(key);
        return msg; 
    }

}
