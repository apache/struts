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

package com.opensymphony.xwork2.inject;

/**
 * Beans marked with this interface will be always initialised
 * after the internal DI mechanism will be created.
 *
 * It should be only used internally!
 *
 * @since Struts 2.5.14
 */
public interface Initializable {

    /**
     * Use this method to initialise your bean, the whole dependency graph was already built
     */
    void init();

}
