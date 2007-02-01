package org.apache.struts2.config;

import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.ObjectFactory;

import java.util.*;
import java.lang.reflect.Method;

/**
 * MethodConfigurationProvider creates ActionConfigs for potential action
 * methods that lack a corresponding action mapping,
 * so that these methods can be invoked without extra or redundant configuration.
 * <p/>
 * As a dynamic method, the behavior of this class could be represented as:
 * <p/>
 * <code>
 * int bang = name.indexOf('!');
 * if (bang != -1) {
 * String method = name.substring(bang + 1);
 * mapping.setMethod(method);
 * name = name.substring(0, bang);
 * }
 * </code>
 * <p/>
 * If the action URL is "foo!bar", the the "foo" action is invoked,
 * calling "bar" instead of "execute".
 * <p/>
 * Instead of scanning each request at runtime, the provider creates action mappings
 * for each method that could be matched using a dynamic approach.
 * Advantages over a dynamic approach are that:
 * <p/>
 * <ul>
 * <ol>The "dynamic" methods are not a special case, but just another action mapping,
 * with all the features of a hardcoded mapping.
 * <ol>When needed, a manual action can be provided for a method and invoked with the same
 * syntax as an automatic action.
 * <ol>The ConfigBrowser can display all potential actions.
 * </ul>
 */
public class MethodConfigurationProvider implements ConfigurationProvider {

    /**
     * Stores configuration property.
     */
    private Configuration configuration;

    /**
     * Updates configuration property.
     * @param configuration New configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    // See superclass for Javadoc
    public void destroy() {
        // Override to provide functionality
    }

    // See superclass for Javadoc
    public void init(Configuration configuration) throws ConfigurationException {
        setConfiguration(configuration);
        configuration.rebuildRuntimeConfiguration();
    }

    // See superclass for Javadoc
    public void register(ContainerBuilder containerBuilder, LocatableProperties locatableProperties) throws ConfigurationException {
        // Override to provide functionality
    }

    // See superclass for Javadoc
    public void loadPackages() throws ConfigurationException {

        Set namespaces = Collections.EMPTY_SET;
        RuntimeConfiguration rc = configuration.getRuntimeConfiguration();
        Map allActionConfigs = rc.getActionConfigs();
        if (allActionConfigs != null) {
            namespaces = allActionConfigs.keySet();
        }

        if (namespaces.size() == 0) {
            throw new ConfigurationException("MethodConfigurationProvider.loadPackages: namespaces.size == 0");
        }

        boolean added = false;
        for (Object namespace : namespaces) {
            Map actions = (Map) allActionConfigs.get(namespace);
            Set actionNames = actions.keySet();
            for (Object actionName : actionNames) {
                ActionConfig actionConfig = (ActionConfig) actions.get(actionName);
                added = added | addDynamicMethods(actions, (String) actionName, actionConfig);
            }
        }

        reload = added;
    }

    /**
     * Store needsReload property.
     */
    boolean reload;

    // See superclass for Javadoc
    public boolean needsReload() {
        return reload;
    }

    /**
     * Stores ObjectFactory property.
     */
    ObjectFactory factory;

    /**
     * Updates ObjectFactory property.
     * @param factory
     */
    public void setObjectFactory(ObjectFactory factory) {
        this.factory = factory;
    }

    /**
     * Provides ObjectFactory property.
     * @return
     * @throws ConfigurationException if ObjectFactory has not been set.
     */
    private ObjectFactory getObjectFactory() throws ConfigurationException {
        if (factory == null) {
            factory = ObjectFactory.getObjectFactory();
            if (factory == null) throw new
                    ConfigurationException("MethodConfigurationProvider.getObjectFactory: ObjectFactory==null");
        }
        return factory;
    }

    /**
     * Verifies that character at a String position is upper case.
     * @param pos Position to test
     * @param string Text containing position
     * @return True if character at a String position is upper case
     */
    private boolean upperAt(int pos, String string) {
        int len = string.length();
        if (len < pos) return false;
        String ch = string.substring(pos, pos+1);
        return ch.equals(ch.toUpperCase());
    }

   /**
    * Scans class for potential Action mehods,
    * automatically generating and registering ActionConfigs as needed.
    * <p/>
    * The system iterates over the set of namespaces and the set of actionNames
    * in a Configuration and retrieves each ActionConfig.
    * For each ActionConfig that invokes the default "execute" method,
    * the provider inspects the className class for other non-void,
    * no-argument methods that do not begin with "getX" or "isX".
    * For each qualifying method, the provider looks for another actionName in
    * the same namespace that equals action.name + "!" + method.name.
    * If that actionName is not found, System copies the ActionConfig,
    * changes the method property, and adds it to the package configuration
    * under the new actionName (action!method).
    * <p/>
    * The system ignores ActionConfigs with a method property set so as to
    * avoid creating alias methods for alias methods.
    * The system ignores "getX" and "isX" methods since these would appear to be
    * JavaBeans property and would not be intended as action methods.
    * (The X represents any upper character or non-letter.)
    * @param actions All ActionConfigs in namespace
    * @param actionName Name of ActionConfig to analyze
    * @param actionConfig ActionConfig corresponding to actionName
    */
    protected boolean addDynamicMethods(Map actions, String actionName, ActionConfig actionConfig) throws ConfigurationException {

        String configMethod = actionConfig.getMethodName();
        boolean hasMethod = (configMethod != null) && (configMethod.length() > 0);
        if (hasMethod) return false;

        String className = actionConfig.getClassName();
        Set actionMethods = new HashSet();
        Class actionClass;
        ObjectFactory factory = getObjectFactory();
        try {
            actionClass = factory.getClassInstance(className);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(e);
        }

        Method[] methods = actionClass.getMethods();
        for (Method method : methods) {
            String returnString = method.getReturnType().getName();
            boolean isString = "java.lang.String".equals(returnString);
            if (isString) {
                Class[] parameterTypes = method.getParameterTypes();
                boolean noParameters = (parameterTypes.length == 0);
                String methodString = method.getName();
                boolean notGetMethod = !((methodString.startsWith("get")) && upperAt(3, methodString));
                boolean notIsMethod = !((methodString.startsWith("is")) && upperAt(2, methodString));
                boolean notToString = !("toString".equals(methodString));
                boolean notExecute = !("execute".equals(methodString));
                boolean qualifies = noParameters && notGetMethod && notIsMethod && notToString && notExecute;
                if (qualifies) {
                    actionMethods.add(methodString);
                }
            }
        }

        for (Object actionMethod : actionMethods) {
            String methodName = (String) actionMethod;
            StringBuilder sb = new StringBuilder();
            sb.append(actionName);
            sb.append("!"); // TODO: Make "!" a configurable character
            sb.append(methodName);
            String newActionName = sb.toString();
            boolean haveAction = actions.containsKey(newActionName);
            if (haveAction) continue;
            ActionConfig newActionConfig = new ActionConfig(
                    newActionName,
                    actionConfig.getClassName(),
                    actionConfig.getParams(),
                    actionConfig.getResults(),
                    actionConfig.getInterceptors(),
                    actionConfig.getExceptionMappings());
            newActionConfig.setMethodName(methodName);
            String packageName = actionConfig.getPackageName();
            newActionConfig.setPackageName(packageName);
            PackageConfig packageConfig = configuration.getPackageConfig(packageName);
            packageConfig.addActionConfig(newActionName, actionConfig);
        }

        return (actionMethods.size() > 0);
    }
}
