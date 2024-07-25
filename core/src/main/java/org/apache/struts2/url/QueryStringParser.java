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
package org.apache.struts2.url;

import java.io.Serializable;
import java.util.Map;

/**
 * Used to parse Http Query String into a map of parameters with support for fragment
 *
 * @since Struts 6.1.0
 */
public interface QueryStringParser extends Serializable {

    /**
     * @param queryString a query string to parse
     * @return a {@link Result} of parsing the query string
     * @since Struts 6.2.0
     */
    Result parse(String queryString);

    /**
     * Return an empty {@link Result}
     * @return empty result
     */
    Result empty();

    /**
     * Represents result of parsing query string by implementation of {@link QueryStringParser}
     */
    interface Result {

        Result addParam(String name, String value);

        Result withQueryFragment(String queryFragment);

        Map<String, Object> getQueryParams();

        String getQueryFragment();

        boolean contains(String name);

        boolean isEmpty();
    }

}
