/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.webwork.showcase;

import com.opensymphony.xwork.ActionSupport;

import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <code>DateAction</code>
 *
 * @author Rainer Hermanns
 */
public class DateAction extends ActionSupport {

    private static DateFormat DF = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    private Date now;
    private Date past;
    private Date future;
    private Date after;
    private Date before;


    public String getDate() {
        return DF.format(new Date());
    }


    /**
     * @return Returns the future.
     */
    public Date getFuture() {
        return future;
    }

    /**
     * @return Returns the now.
     */
    public Date getNow() {
        return now;
    }

    /**
     * @return Returns the past.
     */
    public Date getPast() {
        return past;
    }

    /**
     *
     * @return Returns the before date.
     */
    public Date getBefore() {
        return before;
    }

    /**
     *
     * @return Returns the after date.
     */
    public Date getAfter() {
        return after;
    }

    /**
     * @see com.opensymphony.xwork.ActionSupport#execute()
     */
    public String execute() throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
        now = cal.getTime();
        cal.roll(Calendar.DATE, -1);
        cal.roll(Calendar.HOUR, -3);
        past = cal.getTime();
        cal.roll(Calendar.DATE, 2);
        future = cal.getTime();

        cal.roll(Calendar.YEAR, -1);
        before = cal.getTime();

        cal.roll(Calendar.YEAR, 2);
        after = cal.getTime();
        return SUCCESS;
    }

}
