/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.struts2.jasper.runtime;

import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;

import org.apache.AnnotationProcessor;


/**
 * Verify the annotation and Process it.
 *
 * @author Fabien Carrion
 * @author Remy Maucherat
 * @version $Revision: 467222 $, $Date: 2006-10-24 05:17:11 +0200 (Tue, 24 Oct 2006) $
 */
public class AnnotationHelper {

    
    /**
     * Call postConstruct method on the specified instance. Note: In Jasper, this
     * calls naming resources injection as well.
     *
     * @param processor annotation processor
     * @param instance object instance
     *
     * @throws IllegalAccessException on error
     * @throws InvocationTargetException on error
     * @throws NamingException on error
     */
    public static void postConstruct(AnnotationProcessor processor, Object instance)
        throws IllegalAccessException, InvocationTargetException, NamingException {
        if (processor != null) {
            processor.processAnnotations(instance);
            processor.postConstruct(instance);
        }
    }
    
    
    /**
     * Call preDestroy method on the specified instance.
     *
     * @param processor annotation processor
     * @param instance object instance
     *
     * @throws IllegalAccessException  on error
     * @throws InvocationTargetException on error
     */
    public static void preDestroy(AnnotationProcessor processor, Object instance)
        throws IllegalAccessException, InvocationTargetException {
        if (processor != null) {
            processor.preDestroy(instance);
        }
    }
    

}
