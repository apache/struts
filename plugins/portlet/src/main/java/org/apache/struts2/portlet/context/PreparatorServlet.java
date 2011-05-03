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

package org.apache.struts2.portlet.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * @deprecated
 * 
 * This servlet has been deprecated. Do not use it (WW-2101)
 *
 */
public class PreparatorServlet extends HttpServlet implements StrutsStatics {

    private static final long serialVersionUID = 1853399729352984089L;

    private final static Logger LOG = LoggerFactory.getLogger(PreparatorServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
            if (LOG.isWarnEnabled()) {
		LOG.warn("The preparator servlet has been deprecated. It can safely be removed from your web.xml file");
            }
	}

}
