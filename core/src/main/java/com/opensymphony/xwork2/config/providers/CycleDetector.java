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
package com.opensymphony.xwork2.config.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CycleDetector<T> {
    private DirectedGraph<T> graph;
    private Map<T, Status> marks;
    private List<T> verticesInCycles;
    
    private enum Status { MARKED, COMPLETE };

    public CycleDetector(DirectedGraph<T> graph) {
        this.graph = graph;
        marks = new HashMap<>();
        verticesInCycles = new ArrayList<>();
    }

    public boolean containsCycle() {
        for (T v : graph) {
            if (!marks.containsKey(v)) {
                if (mark(v)) {
                    // return true;
                }
            }
        }
        // return false;
        return !verticesInCycles.isEmpty();
    }

    private boolean mark(T vertex) {
        /*
         * return statements commented out for fail slow behavior detect all nodes in cycles instead of just the first one
         */
        List<T> localCycles = new ArrayList<T>();
        marks.put(vertex, Status.MARKED);
        for (T u : graph.edgesFrom(vertex)) {
            if (marks.get(u) == Status.MARKED) {
                localCycles.add(vertex);
                // return true;
            } else if (!marks.containsKey(u)) {
                if (mark(u)) {
                    localCycles.add(vertex);
                    // return true;
                }
            }
        }
        marks.put(vertex, Status.COMPLETE);
        // return false;
        verticesInCycles.addAll(localCycles);
        return !localCycles.isEmpty();
    }

    public List<T> getVerticesInCycles() {
        return verticesInCycles;
    }
}
