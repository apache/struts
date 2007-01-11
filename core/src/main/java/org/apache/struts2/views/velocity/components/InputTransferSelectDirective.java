package org.apache.struts2.views.velocity.components;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.InputTransferSelect;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see org.apache.struts2.components.InputTransferSelect
 */
public class InputTransferSelectDirective extends AbstractDirective {

    public String getBeanName() {
        return "inputtransferselect";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new InputTransferSelect(stack, req, res);
    }

}
