/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.components.Bean;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Bean
 */
public class BeanTag extends ComponentTagSupport {
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
