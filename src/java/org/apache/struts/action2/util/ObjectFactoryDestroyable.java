/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

/**
 * An interface to be implemented by any ObjectFactory implementation 
 * if it requires shutdown hook whenever an ObjectFactory is to be 
 * destroyed. 
 * 
 * @author tm_jee
 * @version $Date: 2006/03/16 14:44:01 $ $Id: ObjectFactoryDestroyable.java,v 1.1 2006/03/16 14:44:01 tmjee Exp $
 * 
 * @see com.opensymphony.webwork.dispatcher.FilterDispatcher
 * @see com.opensymphony.webwork.dispatcher.DispatcherUtils
 */
public interface ObjectFactoryDestroyable {
	void destroy();
}
