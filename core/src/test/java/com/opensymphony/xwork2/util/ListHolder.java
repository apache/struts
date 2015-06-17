package com.opensymphony.xwork2.util;

import java.util.Date;
import java.util.List;

/**
 * User: patrick Date: Dec 20, 2005 Time: 11:15:29 AM
 */
public class ListHolder {
    List<Long> longs;
    List<String> strings;
    List<Date> dates;

    public List<Long> getLongs() {
        return longs;
    }

    public void setLongs(List<Long> longs) {
        this.longs = longs;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }
}
