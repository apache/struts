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
 * This package is a copy of the org.springframework.mock.web.portlet package from Spring 4.3.x
 * (org.springframework, spring-test, <a href="https://github.com/spring-projects/spring-framework/tree/4.3.x</a>)
 * which changes the package name to: org.apache.struts2.mock.web.portlet.
 *
 * The copyright and license notice above is reproduced from the original package's ServletWrappingPortletContext.java,
 * since it contained the widest year-range.
 *
 * With Spring 5 dropping Portlet MVC support completely [SPR-14129], copying this package should allow
 * individuals to continue using JUnit unit tests for their Struts 2 Portlet Plugin applications with
 * Spring 5.x.
 */
/**
 * A comprehensive set of Portlet API mock objects, targeted at usage with Spring's web MVC framework.
 * Useful for testing web contexts and controllers.
 *
 * <p>More convenient to use than dynamic mock objects (<a href="http://easymock.org/">EasyMock</a>) or
 * existing Portlet API mock objects.
 */
package org.apache.struts2.mock.web.portlet;