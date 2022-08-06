package org.apache.struts2.dispatcher;

import java.util.Map.Entry;

abstract class StringObjectEntry implements Entry<String, Object> {
    private String key;
    private Object value;

    public StringObjectEntry(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Entry)) {
            return false;
        }
        Entry<?, ?> entry = (Entry<?, ?>) obj;

        return keyEquals(entry) && valueEquals(entry);
    }

    private boolean keyEquals(final Entry<?, ?> entry) {
        return (key == null) ? (entry.getKey() == null) : key.equals(entry.getKey());
    }

    private boolean valueEquals(Entry<?, ?> entry) {
        return (value == null) ? (entry.getValue() == null) : value.equals(entry.getValue());
    }

    @Override
    public int hashCode() {
        return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
    }
}
