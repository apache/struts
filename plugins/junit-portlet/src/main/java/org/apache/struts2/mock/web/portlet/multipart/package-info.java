/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * This package is a copy of the org.springframework.web.portlet.multipart package from Spring 4.3.x
 * (org.springframework, spring-webmvc-portlet, <a href="https://github.com/spring-projects/spring-framework/tree/4.3.x</a>)
 * which changes the package name to: org.apache.struts2.mock.web.portlet.multipart.
 *
 * The copyright and license notice above is reproduced from the original package's CommonsPortletMultipartResolver.java,
 * since it contained the widest year-range.
 *
 * The mock objects only have a dependency on one file from the package, MultipartActionRequest.java, so
 * it was required.  The other files in the package were not retained in this copy (CommonsPortletMultipartResolver.java
 * in particular created additional dependencies that would require copying additional packages).
 *
 * With Spring 5 dropping Portlet MVC support completely [SPR-14129], copying this package should allow
 * individuals to continue using JUnit unit tests for their Struts 2 Portlet Plugin applications with
 * Spring 5.x.
 */
/**
 * Multipart resolution framework for handling file uploads.
 * Provides a PortletMultipartResolver strategy interface,
 * and a generic extension of the ActionRequest interface
 * for accessing multipart files in web application code.
 */
package org.apache.struts2.mock.web.portlet.multipart;
