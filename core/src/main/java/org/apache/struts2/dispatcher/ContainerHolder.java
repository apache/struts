package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.inject.Container;
import org.apache.struts2.StrutsConstants;

/**
 * Simple class to hold Container instance per thread to minimise number of attempts
 * to read configuration and build each time a new configuration.
 *
 * Thus depends on {@link StrutsConstants#STRUTS_CONFIGURATION_XML_RELOAD} flag,
 * if set to false just use stored container, configuration will do not change.
 */
class ContainerHolder {

    private static ThreadLocal<Container> instance = new ThreadLocal<Container>();

    public static void store(Container instance) {
        boolean reloadConfigs = Boolean.valueOf(instance.getInstance(String.class, StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD));
        if (!reloadConfigs) {
            // reloadConfigs is false, configuration will do not change, just keep it
            ContainerHolder.instance.set(instance);
        }
    }

    public static Container get() {
        return ContainerHolder.instance.get();
    }

    public static void clear() {
        ContainerHolder.instance.remove();
    }

}
