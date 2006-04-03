/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.util;

/**
 * An interface indicating the lifecycle of an ObjectFactory implementation.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 * 
 * @see ObjectFactoryLifecycle
 * @see com.opensymphony.xwork.ObjectFactory
 * @see org.apache.struts.action2.util.ObjectFactoryInitializable
 * @see org.apache.struts.action2.util.ObjectFactoryDestroyable
 */
public interface ObjectFactoryLifecycle extends ObjectFactoryInitializable, ObjectFactoryDestroyable {
	
}
