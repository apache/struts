package org.apache.struts2.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.TimePicker;

import com.opensymphony.xwork2.util.ValueStack;

public class TimePickerModel extends TagModel {

	public TimePickerModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		super(stack, req, res);
	}

	protected Component getBean() {
		return new TimePicker(stack, req, res);
	}

}
