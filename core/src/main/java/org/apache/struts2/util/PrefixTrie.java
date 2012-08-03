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

package org.apache.struts2.util;

/**
 * Quickly matches a prefix to an object.
 *
 */
public class PrefixTrie {

    // supports 7-bit chars.
    private static final int SIZE = 128;

    Node root = new Node();

    public void put(String prefix, Object value) {
        Node current = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (c > SIZE)
                throw new IllegalArgumentException("'" + c + "' is too big.");
            if (current.next[c] == null)
                current.next[c] = new Node();
            current = current.next[c];
        }
        current.value = value;
    }

    public Object get(String key) {
        Node current = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c > SIZE)
                return null;
            current = current.next[c];
            if (current == null)
                return null;
            if (current.value != null)
                return current.value;
        }
        return null;
    }

    static class Node {
        Object value;
        Node[] next = new Node[SIZE];
    }
}
