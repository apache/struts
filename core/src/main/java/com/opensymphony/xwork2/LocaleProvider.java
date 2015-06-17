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
package com.opensymphony.xwork2;

import java.util.Locale;


/**
 * Indicates that the implementing class can provide its own {@link Locale}.
 * <p/>
 * This is useful for when an action may wish override the default locale. All that is
 * needed is to implement this interface and return your own custom locale.
 * The {@link TextProvider} interface uses this interface heavily for retrieving
 * internationalized messages from resource bundles.
 *
 * @author Jason Carreira
 */
public interface LocaleProvider {

    /**
     * Gets the provided locale.
     *
     * @return  the locale.
     */
    Locale getLocale();

}
