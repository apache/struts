/*
 * $Id: SessionAware.java 394468 2006-04-16 12:16:03Z tmjee $
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts2.interceptor;

import java.util.Map;

/**
 * Actions that want access to the current serlvet request attributes should implement this interface.<p />
 * 
 * This interface is only relevant if the Action is used in a servlet environment.<p/>
 * 
 * Note that using this interface makes the Action tied to a servlet environment, so it should be
 * avoided if possible since things like unit testing will become more difficult.
 */
public interface RequestAware {

	/**
     * Sets the Map of request attributes in the implementing class.
     *
     * @param session a Map of HTTP request attribute name/value pairs.
     */
    public void setRequest(Map request);
}
