package cash.action;

import org.apache.log4j.Logger;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

/**
 * Superclass for Hibernate-aware actions.
 *
 * @author Joel Hockey
 * @version $Id: HibernateAction.java,v 1.1 2005/03/21 04:16:08 plightbo Exp $
 */
public abstract class HibernateAction extends ActionSupport {
    /** xwork action return code */
    public static final String DBERROR = "dberror";

    private static final Logger LOG = Logger.getLogger(HibernateAction.class);

    private boolean m_rollback = false;

    /** roll back the current session */
    protected void setRollbackOnly() { m_rollback = true; }

    /** @return whether the current Hibernate Session should be rolled back */
    public boolean getRollback() { return m_rollback; }

    /**
     * Sets an object into the web session
     * @param key Key used to index object
     * @param object The object to store
     */
    public void set(Object key, Object object) {
        ActionContext.getContext().getSession().put(key, object);
    }

    /**
     * Retrieve object from web session
     * @param key Key used to index object
     * @return object if it exists, or null
     */
    public Object get(Object key) {
        return ActionContext.getContext().getSession().get(key);
    }
}


