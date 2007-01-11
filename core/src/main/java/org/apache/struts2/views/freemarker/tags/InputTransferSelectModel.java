package org.apache.struts2.views.freemarker.tags;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.InputTransferSelect;

/**
 * @see org.apache.struts2.components.InputTransferSelect
 */
public class InputTransferSelectModel extends TagModel {

    public InputTransferSelectModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new InputTransferSelect(stack, req, res);
    }

}
