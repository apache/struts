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
package com.opensymphony.xwork2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>GenericsBean</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class GenericsBean {
    private List<Double> blubb;
    private List<Double> getterList;
    private Map<Double, Integer> genericMap = new HashMap<>();
    private Map<Double, List<Integer>> extendedMap = new HashMap<>();

    /**
     * @return Returns the doubles.
     */
    public List<Double> getDoubles() {
        return blubb;
    }

    /**
     * @param doubles The doubles to set.
     */
    public void setDoubles(List<Double> doubles) {
        this.blubb = doubles;
    }

    public Map<Double, Integer> getGenericMap() {
        return genericMap;
    }

    public void setGenericMap(Map<Double, Integer> genericMap) {
        this.genericMap = genericMap;
    }

    public List<Double> getGetterList() {
        if ( getterList == null ) {
            getterList = new ArrayList<>(1);
            getterList.add(42.42);
        }
        return getterList;
    }

    public Map<Double, List<Integer>> getExtendedMap() {
        return extendedMap;
    }

    public void setExtendedMap(Map<Double, List<Integer>> extendedMap) {
        this.extendedMap = extendedMap;
    }
}
