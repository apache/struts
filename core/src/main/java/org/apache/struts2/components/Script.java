package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;


@StrutsTag(name="script",
        tldTagClass="org.apache.struts2.views.jsp.ui.ScriptTag",
        description="TODO",
        allowDynamicAttributes=true)
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

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (async != null) {
            addParameter("async", findString(async));
        }

        if (charset != null) {
            addParameter("charset", findString(charset));
        }

        if (defer != null) {
            addParameter("defer", findString(defer));
        }

        if (src != null) {
            addParameter("src", findString(src));
        }

        if (type != null) {
            addParameter("type", findString(type));
        }

        //TODO this portion was copied from Form.java - don't know if it's necessary for scripts
        // keep a collection of the tag names for anything special the templates might want to do (such as pure client
        // side validation)
        if (!parameters.containsKey("tagNames")) {
            // we have this if check so we don't do this twice (on open and close of the template)
            addParameter("tagNames", new ArrayList());
        }

    }

}
