/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.webwork.showcase.token;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

import java.util.Date;

/**
 * Example to illustrate the <code>token</code> and <code>token-session</code> interceptor.
 *
 * @author Claus Ibsen
 */
public class TokenAction extends ActionSupport {

    private int amount;

    public String execute() throws Exception {
        // transfer from source to destination

        Integer balSource = (Integer) ActionContext.getContext().getSession().get("balanceSource");
        Integer balDest = (Integer) ActionContext.getContext().getSession().get("balanceDestination");

        Integer newSource = new Integer(balSource.intValue() - amount);
        Integer newDest = new Integer(balDest.intValue() + amount);

        ActionContext.getContext().getSession().put("balanceSource", newSource);
        ActionContext.getContext().getSession().put("balanceDestination", newDest);
        ActionContext.getContext().getSession().put("time", new Date());

        Thread.sleep(2000); // to simulate processing time

        return SUCCESS;
    }

    public String doInput() throws Exception {
        // prepare input form
        Integer balSource = (Integer) ActionContext.getContext().getSession().get("balanceSource");
        Integer balDest = (Integer) ActionContext.getContext().getSession().get("balanceDestination");

        if (balSource == null) {
            // first time set up an initial account balance
            balSource = new Integer(1200);
            ActionContext.getContext().getSession().put("balanceSource", balSource);
        }

        if (balDest == null) {
            // first time set up an initial account balance
            balDest = new Integer(2500);
            ActionContext.getContext().getSession().put("balanceDestination", balDest);
        }

        return INPUT;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
