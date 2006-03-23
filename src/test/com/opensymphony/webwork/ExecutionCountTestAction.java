/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork;

import com.opensymphony.xwork.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * ExecutionCountTestAction
 *
 * @author Jason Carreira
 *         Created Apr 18, 2003 6:17:40 PM
 */
public class ExecutionCountTestAction extends ActionSupport {

    private static final Log LOG = LogFactory.getLog(ExecutionCountTestAction.class);


    private int executionCount = 0;


    public int getExecutionCount() {
        return executionCount;
    }

    public String execute() throws Exception {
        executionCount++;
        LOG.info("executing ExecutionCountTestAction. Current count is " + executionCount);

        return SUCCESS;
    }
}
