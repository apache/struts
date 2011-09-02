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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * This annotation allows actions to modify the parent package
 * that they are using. Since XWork packages are created by
 * the Convention plugin via the Java packages that the actions
 * exist in, there is some tricky handling of XWork parent packages
 * and namespaces of the XWork packages for discovered
 * actions so that two actions in the same package can specify
 * different parents and namespaces without collision.
 * </p>
 *
 * <p>
 * In order to handle this correctly, the name of the XWork
 * package that actions are placed into is built using this
 * format:
 * </p>
 *
 * <pre>
 * &lt;java-package>#&lt;parent-xwork-package>#&lt;namespace>
 * </pre>
 *
 * <p>
 * This mechanism will guarantee that two actions in the same
 * Java package can specify different parent packages (using this
 * annotation) and namespaces (using the {@link Namespace} annotation).
 * </p>
 *
 * <p>
 * This annotation can be used directly on Action classes or
 * in the <strong>package-info.java</strong> class in order
 * to specify the default XWork parent package for all actions
 * in the Java package. The search order for XWork parent packages
 * is therefore:
 * </p>
 *
 * <ol>
 * <li>Any ParentPackage annotations placed on individual action classes</li>
 * <li>Any ParentPackage annotations placed in the package-info.java file</li>
 * <li>The struts configuration property <strong>struts.convention.default.parent.package</strong></li>
 * </ol>
 * <!-- END SNIPPET: javadoc -->
 */
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ParentPackage {
    /**
     * @return  The parent package.
     */
    String value();
}