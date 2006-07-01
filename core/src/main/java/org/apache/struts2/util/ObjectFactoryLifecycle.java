/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.util;

/**
 * An interface indicating the lifecycle of an ObjectFactory implementation.
 * 
 * @see ObjectFactoryLifecycle
 * @see com.opensymphony.xwork.ObjectFactory
 * @see org.apache.struts2.util.ObjectFactoryInitializable
 * @see org.apache.struts2.util.ObjectFactoryDestroyable
 */
public interface ObjectFactoryLifecycle extends ObjectFactoryInitializable, ObjectFactoryDestroyable {
	
}
