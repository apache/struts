/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.util;

/**
 * An interface to be implemented by any ObjectFactory implementation 
 * if it requires shutdown hook whenever an ObjectFactory is to be 
 * destroyed. 
 * 
 * @author tm_jee
 * @version $Date: 2006/03/16 14:44:01 $ $Id: ObjectFactoryDestroyable.java,v 1.1 2006/03/16 14:44:01 tmjee Exp $
 * 
 * @see org.apache.struts.action2.dispatcher.FilterDispatcher
 * @see org.apache.struts.action2.dispatcher.DispatcherUtils
 */
public interface ObjectFactoryDestroyable {
	void destroy();
}
