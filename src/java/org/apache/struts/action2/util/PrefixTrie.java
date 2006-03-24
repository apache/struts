package com.opensymphony.webwork.util;

/**
 * Quickly matches a prefix to an object.
 *
 * @author crazybob@google.com (Bob Lee)
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
