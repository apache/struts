/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.ValidationAware;


/**
 * The context for validation. This interface extends others to provide methods for reporting
 * errors and messages as well as looking up error messages in a resource bundle using a specific locale.
 *
 * @author Jason Carreira
 */
public interface ValidatorContext extends ValidationAware, TextProvider, LocaleProvider {

    /**
     * Translates a simple field name into a full field name in OGNL syntax.
     *
     * @param fieldName the field name to lookup.
     * @return the full field name in OGNL syntax.
     */
    String getFullFieldName(String fieldName);
}
