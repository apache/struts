package org.apache.struts2.config;

import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;


/**
 * MethodConfigurationProvider creates ActionConfigs for potential action
 * methods without a corresponding action.
 * <p>
 * The provider iterates over the set of namespaces and the set of actionNames
 * in a Configuration and retrieves each ActionConfig.
 * For each ActionConfig that invokes the default "execute" method,
 * the provider inspects the className class for other non-void,
 * no-argument methods that do not begin with "get".
 * For each qualifying method, the provider looks for another actionName in
 * the same namespace that equals action.name + "!" + method.name.
 * If that actionName is not found, System copies the ActionConfig,
 * changes the method property, and adds it to the package configuration
 * under the new actionName (action!method).
 * <p>
 * The system ignores ActionConfigs with a method property set so as to
 * avoid creating alias methods for alias methods.
 * The system ignores "get" methods since these would appeare to be
 * JavaBeans property and would not be intended as action methods.  
 *
 *
 * starts with default "execute" ActionConfigs so that an
 * application can provide its own alias methods too.
 */
public class MethodConfigurationProvider  implements ConfigurationProvider {

    public void destroy() {
        // TODO
    }

    public void init(Configuration configuration) throws ConfigurationException {
        // TODO
    }

    public void register(ContainerBuilder containerBuilder, LocatableProperties locatableProperties) throws ConfigurationException {
        // TODO
    }

    public void loadPackages() throws ConfigurationException {
        // TODO
    }

    public boolean needsReload() {
        return false;   // TODO
    }
}
