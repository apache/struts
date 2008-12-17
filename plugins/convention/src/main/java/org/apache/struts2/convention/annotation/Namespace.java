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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * This annotation defines how actions can modify the namespace
 * that they are added to. This overrides the behavior of the
 * Convention plugin which by default uses the package names for
 * namespaces. Since XWork packages are created by the Convention
 * plugin via the Java packages that the actions exist in, there
 * is some tricky handling of XWork parent packages and namespaces
 * of the XWork packages for the Convention plugin discovered
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
 * Java package can specify different parent packages (using the
 * {@link org.apache.struts2.convention.annotation.ParentPackage} annotation)
 * and namespaces (using this annotation).
 * </p>
 *
 * <p>
 * The value of a Namespace annotation should specify the portion of
 * the action URL between the context path and the action name.  For
 * example:
 * </p>
 * <pre>
 * &#064;Namespace("/careers/job-postings-overview/job-postings")
 * </pre>
 *
 * <p>
 * This annotation can also be placed inside the special Java file
 * named <strong>package-info.java</strong>, which allows package
 * level annotations. If this is used in this manner it changes the
 * default namespace for all actions within that Java package. The
 * search order for the namespace of a particular class is therefore:
 * </p>
 *
 * <ol>
 * <li>If the {@link Action} annotation exists within the action and
 *  specifies a full URI (i.e. it starts with a / character)</li>
 * <li>Any Namespace annotations placed on individual action classes</li>
 * <li>Any Namespace annotations placed in the package-info.java file</li>
 * <li>The namespace as determined using the Java package name and the
 *  standard convention based naming.</li>
 * </ol>
 * <!-- END SNIPPET: javadoc -->
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Namespace {
    /**
     * @return  The namespace value.
     */
    String value();
}
