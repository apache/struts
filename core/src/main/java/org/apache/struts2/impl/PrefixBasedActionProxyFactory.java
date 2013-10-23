package org.apache.struts2.impl;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * Prefix based factory should be used with {@link org.apache.struts2.dispatcher.mapper.PrefixBasedActionMapper}
 * to use appropriate {@link com.opensymphony.xwork2.ActionProxyFactory} connected with given
 * {@link org.apache.struts2.dispatcher.mapper.ActionMapper}
 *
 * Add below entry to struts.xml to enable the factory:
 * <p/>
 * <pre>
 * &lt;constant name="struts.actionProxyFactory" value="prefix"/&gt;
 * </pre>
 *
 * The factory will use the same set of patterns as defined with:
 * <p/>
 * <pre>
 * &lt;constant name="struts.mapper.prefixMapping" value="..."/&gt;
 * </pre>
 * <!-- END SNIPPET: description -->
 */
public class PrefixBasedActionProxyFactory extends DefaultActionProxyFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PrefixBasedActionProxyFactory.class);

    private Map<String, ActionProxyFactory> actionProxyFactories = new HashMap<String, ActionProxyFactory>();
    private ActionProxyFactory defaultFactory;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(StrutsConstants.STRUTS_ACTIONPROXYFACTORY)
    public void setActionProxyFactory(ActionProxyFactory factory) {
        this.defaultFactory = factory;
    }

    @Inject(StrutsConstants.PREFIX_BASED_MAPPER_CONFIGURATION)
    public void setPrefixBasedActionProxyFactories(String list) {
        if (list != null) {
            String[] factories = list.split(",");
            for (String factory : factories) {
                String[] thisFactory = factory.split(":");
                if ((thisFactory != null) && (thisFactory.length == 2)) {
                    String factoryPrefix = thisFactory[0].trim();
                    String factoryName = thisFactory[1].trim();
                    ActionProxyFactory obj = container.getInstance(ActionProxyFactory.class, factoryName);
                    if (obj != null) {
                        actionProxyFactories.put(factoryPrefix, obj);
                    } else if (LOG.isWarnEnabled()) {
                        LOG.warn("Invalid PrefixBasedActionProxyFactory config entry: [#0]", factory);
                    }
                }
            }
        }
    }

    public ActionProxy createActionProxy(String namespace, String actionName, String methodName,
                                         Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {

        String uri = namespace + (namespace.endsWith("/") ? actionName : "/" + actionName);
        for (int lastIndex = uri.lastIndexOf('/'); lastIndex > (-1); lastIndex = uri.lastIndexOf('/', lastIndex - 1)) {
            String key = uri.substring(0, lastIndex);
            ActionProxyFactory actionProxyFactory = actionProxyFactories.get(key);
            if (actionProxyFactory != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Using ActionProxyFactory [#0] for prefix [#1]", actionProxyFactory, key);
                }
                return actionProxyFactory.createActionProxy(namespace, actionName, methodName, extraContext, executeResult, cleanupContext);
            } else if (LOG.isDebugEnabled()) {
                LOG.debug("No ActionProxyFactory defined for [#1]", key);
            }
        }
        if (LOG.isDebugEnabled()){
            LOG.debug("Cannot find any matching ActionProxyFactory, falling back to [#0]", defaultFactory);
        }
        return defaultFactory.createActionProxy(namespace, actionName, methodName, extraContext, executeResult, cleanupContext);
    }

}
