package org.apache.struts.action2.util;

import javax.servlet.ServletContext;

/**
 * Used to pass ServletContext init parameters to various
 * frameworks such as Spring, Plexus and Portlet.
 *
 * @author plightbo
 * @version $Date: 2006/03/16 14:44:01 $ $Id: ObjectFactoryInitializable.java,v 1.3 2006/03/16 14:44:01 tmjee Exp $
 */
public interface ObjectFactoryInitializable {

    void init(ServletContext servletContext);

}
