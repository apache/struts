package org.apache.struts.webwork.views.freemarker.tags;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Token;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Token
 */
public class TokenModel extends TagModel {
    public TokenModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new Token(stack, req, res);
    }
}
