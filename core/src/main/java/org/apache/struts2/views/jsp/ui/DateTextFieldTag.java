package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.DateTextField;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see DateTextField
 */
public class DateTextFieldTag extends AbstractUITag {

    private static final long serialVersionUID = 5811285953670562288L;

    protected String format;
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DateTextField(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        DateTextField textField = ((DateTextField) component);
        textField.setFormat(format);
    }

	public void setFormat(String format) {
		this.format = format;
	}

}
