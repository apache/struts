package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.DatePicker;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see DatePicker
 */
public class DatePickerTag extends TextFieldTag {

    protected String language;
    protected String format;
    protected String showstime;
    protected String singleclick;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DatePicker(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        final DatePicker datePicker = (DatePicker) component;
        datePicker.setLanguage(language);
        datePicker.setFormat(format);
        datePicker.setShowstime(showstime);
        datePicker.setSingleclick(singleclick);
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setShowstime(String showstime) {
        this.showstime = showstime;
    }

    public void setSingleclick(String singleclick) {
        this.singleclick = singleclick;
    }
}
