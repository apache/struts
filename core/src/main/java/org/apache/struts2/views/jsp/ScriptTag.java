package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Script;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScriptTag extends ComponentTagSupport {

    protected String async;
    protected String charset;
    protected String defer;
    protected String src;
    protected String type;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Script();
    }

    protected void populateParams() {
        super.populateParams();;
        Script script = ((Script) component);
        script.setAsync(async);
        script.setCharset(charset);
        script.setDefer(defer);
        script.setSrc(src);
        script.setType(type);
    }

    public void setAsync(String async) {
        this.async = async;
    }

}
