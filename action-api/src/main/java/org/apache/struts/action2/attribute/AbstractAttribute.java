package org.apache.struts.action2.attribute;

import java.util.Map;

/**
 * Support for {@link Attribute} implementations.
 *
 * <p>This class isn't public because we may want to optimize the implementation later. The current approach maximizes
 * code reuse and only performs one thread local lookup per method invocation, but it also creates an object per
 * invocation.
 *
 * @author crazybob@google.com (Bob Lee)
 */
abstract class AbstractAttribute<T> implements Attribute<T> {

    String name;

    AbstractAttribute(String name) {
        if (name == null) {
            throw new NullPointerException("Null name.");
        }
        this.name = name;
    }

    public T get() {
        return execute(new UnitOfWork<T>() {
            public T execute(Map<String, Object> map) {
                T t = (T) map.get(name);
                if (t == null) {
                    t = initialValue();
                    if (t != null) {
                        map.put(name, t);
                    }
                }
                return t;
            }
        });
    }

    public T set(final T t) {
        return execute(new UnitOfWork<T>() {
            public T execute(Map<String, Object> map) {
                return (T) map.put(name, t);
            }
        });
    }

    public T remove() {
        return execute(new UnitOfWork<T>() {
            public T execute(Map<String, Object> map) {
                return (T) map.remove(name);
            }
        });
    }

    abstract T execute(UnitOfWork<T> unitOfWork);

    /**
     * Called by {@link #get()} to initialize the attribute value when it is {@code null}. The default implementation
     * returns {@code null}.
     *
     * @return initial attribute value
     */
    protected T initialValue() {
        return null;
    }
}