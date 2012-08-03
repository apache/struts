/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.convention.annotation;

import junit.framework.TestCase;

import org.apache.struts2.convention.actions.namespace.PackageLevelNamespaceAction;
import org.apache.struts2.convention.actions.resultpath.ClassLevelResultPathAction;

/**
 * <p>
 * This class tests the annotation tools.
 * </p>
 */
public class AnnotationToolsTest extends TestCase {
    public void testFindAnnotationOnClass() {
        ResultPath rl = AnnotationTools.findAnnotation(ClassLevelResultPathAction.class, ResultPath.class);
        assertNotNull(rl);
        assertEquals("/class-level", rl.value());
    }

    public void testFindAnnotationOnPackage() {
        Namespace ns = AnnotationTools.findAnnotation(PackageLevelNamespaceAction.class, Namespace.class);
        assertNotNull(ns);
        assertEquals("/package-level", ns.value());
    }
}