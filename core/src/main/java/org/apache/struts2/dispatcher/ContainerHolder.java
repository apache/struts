package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.inject.Container;

/**
 * Simple class to hold Container instance per thread to minimise number of attempts
 * to read configuration and build each time a new configuration.
 *
 * As ContainerHolder operates just per thread (which means per request) there is no need
 * to check if configuration changed during the same request. If changed between requests,
 * first call to store Container in ContainerHolder will be with the new configuration.
 */
class ContainerHolder {

    private static ThreadLocal<Container> instance = new ThreadLocal<Container>();

    public static void store(Container instance) {
        ContainerHolder.instance.set(instance);
    }

    public static Container get() {
        return ContainerHolder.instance.get();
    }

    public static void clear() {
        ContainerHolder.instance.remove();
    }

}
