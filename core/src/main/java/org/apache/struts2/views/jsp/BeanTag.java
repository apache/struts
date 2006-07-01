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
package org.apache.struts2.views.jsp;

import org.apache.struts2.components.Bean;
import org.apache.struts2.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Bean
 */
public class BeanTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = -3863152522071209267L;

	protected static Log log = LogFactory.getLog(BeanTag.class);

    protected String name;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Bean(stack);
    }

    protected void populateParams() {
        super.populateParams();

        ((Bean) component).setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
