package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.DatePicker;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see DatePicker
 */
public class DatePickerDirective extends TextFieldDirective {
    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DatePicker(stack, req, res);
    }

    public String getBeanName() {
        return "datepicker";
    }
}
