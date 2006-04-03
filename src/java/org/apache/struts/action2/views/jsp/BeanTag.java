/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp;

import org.apache.struts.action2.components.Bean;
import org.apache.struts.action2.components.Component;
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
