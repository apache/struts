/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import com.opensymphony.webwork.views.velocity.VelocityManager;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.IOException;


/**
 * WebWork velocity related util.
 *
 * @author CameronBraid
 */
public class VelocityWebWorkUtil extends WebWorkUtil {

    private Context ctx;

    public VelocityWebWorkUtil(Context ctx, OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.ctx = ctx;
    }

    public String evaluate(String expression) throws IOException, ResourceNotFoundException, MethodInvocationException, ParseErrorException {
        CharArrayWriter writer = new CharArrayWriter();
        VelocityManager.getInstance().getVelocityEngine().evaluate(ctx, writer, "Error parsing " + expression, expression);

        return writer.toString();
    }

}
