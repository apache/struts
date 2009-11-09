/*
 * $Id: DefaultActionSupport.java 651946 2008-04-27 13:41:38Z apetrelli $
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
/**
 * This package contains a reimagining of the traditional Struts filter dispatchers.  Each specific deployment has
 * their own filters to prevent confusion.  In addition, the operations have been explicitly pulled into *Operations
 * objects that try to document through method naming what is happening at every step.  Here are a few common use
 * cases and how you would manage the Struts deployment:
 *
 * <h3>Simple Dispatcher</h3>
 * <pre>
 * &lt;filter&gt;
 *     &lt;filter-name&gt;struts2&lt;/filter-name&gt;
 *     &lt;filter-class&gt;org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 *
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;struts2&lt;/filter-name&gt;
 *     &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 *
 * <h3>Deployment with Sitemesh</h3>
 * <pre>
 * &lt;filter&gt;
 *     &lt;filter-name&gt;struts2-prepare&lt;/filter-name&gt;
 *     &lt;filter-class&gt;org.apache.struts2.dispatcher.ng.filter.StrutsPrepareFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter&gt;
 *     &lt;filter-name&gt;sitemesh&lt;/filter-name&gt;
 *     &lt;filter-class&gt;com.opensymphony.module.sitemesh.filter.PageFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter&gt;
 *     &lt;filter-name&gt;struts2-execute&lt;/filter-name&gt;
 *     &lt;filter-class&gt;org.apache.struts2.dispatcher.ng.filter.StrutsExecuteFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 *
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;struts2-prepare&lt;/filter-name&gt;
 *     &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;sitemesh&lt;/filter-name&gt;
 *     &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;struts2-execute&lt;/filter-name&gt;
 *     &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 * 
 */
package org.apache.struts2.dispatcher.ng;