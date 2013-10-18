package org.apache.struts2.security;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of {@link org.apache.struts2.security.SecurityGate}
 * just examines all the defined {@link org.apache.struts2.security.SecurityGuard}'s
 */
public class DefaultSecurityGate implements SecurityGate {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityGate.class);

    private List<SecurityGuard> guards;
    private boolean devMode;

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String devMode) {
        this.devMode = "true".equalsIgnoreCase(devMode);
    }

    @Inject
    public void setContainer(Container container) {
        guards = new ArrayList<SecurityGuard>();
        Set<String> guardNames = container.getInstanceNames(SecurityGate.class);
        for (String guardName : guardNames) {
            SecurityGuard guard = container.getInstance(SecurityGuard.class, guardName);
            if (guard != null) {
                guards.add(guard);
            } else if (devMode) {
                LOG.debug("Got null instance of [#0] for name [#1]", SecurityGuard.class.getSimpleName(), guardName);
            }
        }
    }

    public void check(HttpServletRequest request) {
        for (SecurityGuard guard : guards) {
            SecurityPass pass = guard.accept(request);
            if (pass.isNotAccepted()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("[#0] didn't accept the request!", guard.getClass().getName());
                }
                throw new StrutsSecurityException(pass.getGuardMessage());
            }
        }
    }

}
