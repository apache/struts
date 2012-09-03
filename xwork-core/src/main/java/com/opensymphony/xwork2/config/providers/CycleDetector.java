package com.opensymphony.xwork2.config.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CycleDetector<T> {
    private static final String marked = "marked";
    private static final String complete = "complete";
    private DirectedGraph<T> graph;
    private Map<T, String> marks;
    private List<T> verticesInCycles;

    public CycleDetector(DirectedGraph<T> graph) {
        this.graph = graph;
        marks = new HashMap<T, String>();
        verticesInCycles = new ArrayList<T>();
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
        marks.put(vertex, marked);
        for (T u : graph.edgesFrom(vertex)) {
            if (marks.containsKey(u) && marks.get(u).equals(marked)) {
                localCycles.add(vertex);
                // return true;
            } else if (!marks.containsKey(u)) {
                if (mark(u)) {
                    localCycles.add(vertex);
                    // return true;
                }
            }
        }
        marks.put(vertex, complete);
        // return false;
        verticesInCycles.addAll(localCycles);
        return !localCycles.isEmpty();
    }

    public List<T> getVerticesInCycles() {
        return verticesInCycles;
    }
}
