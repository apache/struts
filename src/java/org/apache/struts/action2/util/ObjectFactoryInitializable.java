package org.apache.struts.action2.util;

import javax.servlet.ServletContext;

/**
 * Used to pass ServletContext init parameters to various
 * frameworks such as Spring, Plexus and Portlet.
 *
 * @author plightbo
 * @version $Date$ $Id$
 */
public interface ObjectFactoryInitializable {

    void init(ServletContext servletContext);

}
