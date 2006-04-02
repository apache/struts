package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.DatePicker;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see DatePicker
 */
public class DatePickerTag extends TextFieldTag {

	private static final long serialVersionUID = 4054114507143447232L;
	
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
