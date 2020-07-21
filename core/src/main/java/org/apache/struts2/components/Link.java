package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Link extends UIBean{

    private static final String TEMPLATE="link";

    protected String href;
    protected String hreflang;
    protected String rel;
    protected String media;
    protected String referrerpolicy;
    protected String sizes;
    protected String crossorigin;
    protected String type;
    protected String as;
//    protected String disabled;
    protected String importance; //docs say importance is an experimental API TODO: should we include it
    protected String integrity; //experimental API too
//    protected String title;

    public Link(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @StrutsTagAttribute(description="HTML link href attribute")
    public void setHref(String href) {
        this.href = href;
    }

    @StrutsTagAttribute(description="HTML link hreflang attribute")
    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    @StrutsTagAttribute(description="HTML link rel attribute")
    public void setRel(String rel) {
        this.rel = rel;
    }

    @StrutsTagAttribute(description="HTML link sizes attribute")
    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    @StrutsTagAttribute(description="HTML link crossorigin attribute")
    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    @StrutsTagAttribute(description="HTML link type attribute")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="HTML link as attribute")
    public void setAs(String as) {
        this.as = as;
    }

    @StrutsTagAttribute(description="HTML link importance attribute")
    public void setImportance(String importance) {
        this.importance = importance;
    }

    @StrutsTagAttribute(description="HTML link integrity attribute")
    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    @StrutsTagAttribute(description="HTML link media attribute")
    public void setMedia(String media) {
        this.media = media;
    }

    @StrutsTagAttribute(description="HTML link referrerpolicy attribute")
    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (href != null) {
            addParameter("href", findString(href));
        }

        if (hreflang != null) {
            addParameter("hreflang", findString(hreflang));
        }

        if (rel != null) {
            addParameter("rel", findString(rel));
        }

        if (media != null) {
            addParameter("media", findString(media));
        }

        if (referrerpolicy != null) {
            addParameter("referrerpolicy", findString(referrerpolicy));
        }

        if (sizes != null) {
            addParameter("sizes", findString(sizes));
        }

        if (type != null) {
            addParameter("type", findString(type));
        }

        if (as != null) {
            addParameter("as", findString(as));
        }

        if (importance != null) {
            addParameter("importance", findString(importance));
        }

        if (integrity != null) {
            addParameter("integrity", findString(integrity));
        }

    }
}
