package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Script;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScriptTag extends ComponentTagSupport {

    protected String async;
    protected String charset;
    protected String defer;
    protected String src;
    protected String type;
    protected String referrerpolicy;
    protected String nomodule;
    protected String integrity;
    protected String crossorigin;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Script(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();
        Script script = ((Script) component);
        script.setAsync(async);
        script.setCharset(charset);
        script.setDefer(defer);
        script.setSrc(src);
        script.setType(type);
        script.setReferrerpolicy(referrerpolicy);
        script.setNomodule(nomodule);
        script.setIntegrity(integrity);
        script.setCrossorigin(crossorigin);
    }

    public void setAsync(String async) {
        this.async = async;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setDefer(String defer) {
        this.defer = defer;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    public void setNomodule(String nomodule) {
        this.nomodule = nomodule;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

}
