package org.apache.struts.action2.interceptor;

/**
 * Actions that want access to the Principal information from HttpServletRequest object
 * should implement this interface.
 *
 * <p>This interface is only relevant if the Action is used in a servlet environment.
 * By using this interface you will not become tied to servlet environment.</p>
 *
 * @author Remigijus Bauzys
 * @version $Revision$
 */
public interface PrincipalAware {
    void setPrincipalProxy(PrincipalProxy principalProxy);
}
