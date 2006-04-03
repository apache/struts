package cash.util;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Session;
import net.sf.hibernate.cfg.Configuration;

import org.apache.log4j.Logger;

/**
 * Helper Singleton class to manage Hibernate Sessions.
 *
 * @author Joel Hockey
 * @version $Id$
 */
public class HibernateUtil {

    /** ThreadLocal Session Map */
    public static final ThreadLocal MAP = new ThreadLocal();

    private static final Logger LOG = Logger.getLogger(HibernateUtil.class);

    private static final SessionFactory SESSION_FACTORY;

    /** Make default construct private */
    private HibernateUtil() { }

    /** Loads Hibernate config. */
    static {
        try {
            LOG.debug("HibernateUtil.static - loading config");
            SESSION_FACTORY = new Configuration().configure().buildSessionFactory();
            LOG.debug("HibernateUtil.static - end");
        } catch (HibernateException ex) {
            throw new RuntimeException("Exception building SessionFactory: " + ex.getMessage(), ex);
        }
    }

    /**
     * Gets Hibernate Session for current thread.  When finished, users
     * must return session using {@link #closeSession() closeSession()} method.
     * @return Hibernate Session for current thread.
     * @throws HibernateException if there is an error opening a new session.
     */
    public static Session currentSession() throws HibernateException {
        Session s = (Session)MAP.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) {
            s = SESSION_FACTORY.openSession();
            MAP.set(s);
        }
        return s;
    }

    /**
     * Closes the Hibernate Session.  Users must call this method after calling
     * {@link #currentSession() currentSession()}.
     * @throws HibernateException if session has problem closing.
     */
    public static void closeSession() throws HibernateException {
        Session s = (Session)MAP.get();
        MAP.set(null);
        if (s != null) {
            s.close();
        }
    }
}