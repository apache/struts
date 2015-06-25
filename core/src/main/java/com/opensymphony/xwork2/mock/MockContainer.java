package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;

import java.util.Set;

/**
 * Mock implementation to be used in unittests
 */
public class MockContainer implements Container {

    public void inject(Object o) {

    }

    public <T> T inject(Class<T> implementation) {
        return null;
    }

    public <T> T getInstance(Class<T> type, String name) {
        return null;
    }

    public <T> T getInstance(Class<T> type) {
        return null;
    }

    public Set<String> getInstanceNames(Class<?> type) {
        return null;
    }

    public void setScopeStrategy(Scope.Strategy scopeStrategy) {

    }

    public void removeScopeStrategy() {

    }

}
