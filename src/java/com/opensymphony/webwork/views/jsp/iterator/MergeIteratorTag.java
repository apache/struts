/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.iterator;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.MergeIterator;
import com.opensymphony.webwork.views.jsp.ComponentTagSupport;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Append a list of iterators. The values of the iterators will be merged
 * into one iterator.
 *
 * @author Rickard �berg (rickard@dreambean.com)
 * @author tm_jee ( tm_jee (at) yahoo.co.uk )
 * @see MergeIterator
 * @see com.opensymphony.webwork.util.MergeIteratorFilter
 */
public class MergeIteratorTag extends ComponentTagSupport {

	private static final long serialVersionUID = 4999729472466011218L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new MergeIterator(stack);
	}

}
