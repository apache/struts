package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@StrutsTag(name="script", tldTagClass="org.apache.struts2.views.jsp.ui.ScriptTag", description="TODO")
public class Script extends UIBean {

    protected String async;
    protected String charset;
    protected String defer;
    protected String src;
    protected String type;

    // TODO Sketchy
    private static final String TEMPLATE = "script";

    public Script(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @StrutsTagAttribute(description="HTML script async attribute")
    public void setAsync(String async) {
        this.async = async;
    }

    @StrutsTagAttribute(description="HTML script charset attribute")
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @StrutsTagAttribute(description="HTML script defer attribute")
    public void setDefer(String defer) {
        this.defer = defer;
    }

    @StrutsTagAttribute(description="HTML script src attribute")
    public void setSrc(String src) {
        this.src = src;
    }

    @StrutsTagAttribute(description="HTML script type attribute")
    public void setType(String type) {
        this.type = type;
    }

}
