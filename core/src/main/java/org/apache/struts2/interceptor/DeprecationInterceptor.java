package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * <!-- START SNIPPET: description -->
 * In devMode checks if application uses deprecated or unknown constants and displays warning
 * when logging level is set to DEBUG
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * no special parameters yet
 * <!-- END SNIPPET: parameters -->
 */
public class DeprecationInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(DeprecationInterceptor.class);

    private Container container;
    private boolean devMode;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (devMode) {
            String message = validate();
            if (message != null) {
                LOG.debug(message);
            }
        }
        return invocation.invoke();
    }

    /**
     * Validates constants. Validation goes on only if devMode is set.
     *
     * @throws Exception
     */
    private String validate() throws Exception {
        Set<String> constants = new HashSet<String>();

        readConstants(constants, StrutsConstants.class);
        readConstants(constants, XWorkConstants.class);

        Set<String> applicationConstants = container.getInstanceNames(String.class);
        String message = null;
        if (!constants.containsAll(applicationConstants)) {
            Set<String> deprecated = new HashSet<String>(applicationConstants);
            deprecated.removeAll(constants);
            message = prepareMessage(deprecated);
        }
        return message;
    }

    private void readConstants(Set<String> constants, Class clazz) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            if (String.class.equals(field.getType())) {
                constants.add((String) field.get(clazz));
            }
        }
    }

    /**
     * Prepares message to display
     *
     * @param deprecated A set with deprecated/unknown constants
     */
    private String prepareMessage(Set<String> deprecated) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("*******************************************************************************\n");
        sb.append("**                                                                           **\n");
        sb.append("**                               WARNING                                     **\n");
        sb.append("**                YOU USE DEPRECATED / UNKNOWN CONSTANTS                     **\n");
        sb.append("**                                                                           **\n");

        for (String dep : deprecated) {
            sb.append(String.format("**  -> %-69s **\n", dep));
        }

        sb.append("*******************************************************************************\n");

        return sb.toString();
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String state) {
        this.devMode = "true".equals(state);
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

}
